package com.czy.smartmedicine.activity;


import com.czy.baseUtilsLib.activity.BaseActivity;
import com.czy.baseUtilsLib.viewModel.ViewModelUtil;
import com.czy.dal.vo.viewModelVo.post.PublishPostVo;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.databinding.ActivityPublishPostBinding;
import com.czy.smartmedicine.viewModel.ApiViewModelFactory;
import com.czy.smartmedicine.viewModel.PublishViewModel;

public class PublishPostActivity extends BaseActivity<ActivityPublishPostBinding> {

    public PublishPostActivity() {
        super(PublishPostActivity.class);
    }

    @Override
    protected void init() {
        super.init();
        initViewModel();
    }

    @Override
    protected void setListener() {
        super.setListener();
        binding.btnPublish.setOnClickListener(v -> {
            // TODO 发布帖子接口
        });
    }

    private PublishViewModel viewModel;

    private void initViewModel(){
        ApiViewModelFactory apiViewModelFactory = new ApiViewModelFactory(MainApplication.getApiRequestImplInstance(), MainApplication.getInstance().getMessageSender());
        viewModel = ViewModelUtil.newViewModel(this, apiViewModelFactory, PublishViewModel.class);

        initViewModelVo();

        observeLivedata();

        // 绑定viewModel
        binding.setViewModel(viewModel);
        // 设置监听者
        binding.setLifecycleOwner(this);
    }

    private void initViewModelVo() {
        PublishPostVo publishPostVo = new PublishPostVo();
        viewModel.init(publishPostVo);
    }

    private void observeLivedata() {
    }

    // 选择图片


}