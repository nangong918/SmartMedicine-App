package com.czy.smartmedicine.fragment;

import android.content.Intent;
import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.czy.baseUtilsLib.activity.BaseFragment;
import com.czy.baseUtilsLib.viewModel.ViewModelUtil;
import com.czy.customviewlib.view.chatCard.ChatContactAdapter;
import com.czy.dal.ao.chat.ChatActivityStartAo;
import com.czy.dal.ao.chat.ChatContactItemAo;
import com.czy.dal.vo.entity.message.ChatContactListVo;
import com.czy.dal.vo.viewModelVo.message.MessageVo;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.activity.ChatActivity;
import com.czy.smartmedicine.databinding.FragmentMessageBinding;
import com.czy.smartmedicine.viewModel.ApiViewModelFactory;
import com.czy.smartmedicine.viewModel.MessageViewModel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


/**
 * @author 13225
 * im系统前端UI，前端数据存储设计，后端数据存储设计，前端实时通讯，后端实时通讯；python批量导入测试数据
 * 1.RecyclerView展示聊天消息列表
 *    1.1.列表消息基本Item
 *    1.2.聊天列表RecyclerView
 * 2.聊天Activity
 *    2.1.聊天界面RecyclerView
 *    2.2.聊天界面输入框
 *    2.3.聊天记录：
 *      2.3.1.聊天记录本地存取（SQLite记录聊天记录所属+文件[加密]+分页查询）
 *      2.3.2.聊天记录服务器存取 （MySQL记录聊天记录所属+MongoDB[分页查询]+文件服务）
 * 3.双人互聊IM通讯
 *    3.1.Android端WebSocket连接
 *    3.2.Spring端WebSocket连接
 *    3.3.Spring端WebSocket消息转发 + Redis消息队列 + 消息推送
 *    3.4.Android端WebSocket消息接收 + 显示消息
 * <p>
 * 信息推送：FirebaseMessagingService
 * 消息长连接：WebSocket
 * 音视频推流：WebRTC
 */
public class MessageFragment extends BaseFragment<FragmentMessageBinding> {


    public MessageFragment() {
        super(MessageFragment.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void init() {
        super.init();
        initViewModel();
        initRecyclerView();
    }

    @Override
    protected void setListener() {
        super.setListener();
    }

    //---------------------------viewModel---------------------------

    private MessageViewModel viewModel;

    private void initViewModel(){
        ApiViewModelFactory apiViewModelFactory = new ApiViewModelFactory(MainApplication.getApiRequestImplInstance(), MainApplication.getInstance().getMessageSender());
        viewModel = ViewModelUtil.newViewModel(this, apiViewModelFactory, MessageViewModel.class);

        initViewModelVo();

//        // 绑定viewModel
//        binding.setViewModel(signViewModel);
//        // 设置监听者
//        binding.setLifecycleOwner(this);

        observeData();
    }

    private void initViewModelVo(){
        MessageVo messageVo = new MessageVo();

        List<ChatContactItemAo> list = new ArrayList<>();
        messageVo.chatContactListVo.chatContactListLd.setValue(list);

        viewModel.init(messageVo);
    }

    private void observeData() {
        // 观察RecyclerView
        Optional.ofNullable(viewModel)
                .map(vm -> vm.messageVo)
                .map(mvo -> mvo.chatContactListVo)
                .map(cvo -> cvo.chatContactListLd)
                .ifPresent(liveData -> {
                    liveData.observe(this, newList -> {
                        Optional.ofNullable(((ChatContactAdapter)binding.rclvMessage.getAdapter()))
                                .ifPresent(chatContactAdapter -> {
                                    chatContactAdapter.setCurrentList(newList);
                                });
                    });
                });
    }

    //-----------------------RecyclerView-----------------------

    private void initRecyclerView() {
        // TODO 将test改为获取数据资源 :0.聊天Activity，1.先不要持久化数据，全部从网络获取；2.添加好友功能 3.WebSocket前后端发送消息 4.FirebaseMessagingService消息推送 5.好友前端持久化 6.聊天记录前后端持久化
        ChatContactListVo recyclerViewVo = Optional.ofNullable(viewModel.messageVo)
                .map(vo -> vo.chatContactListVo)
                .orElse(new ChatContactListVo());
        ChatContactAdapter adapter = new ChatContactAdapter(
                recyclerViewVo.chatContactListLd.getValue(),
                position -> {
                    // 启动Ao
                    ChatActivityStartAo chatActivityStartAo = getChatActivityStartAo(
                            Optional.ofNullable(recyclerViewVo.chatContactListLd.getValue())
                                    .map(list -> list.get(position))
                                    .orElse(new ChatContactItemAo())
                    );

                    // 归零未读
                    List<ChatContactItemAo> list = recyclerViewVo.chatContactListLd.getValue();
                    list.get(position).chatContactItemVo.unreadCount = 0;
                    // 通知后端那一条被读了
                    String contactAccount = list.get(position).contactAccount;
                    viewModel.socketHaveReadMessage(contactAccount);
                    // 通知观察者调用 chatContactAdapter.setCurrentList(newList)
                    viewModel.messageVo.chatContactListVo.chatContactListLd.postValue(list);

                    // 启动ChatActivity
                    Intent intent = new Intent(requireActivity(), ChatActivity.class);
                    intent.putExtra(ChatActivityStartAo.class.getName(), chatActivityStartAo);
                    startActivity(intent);
        });
        binding.rclvMessage.setAdapter(adapter);
    }

    private ChatActivityStartAo getChatActivityStartAo(ChatContactItemAo chatContactItemAo){
        ChatActivityStartAo chatActivityStartAo = new ChatActivityStartAo();
        chatActivityStartAo.contactAccount = chatContactItemAo.contactAccount;
        chatActivityStartAo.contactName = chatContactItemAo.chatContactItemVo.name;
        chatActivityStartAo.chatMessageListItemVo = new LinkedList<>();
        // TODO 从网络或者本地获取聊天记录
        return chatActivityStartAo;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (viewModel != null){
            viewModel.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (viewModel != null){
            viewModel.onDestroy();
        }
    }
}