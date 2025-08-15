package com.czy.smartmedicine.activity;


import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.czy.baseUtilsLib.activity.BaseActivity;
import com.czy.baseUtilsLib.image.ImageLoadUtil;
import com.czy.baseUtilsLib.ui.ToastUtils;
import com.czy.baseUtilsLib.viewModel.ViewModelUtil;
import com.czy.dal.ao.chat.ChatActivityStartAo;
import com.czy.dal.fragmentActivityAo.chat.ChatVo;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.databinding.ActivityChatBinding;
import com.czy.smartmedicine.viewModel.activity.ChatVm;
import com.czy.smartmedicine.viewModel.base.ApiViewModelFactory;

import java.util.Optional;

/**
 * @author 13225
 * 聊天界面
 */
public class ChatActivity extends BaseActivity<ActivityChatBinding> {

    public ChatActivity() {
        super(ChatActivity.class);
    }

    @Override
    public ActivityChatBinding getBinding() {
        return ActivityChatBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void init() {
        super.init();

        Log.i("check_netty", "ChatActivity::MessageSender: " + MainApplication.getInstance().getMessageSender());

        initIntentData();
        initViewModel();

        // 初始化聊天数据请求
        vm.initialNetworkRequest(
                vm.chatVo.contactId
        );
    }

    @Override
    protected void setListener() {
        super.setListener();

        binding.imgvBack.setOnClickListener(v -> finish());

        binding.smSendMessage.setSendClickListener(v -> {
            vm.sendMessage();
            binding.smSendMessage.setEditMessage("");
        });

        // 图片消息：1. 选择图片
        binding.smSendMessage.setImgClickListener(v ->
                vm.beginSelectPicture(this)
        );
    }

    //-----------------------Intent Data-----------------------

    private ChatActivityStartAo startAo;

    private void initIntentData() {
        // 获取传递的对象
        try {
            Intent intent = getIntent();
            Optional.ofNullable(intent)
                    .map(i -> (ChatActivityStartAo)i.getSerializableExtra(
                            ChatActivityStartAo.class.getName()
                    ))
                    .ifPresent(ao -> {
                        this.startAo = ao;
                    });
        } catch (Exception e) {
            Log.d(TAG, "initIntentData::get ChatActivityStartAo SerializableExtra Error: ", e);
            ToastUtils.showToast(this, "获取聊天对象失败");
            finish();
        }
    }


    //-----------------------ViewModel-----------------------

    private ChatVm vm;

    private void initViewModel(){
        ApiViewModelFactory apiViewModelFactory = new ApiViewModelFactory(MainApplication.getApiRequestImplInstance(), MainApplication.getInstance().getMessageSender());
        vm = ViewModelUtil.newViewModel(this, apiViewModelFactory, ChatVm.class);

        initViewModelVo();

        // 初始化图片选择器
        vm.initPictureSelectorLauncher(this);

        // 初始化recyclerView
        vm.initRecyclerView(binding.rclvMessage);
    }

    private void initViewModelVo(){
        ChatVo chatVo = new ChatVo();
        chatVo.contactId = startAo.contactId;

        vm.setStartAo(startAo);
        vm.init(chatVo);

        // 观察数据
        observeData();

        binding.setViewModel(vm);
        binding.setLifecycleOwner(this);
    }

    private void observeData(){
        // 标题
        Optional.ofNullable(vm)
                .map(vm -> vm.chatVo)
                .map(cvo -> cvo.name)
                .ifPresent(liveData -> {
                    liveData.observe(this, newName -> {
                        binding.tvTitle.setText(newName);
                    });
                });
        // 头像
        Optional.ofNullable(vm)
                .map(vm -> vm.chatVo)
                .map(cvo -> cvo.avatarUrlOrUri)
                .ifPresent(liveData -> {
                    liveData.observe(this, newAvatarUrlOrUri -> {
                        if (!TextUtils.isEmpty(newAvatarUrlOrUri)) {
                            ImageLoadUtil.loadImageViewByNetWork(
                                    newAvatarUrlOrUri,
                                    binding.imgvCircle
                            );
                        }
                    });
                });
        // 输入框
//        Optional.ofNullable(viewModel)
//                .map(vm -> vm.chatVo)
//                .map(cvo -> cvo.inputText)
//                .ifPresent(liveData -> {
//                    liveData.observe(this, newText -> {
//                        Log.i(TAG, "initViewModelVo::inputText: " + newText);
//                        if (newText != null){
//                            binding.smSendMessage.setEditMessage(newText);
//                        }
//                    });
//                });
    }


    @Override
    protected void onPause() {
        super.onPause();
        vm.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        vm.onDestroy();
    }
}