package com.czy.smartmedicine.viewModel.fragment;


import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.czy.appcore.network.netty.api.receive.ChatApiHandler;
import com.czy.appcore.network.netty.api.send.SocketMessageSender;
import com.czy.appcore.service.chat.ChatMessageManager;
import com.czy.appcore.service.chat.OnRecentContactMessageChange;
import com.czy.baseUtilsLib.date.DateUtils;
import com.czy.baseUtilsLib.network.BaseResponse;
import com.czy.customviewlib.view.chatCard.ChatContactAdapter;
import com.czy.dal.OnPositionItemClick;
import com.czy.dal.ao.chat.ChatActivityStartAo;
import com.czy.dal.ao.chat.ChatContactItemAo;
import com.czy.dal.bo.UserChatLastViewMessageBo;
import com.czy.dal.constant.NettyConstants;
import com.czy.dal.dto.http.request.BaseHttpRequest;
import com.czy.dal.dto.netty.forwardMessage.GroupTextDataResponse;
import com.czy.dal.dto.netty.forwardMessage.UserImageResponse;
import com.czy.dal.dto.netty.forwardMessage.UserTextDataResponse;
import com.czy.dal.dto.netty.request.HaveReadMessageRequest;
import com.czy.dal.dto.netty.response.HaveReadMessageResponse;
import com.czy.dal.dto.netty.response.UserNewMessageResponse;
import com.czy.dal.vo.entity.message.ChatContactListVo;
import com.czy.dal.vo.fragmentActivity.MessageVo;
import com.czy.datalib.networkRepository.ApiRequestImpl;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.activity.ChatActivity;
import com.czy.smartmedicine.fragment.MessageFragment;
import com.czy.smartmedicine.manager.HttpRequestManager;
import com.czy.smartmedicine.utils.ViewModelUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


public class MessageVm extends ViewModel {

    private static final String TAG = MessageVm.class.getName();

    private final ApiRequestImpl apiRequestImpl;
    private final SocketMessageSender socketMessageSender;

    public MessageVm(ApiRequestImpl apiRequestImpl, SocketMessageSender socketMessageSender) {
        this.apiRequestImpl = apiRequestImpl;
        this.socketMessageSender = socketMessageSender;
    }


    public void init(MessageVo messageVo){
        this.messageVo = messageVo;
        initSocketReceiver();
        initialNetworkRequest();

        MainApplication.getInstance().getChatMessageManager().setOnRecentContactMessageChange(
                getOnRecentContactMessageChange()
        );
    }

    private OnRecentContactMessageChange getOnRecentContactMessageChange(){
        return list -> {
            // 先赋值
            messageVo.chatContactListVo.chatContactList = list;
            // 更新ui
            chatContactAdapter.setCurrentList(
                    messageVo.chatContactListVo.chatContactList
            );
        };
    }

    //---------------------------Vo Ld---------------------------

    public MessageVo messageVo = new MessageVo();

    public ChatContactAdapter chatContactAdapter;

    public void initRecyclerView(@NonNull RecyclerView recyclerView, @NonNull FragmentActivity activity){
        // 初始化Adapter
        this.chatContactAdapter = new ChatContactAdapter(
                getOnPositionClickListener(activity)
        );

        // 绑定Adapter
        recyclerView.setAdapter(chatContactAdapter);

        // 初始化view
        chatContactAdapter.setCurrentList(messageVo.chatContactListVo.chatContactList);
    }

    private OnPositionItemClick getOnPositionClickListener(@NonNull FragmentActivity activity){
        // TODO 点击之后netty通知后端消息已读
        return position -> {
            ChatContactListVo recyclerViewVo = Optional.ofNullable(messageVo)
                    .map(vo -> vo.chatContactListVo)
                    .orElse(new ChatContactListVo());

            // 获取ChatActivity启动Ao
            ChatActivityStartAo chatActivityStartAo = ChatActivityStartAo.getStartAoByChatContactItemAo(
                    Optional.ofNullable(recyclerViewVo.chatContactList)
                            .map(list -> list.get(position))
                            .orElse(new ChatContactItemAo())
                    ,
                    ""
            );

            // 归零未读
            List<ChatContactItemAo> list = recyclerViewVo.chatContactList;
            list.get(position).chatContactItemVo.unreadCount = 0;
            // 通知后端那一条被读了
            Long userId = list.get(position).userId;
            socketHaveReadMessage(userId);
            // ui 更新
//            messageVo.chatContactListVo.chatContactListLd.postValue(list);
            chatContactAdapter.setCurrentList(list);

            // 启动activity
            // 启动ChatActivity
            Intent intent = new Intent(activity, ChatActivity.class);
            intent.putExtra(ChatActivityStartAo.class.getName(), chatActivityStartAo);
            activity.startActivity(intent);
        };
    }

    ;
    // 本地消息也要展示在List中 (以后有空闲的没事干再加入sqlite[估计做其他项目去了，懒得做这个项目])

    //---------------------------NetWork---------------------------

    private ChatApiHandler chatApiHandler;

    // 消息队列：如果因为List是一个唯一资源，多线程情况下应该上锁，而上锁会导致阻塞或者CPU繁忙，应该将全部的消息交给消息队列处理；避免重要线程被普通任务阻塞
    private final Handler messageHandler = new Handler(Looper.getMainLooper());

    private void initSocketReceiver(){
        initEventBus();
        chatApiHandler = new ChatApiHandler() {
            @Override
            public void receiveUserText(@NonNull UserTextDataResponse response) {
                // socket消息交给消息队列同步等待处理
                messageHandler.post(() -> {
                    processUserTextDataResponse(response);
                });
            }

            @Override
            public void receiveGroupText(@NonNull GroupTextDataResponse response) {
                // 暂时不做
            }

            @Override
            public void haveReadMessage(@NonNull HaveReadMessageResponse response) {
                // 已读
            }

            @Override
            public void receiveUserImage(@NonNull UserImageResponse response) {
                messageHandler.post(() -> {
                    processUserImageResponse(response);
                });
            }
        };
    }

    // 消息获取：外存获取，Http网络获取，Socket获取 -> 内存

    private void processUserTextDataResponse(UserTextDataResponse response){
        Log.d(TAG, "receiveUserText: " + response.toJsonString());
        // contactAccount存在的话就更新RecyclerView(LiveData)
        // 不存在的话就添加一条到LiveData然后插入RecyclerView
        // 更新RecyclerView LiveData 和 DiffUtil
        // 非 contactAccount 作为索引方案

        ChatContactItemAo item = new ChatContactItemAo();
        item.contactAccount = Optional.ofNullable(response.account).orElse("");
        // 应该替换为本地url加载
        item.chatContactItemVo.avatarUrlOrUri = response.avatarUrls;
        item.chatContactItemVo.name = response.senderName;
        item.userId = response.senderId;
        item.chatContactItemVo.setMessagePreview(response.getContent());
        item.timestamp = Optional.ofNullable(response.timestamp)
                .map(t -> {
                    try {
                        return Long.parseLong(t);
                    } catch (Exception e){
                        Log.w(TAG, "时间戳转化出错" + e);
                        return System.currentTimeMillis();
                    }
                })
                .orElse(System.currentTimeMillis());
        item.index = item.timestamp;
        item.chatContactItemVo.time = DateUtils.getTime(new Date(item.timestamp));
        List<ChatContactItemAo> list = new ArrayList<>();
        list.add(item);
        // 同步设置
        ChatMessageManager chatMessageManager = MainApplication.getInstance().getChatMessageManager();
        chatMessageManager.cacheMessage(list);

        // 存在contactAccount 暂时取消方案，Map索引数据结构在ChatList中先实现再说
//                ChatContactItemAo contactItemAo = messageVo.chatContactListVo.findContactByAccount(contactAccount);
//                if (contactItemAo != null){
//                    contactItemAo.chatContactItemVo.setMessagePreview(response.getContent());
//                    long time = Long.parseLong(response.timestamp);
//                    Date date = new Date(time);
//                    contactItemAo.chatContactItemVo.time = (DateUtils.getTime(date));
//                    contactItemAo.chatContactItemVo.unreadCount = (contactItemAo.chatContactItemVo.unreadCount + 1);
//                    contactItemAo.chatContactItemVo.name = (response.senderName);
//                    messageVo.chatContactListVo.updateContact(contactAccount, contactItemAo);
//                }
//                else {
//                    contactItemAo = new ChatContactItemAo();
//                    contactItemAo.contactAccount = contactAccount;
//                    contactItemAo.chatContactItemVo.setMessagePreview(response.getContent());
//                    long time = Long.parseLong(response.timestamp);
//                    Date date = new Date(time);
//                    contactItemAo.chatContactItemVo.time = (DateUtils.getTime(date));
//                    contactItemAo.chatContactItemVo.unreadCount = (1);
//                    contactItemAo.chatContactItemVo.name = (response.senderName);
//                    messageVo.chatContactListVo.addContact(contactItemAo);
//                }
        // TODO 消息弹窗提示
    }

    private void processUserImageResponse(UserImageResponse response) {
        Log.d(TAG, "receiveUserImage: " + response.toJsonString());
        String contactAccount = response.account == null ? "" : response.account;
        ChatContactItemAo item = new ChatContactItemAo();
        item.contactAccount = contactAccount;
        item.userId = response.senderId;
        item.timestamp = Optional.ofNullable(response.timestamp)
                .map(t -> {
                    try {
                        return Long.parseLong(t);
                    } catch (Exception e){
                        Log.w(TAG, "时间戳转化出错" + e);
                        return System.currentTimeMillis();
                    }
                })
                .orElse(System.currentTimeMillis());
//        item.chatContactItemVo.avatarUrlOrUri = response.avatarUrls;
        item.chatContactItemVo.name = response.senderName;
        // 包含消息裁剪功能
        item.chatContactItemVo.setMessagePreview("图片消息");
        item.chatContactItemVo.time = DateUtils.getTime(new Date(item.timestamp));
        item.chatContactItemVo.unreadCount = 0;

        item.index = item.timestamp;

        List<ChatContactItemAo> list = new ArrayList<>();
        list.add(item);
        // 同步设置
        ChatMessageManager chatMessageManager = MainApplication.getInstance().getChatMessageManager();
        chatMessageManager.cacheMessage(list);
    }

    private void initialNetworkRequest(){
        // 首次打开：Http请求
        if (HttpRequestManager.getIsFirstOpen(MessageFragment.class.getName())){
            BaseHttpRequest request = new BaseHttpRequest();
            request.senderId = Optional.ofNullable(MainApplication.getInstance().getUserLoginInfoAo())
                            .map(ao -> ao.userId)
                            .orElse(NettyConstants.ERROR_ID);
            if (NettyConstants.ERROR_ID.equals(request.senderId)){
                Log.w(TAG, "doGetUserNewMessage: senderId is empty");
                return;
            }
            doGetUserNewMessage(request);
        }
        // 非首次打开，读取内存数据
        else {
            // 从缓存获取数据
            messageVo.chatContactListVo.chatContactList = Optional.ofNullable(MainApplication.getInstance().getChatMessageManager())
                    .map(ChatMessageManager::getRecentContactMessages)
                    .orElse(new ArrayList<>());

            // 通知adapter更新view
            // 创建 Handler
            Handler handler = new Handler(Looper.getMainLooper());

            // 定义 Runnable
            Runnable checkAdapterRunnable = new Runnable() {
                @Override
                public void run() {
                    if (chatContactAdapter != null){
                        // 更新 UI
                        chatContactAdapter.setCurrentList(messageVo.chatContactListVo.chatContactList);
                        Log.i(TAG, "chatContactAdapter is not null, 刷新ui");
                    }
                    else{
                        // 如果 chatContactAdapter 仍然为 null，300 毫秒后继续检查
                        Log.i(TAG, "chatContactAdapter is null 继续等待300");
                        handler.postDelayed(this, 300);
                    }
                }
            };

            // 开始检查
            handler.post(checkAdapterRunnable);
        }
    }

    //==========已读，socket通知service

    public void socketHaveReadMessage(Long contactId){
        HaveReadMessageRequest request = new HaveReadMessageRequest(contactId);
        socketMessageSender.readMessage(request);
    }

    //==========主动获取全部好友的最新消息  当且仅当断开重连之后调用此方法

    private void doGetUserNewMessage(BaseHttpRequest request){
        apiRequestImpl.getUserNewMessage(
                request,
                this::handleGetUserNewMessage,
                throwable -> {
                    Log.i(TAG, "getUserNewMessage error: " + throwable);
                    ViewModelUtil.globalThrowableToast(throwable);
                }
        );
    }

    private void handleGetUserNewMessage(BaseResponse<UserNewMessageResponse> response){
        if (ViewModelUtil.handleResponse(response)){
            // Bo -> Ao
            List<ChatContactItemAo> chatContactList = new ArrayList<>();
            for (UserChatLastViewMessageBo lastMessageBo : response.getData().lastMessageList) {
                ChatContactItemAo ao = new ChatContactItemAo();
                // view: ChatContactItemVo
                // 头像 URL
                ao.chatContactItemVo.avatarUrlOrUri =
                        lastMessageBo.friendViewEntity.avatarUrl;
                // name = name + (备注) ? 备注 : account
                ao.chatContactItemVo.name = getFinalName(lastMessageBo);
                ao.chatContactItemVo.messagePreview = lastMessageBo.msgContent;
                ao.chatContactItemVo.time = DateUtils.getTime(
                        new Date(
                                Optional.ofNullable(lastMessageBo.timestamp)
                                        .orElse(System.currentTimeMillis())
                        )
                );
                ao.chatContactItemVo.unreadCount = lastMessageBo.unreadCount;
                // data
                ao.contactAccount = lastMessageBo.friendViewEntity.userAccount;
                ao.userId = lastMessageBo.friendViewEntity.userId;
                ao.timestamp = lastMessageBo.timestamp;

                chatContactList.add(ao);
            }

            // 同步设置
            ChatMessageManager chatMessageManager = MainApplication.getInstance().getChatMessageManager();
            chatMessageManager.cacheMessage(chatContactList);
        }
    }

    // name = name + (备注) ? 备注 : account
    private String getFinalName(UserChatLastViewMessageBo bo) {
        String name = Optional.ofNullable(bo.friendViewEntity)
                .map(f -> f.userName)
                .orElse("");
        String remark = Optional.ofNullable(bo.friendViewEntity)
                .map(f -> f.remark)
                .orElse("");
        String finalName = Optional.ofNullable(bo.friendViewEntity)
                .map(f -> f.userAccount)
                .orElse("");
        if (!TextUtils.isEmpty(name)){
            if (!TextUtils.isEmpty(remark)){
                finalName = name + "(" + remark + ")";
            }
            else {
                finalName = name;
            }
        }
        else {
            if (!TextUtils.isEmpty(name)){
                finalName = name;
            }
        }
        return finalName;
    }

    //---------------------------EventBus---------------------------

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageReceived(UserTextDataResponse response) {
        if (response != null){
            chatApiHandler.receiveUserText(response);
        }
        // 移除已处理的粘性事件
        EventBus.getDefault().removeStickyEvent(response);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageReceived(GroupTextDataResponse response) {
        if (response != null){
            chatApiHandler.receiveGroupText(response);
        }
        // 移除已处理的粘性事件
        EventBus.getDefault().removeStickyEvent(response);
    }

    private void initEventBus() {
        EventBus.getDefault().register(this);
    }

    private void unInitEventBus() {
        EventBus.getDefault().unregister(this);
    }

    //---------------------------logic---------------------------

    ;
    // 存储List
//    public void storage(){
//        MainApplication.getInstance().chatContactList = Optional.ofNullable(messageVo)
//                .map(mvo -> mvo.chatContactListVo)
//                .map(cvo -> cvo.chatContactList)
////                .map(LiveData::getValue)
//                .orElse(new ArrayList<>());
//    }

    public void onPause() {
//        storage();
    }

    public void onDestroy() {
//        storage();
        unInitEventBus();
    }
}
