package com.czy.smartmedicine.viewModel.fragment;


import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.czy.appcore.network.netty.api.receive.ChatApiHandler;
import com.czy.appcore.network.netty.api.send.SocketMessageSender;
import com.czy.baseUtilsLib.date.DateUtils;
import com.czy.baseUtilsLib.network.BaseResponse;
import com.czy.dal.ao.chat.ChatContactItemAo;
import com.czy.dal.bo.UserChatLastMessageBo;
import com.czy.dal.constant.Constants;
import com.czy.dal.dto.http.request.BaseHttpRequest;
import com.czy.dal.dto.netty.forwardMessage.GroupTextDataResponse;
import com.czy.dal.dto.netty.forwardMessage.UserImageResponse;
import com.czy.dal.dto.netty.forwardMessage.UserTextDataResponse;
import com.czy.dal.dto.netty.request.HaveReadMessageRequest;
import com.czy.dal.dto.netty.response.HaveReadMessageResponse;
import com.czy.dal.dto.netty.response.UserNewMessageResponse;
import com.czy.dal.vo.fragmentActivity.MessageVo;
import com.czy.datalib.networkRepository.ApiRequestImpl;
import com.czy.smartmedicine.MainApplication;
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
import java.util.concurrent.atomic.AtomicBoolean;

public class MessageViewModel extends ViewModel {

    private static final String TAG = MessageViewModel.class.getName();

    private final ApiRequestImpl apiRequestImpl;
    private final SocketMessageSender socketMessageSender;

    public MessageViewModel(ApiRequestImpl apiRequestImpl, SocketMessageSender socketMessageSender) {
        this.apiRequestImpl = apiRequestImpl;
        this.socketMessageSender = socketMessageSender;
    }

    public MessageVo messageVo = new MessageVo();

    public void init(MessageVo messageVo){
        initVo(messageVo);
        initReceiveMessageApi();
        initialNetworkRequest();
    }

    private void initVo(MessageVo messageVo){
        this.messageVo = messageVo;
    }

    //---------------------------Vo Ld---------------------------

    ;
    // 本地消息也要展示在List中

    //---------------------------NetWork---------------------------

    private ChatApiHandler chatApiHandler;

    // 消息队列：如果因为List是一个唯一资源，多线程情况下应该上锁，而上锁会导致阻塞或者CPU繁忙，应该将全部的消息交给消息队列处理；避免重要线程被普通任务阻塞
    private final Handler messageHandler = new Handler(Looper.getMainLooper());

    private void initReceiveMessageApi(){
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

    // TODO 消息获取：外存获取，内存获取，Http网络获取，Socket获取

    private void processUserTextDataResponse(UserTextDataResponse response){
        Log.d(TAG, "receiveUserText: " + response.toJsonString());
        // TODO 匹配account -> 更新 messageList 消息气泡
        // contactAccount存在的话就更新RecyclerView(LiveData)
        // 不存在的话就添加一条到LiveData然后插入RecyclerView
        // 更新RecyclerView LiveData 和 DiffUtil
        // 非 contactAccount 作为索引方案

        String contactAccount = response.account == null ? "" : response.account;
        ChatContactItemAo item = new ChatContactItemAo();
        item.contactAccount = contactAccount;
        item.chatContactItemVo.avatarUrlOrUri = response.avatarUrl;
        item.chatContactItemVo.name = response.senderName;
        item.userId = response.senderId;
        item.chatContactItemVo.setMessagePreview(response.getContent());
        item.chatContactItemVo.time = (DateUtils.getTime(new Date(Long.parseLong(response.timestamp))));
        List<ChatContactItemAo> list = new ArrayList<>();
        list.add(item);
        handleUserChatLastMessage(list);

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
        item.chatContactItemVo.avatarUrlOrUri = response.avatarUrl;
        item.chatContactItemVo.name = response.senderName;
        item.chatContactItemVo.setMessagePreview("图片消息");
        item.chatContactItemVo.time = (DateUtils.getTime(new Date(Long.parseLong(response.timestamp))));
        List<ChatContactItemAo> list = new ArrayList<>();
        list.add(item);
        handleUserChatLastMessage(list);
    }

    private void initialNetworkRequest(){
        // 首次打开：Http请求
        if (HttpRequestManager.getIsFirstOpen(MessageFragment.class.getName())){
            BaseHttpRequest request = new BaseHttpRequest();
            request.senderId = Optional.ofNullable(MainApplication.getInstance().getUserLoginInfoAo())
                            .map(ao -> ao.userId)
                            .orElse(Constants.ERROR_ID);
            if (Constants.ERROR_ID.equals(request.senderId)){
                Log.w(TAG, "doGetUserNewMessage: senderId is empty");
                return;
            }
            doGetUserNewMessage(request);
        }
        // 非首次打开，读取内存数据
        else {
            List<ChatContactItemAo> cacheList = Optional.ofNullable(MainApplication.getInstance().chatContactList)
                    .orElse(new ArrayList<>());
            messageVo.chatContactListVo.chatContactListLd.postValue(cacheList);
        }
    }

    //==========已读，socket通知service

    public void socketHaveReadMessage(String contactAccount){
        HaveReadMessageRequest request = new HaveReadMessageRequest(contactAccount);
        socketMessageSender.readMessage(request);
    }

    //==========主动获取全部好友的最新消息  当且仅当断开重连之后调用此方法

    private void doGetUserNewMessage(BaseHttpRequest request){
        apiRequestImpl.getUserNewMessage(
                request,
                this::handleGetUserNewMessage,
                ViewModelUtil::globalThrowableToast
        );
    }

    private void handleGetUserNewMessage(BaseResponse<UserNewMessageResponse> response){
        if (ViewModelUtil.handleResponse(response)){
            // Bo -> Ao
            List<ChatContactItemAo> chatContactList = new ArrayList<>();
            for (UserChatLastMessageBo lastMessageBo : response.getData().lastMessageList) {
                ChatContactItemAo chatContactItemAo = new ChatContactItemAo();
                chatContactItemAo.chatContactItemVo.messagePreview = (lastMessageBo.msgContent);
                chatContactItemAo.chatContactItemVo.time = (DateUtils.getTime(new Date(lastMessageBo.timestamp)));
                chatContactItemAo.chatContactItemVo.unreadCount = (lastMessageBo.unreadCount);
                chatContactItemAo.contactAccount = lastMessageBo.senderAccount;
                // 暂时不设置avatarUrlOrUri，而是从MessageFragment传递
//                chatContactItemAo.chatContactItemVo.avatarUrlOrUri = (lastMessageBo.contactPhoto);
                chatContactItemAo.chatContactItemVo.name = TextUtils.isEmpty(lastMessageBo.receiverName) ? lastMessageBo.receiverAccount : lastMessageBo.receiverName;
                chatContactList.add(chatContactItemAo);
            }
            // 同步设置
            messageHandler.post(() -> {
                handleUserChatLastMessage(chatContactList);
            });
        }
    }

    private synchronized void handleUserChatLastMessage(List<ChatContactItemAo> list){
        if (list == null || list.isEmpty()){
            return;
        }
        List<ChatContactItemAo> chatContactList = messageVo.chatContactListVo.chatContactListLd.getValue();
        if (chatContactList == null || chatContactList.isEmpty()){
            messageVo.chatContactListVo.chatContactListLd.setValue(new ArrayList<>());
            chatContactList = messageVo.chatContactListVo.chatContactListLd.getValue();
            chatContactList.addAll(list);
        }
        else {
            for (ChatContactItemAo item : list){
                AtomicBoolean isExist = new AtomicBoolean(false);
                for (int i = 0; i < chatContactList.size(); i++){
                    ChatContactItemAo chatContactItemAo = chatContactList.get(i);
                    if (chatContactItemAo.contactAccount.equals(item.contactAccount)){
                        ChatContactItemAo ao = new ChatContactItemAo(item);
                        ao.chatContactItemVo.unreadCount =
                                chatContactItemAo.chatContactItemVo.unreadCount + 1;
                        chatContactList.remove(i);
                        chatContactList.add(0, ao);
                        isExist.set(true);
                        break;
                    }
                }
                if (!isExist.get()) {
                    ChatContactItemAo ao = new ChatContactItemAo(item);
                    ao.chatContactItemVo.unreadCount = 1;
                    chatContactList.add(0, ao);
                }
            }
        }
        // 更新 LiveData
        messageVo.chatContactListVo.chatContactListLd.postValue(chatContactList);
    }

    //---------------------------EventBus---------------------------

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageReceived(UserTextDataResponse response) {
        if (response != null){
            chatApiHandler.receiveUserText(response);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageReceived(GroupTextDataResponse response) {
        if (response != null){
            chatApiHandler.receiveGroupText(response);
        }
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
    public void storage(){
        MainApplication.getInstance().chatContactList = Optional.ofNullable(messageVo)
                .map(mvo -> mvo.chatContactListVo)
                .map(cvo -> cvo.chatContactListLd)
                .map(LiveData::getValue)
                .orElse(new ArrayList<>());
    }

    public void onPause() {
        storage();
    }

    public void onDestroy() {
//        storage();
        unInitEventBus();
    }
}
