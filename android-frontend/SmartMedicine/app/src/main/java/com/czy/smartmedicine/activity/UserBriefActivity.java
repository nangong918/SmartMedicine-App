package com.czy.smartmedicine.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.czy.appcore.network.api.handle.SyncRequestCallback;
import com.czy.baseUtilsLib.activity.BaseActivity;
import com.czy.baseUtilsLib.image.ImageLoadUtil;
import com.czy.baseUtilsLib.ui.ToastUtils;
import com.czy.baseUtilsLib.viewModel.ViewModelUtil;
import com.czy.dal.ao.chat.ChatActivityStartAo;
import com.czy.dal.ao.userBrief.UserBriefIntentAo;
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

        initRequest();
    }

    private void initRequest(){
        viewModel.doGetUserBrief(this, new SyncRequestCallback() {
            @Override
            public void onThrowable(Throwable throwable) {
                ToastUtils.showToast(UserBriefActivity.this, "获取用户信息失败");
            }

            @Override
            public void onAllRequestSuccess() {
                loadPostInfo();
            }
        });
    }

    private void initIntentData(){
        Intent intent = getIntent();
        try {
            UserBriefIntentAo ao = (UserBriefIntentAo)intent.getSerializableExtra(UserBriefIntentAo.class.getName());
            Optional.ofNullable(ao)
                    .ifPresent(a -> {
                        // 设置view数据给viewModel
                        viewModel.userBriefVo.userAccount.setValue(a.userAccount);
                        viewModel.userBriefVo.avatarUrl.setValue(a.avatarUrl);
                        viewModel.userBriefVo.userName.setValue(a.userName);
                        viewModel.userBriefVo.userId = a.userId;
                    });
        } catch (Exception e){
            Log.e(TAG, "initIntentData::get UserBriefIntentAo SerializableExtra Error: ", e);
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

        observeData();
    }

    private void observeData() {
        viewModel.userBriefVo.userName.observe(this, userName -> {
            binding.tvName.setText(userName);
        });

        viewModel.userBriefVo.userAccount.observe(this, userAccount -> {
            binding.tvAccount.setText(userAccount);
        });

        viewModel.userBriefVo.userRemark.observe(this, userRemark -> {
            binding.tvNotes.setText(userRemark);
        });

        viewModel.userBriefVo.avatarUrl.observe(this, avatarUrl -> {
            ImageLoadUtil.loadImageViewByUrl(avatarUrl, binding.circleImageView2);
        });
    }

    private void loadPostInfo(){
        if (viewModel.userBriefVo.userPosts == null || viewModel.userBriefVo.userPosts.isEmpty()){
            return;
        }
        ImageView[] postImageViews = new ImageView[]{
                binding.imgvFriendMoments1,
                binding.imgvFriendMoments2,
                binding.imgvFriendMoments3,
                binding.imgvFriendMoments4
        };
        int imageIndex = 0;
        for (int i = 0; i < viewModel.userBriefVo.userPosts.size(); i++){
            String url = Optional.ofNullable(viewModel.userBriefVo.userPosts.get(i))
                    .map(postVo -> postVo.postImgUrls)
                    .filter(urls -> !urls.isEmpty())
                    .map(urls_ -> urls_.get(0))
                    .orElse("");
            if (TextUtils.isEmpty(url)){
                continue;
            }
            ImageLoadUtil.loadImageViewByUrl(url, postImageViews[imageIndex]);
            imageIndex++;
        }
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