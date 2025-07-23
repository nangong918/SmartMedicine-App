package com.czy.smartmedicine.activity;

import android.content.Intent;
import android.util.Log;

import com.czy.baseUtilsLib.activity.BaseActivity;
import com.czy.baseUtilsLib.viewModel.ViewModelUtil;
import com.czy.dal.ao.chat.ChatActivityStartAo;
import com.czy.dal.ao.userBrief.UserBriefStartAo;
import com.czy.dal.vo.fragmentActivity.UserBriefVo;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.databinding.ActivityUserBriefBinding;
import com.czy.smartmedicine.viewModel.base.ApiViewModelFactory;
import com.czy.smartmedicine.viewModel.activity.UserBriefViewModel;

import java.util.Optional;

/**
 * 用户简介主页（点击头像之后的朋友圈页面）
 */
public class UserBriefActivity extends BaseActivity<ActivityUserBriefBinding> {

    public UserBriefActivity() {
        super(UserBriefActivity.class);
    }

    @Override
    protected void init() {
        super.init();

        initViewModel();
        initIntentData();
    }

    private void initIntentData(){
        Intent intent = getIntent();
        try {
            UserBriefStartAo ao = (UserBriefStartAo)intent.getSerializableExtra(UserBriefStartAo.class.getName());
            Optional.ofNullable(ao)
                    .ifPresent(a -> {
                        // 设置view数据给viewModel
                        viewModel.userBriefVo.userAccount.setValue(a.userAccount);
                        viewModel.userBriefVo.avatarUrl.setValue(a.avatarUrl);
                        viewModel.userBriefVo.userName.setValue(a.userName);
                    });
        } catch (Exception e){
            Log.e(TAG, "initIntentData::get UserBriefStartAo SerializableExtra Error: ", e);
        }
    }

    @Override
    protected void setListener() {
        super.setListener();

        binding.btnSendMessage.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChatActivity.class);
            ChatActivityStartAo CommandActivityStartAo = new ChatActivityStartAo();
            CommandActivityStartAo.contactAccount = viewModel.userBriefVo.userAccount.getValue();
            CommandActivityStartAo.contactName = viewModel.userBriefVo.userName.getValue();
            CommandActivityStartAo.avatarUrl = viewModel.userBriefVo.avatarUrl.getValue();
            intent.putExtra(ChatActivityStartAo.class.getName(), CommandActivityStartAo);
            startActivity(intent);
        });

        binding.topBar.setBack(v -> {
            finish();
        });
    }

    private UserBriefViewModel viewModel;

    private void initViewModel(){
        ApiViewModelFactory apiViewModelFactory = new ApiViewModelFactory(MainApplication.getApiRequestImplInstance(), MainApplication.getInstance().getMessageSender());
        viewModel = ViewModelUtil.newViewModel(this, apiViewModelFactory, UserBriefViewModel.class);

        initViewModelVo();
    }

    private void initViewModelVo() {
        UserBriefVo userBriefVo = new UserBriefVo();

        viewModel.init(userBriefVo);

//        binding.setViewModel(viewModel);
//        binding.setLifecycleOwner(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}