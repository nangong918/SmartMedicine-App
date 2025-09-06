package com.czy.smartmedicine.fragment.message.children;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.czy.baseutil.activity.BaseFragment;
import com.czy.baseutil.viewModel.ViewModelUtil;
import com.czy.domain.fragmentActivityAo.MessageVo;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.databinding.FragmentMessageBinding;
import com.czy.smartmedicine.viewModel.base.ApiViewModelFactory;
import com.czy.smartmedicine.viewModel.fragment.MessageVm;


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
    public FragmentMessageBinding getBinding() {
        return FragmentMessageBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViewModel();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // 此处recyclerView才创建
        vm.initRecyclerView(
                binding.rclvMessage,
                requireActivity()
        );
    }

    @Override
    protected void setListener() {
        super.setListener();
    }

    //---------------------------viewModel---------------------------

    private MessageVm vm;

    private void initViewModel(){
        ApiViewModelFactory apiViewModelFactory = new ApiViewModelFactory(MainApplication.getApiRequestImplInstance(), MainApplication.getInstance().getMessageSender());
        vm = ViewModelUtil.newViewModel(this, apiViewModelFactory, MessageVm.class);

        vm.init(new MessageVo());

//        // 绑定viewModel
//        binding.setViewModel(signViewModel);
//        // 设置监听者
//        binding.setLifecycleOwner(this);

        observeData();
    }

    private void observeData() {
        // 观察RecyclerView
/*        Optional.ofNullable(viewModel)
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
                });*/
    }

    @Override
    public void onPause() {
        super.onPause();
        if (vm != null){
            vm.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (vm != null){
            vm.onDestroy();
        }
    }
}