package com.czy.smartmedicine.activity;


import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;

import com.czy.appcore.network.api.handle.SyncRequestCallback;
import com.czy.baseUtilsLib.activity.BaseActivity;
import com.czy.baseUtilsLib.image.ImageManager;
import com.czy.baseUtilsLib.network.networkLoad.NetworkLoadUtils;
import com.czy.baseUtilsLib.photo.SelectPhotoUtil;
import com.czy.baseUtilsLib.viewModel.ViewModelUtil;
import com.czy.dal.fragmentActivityAo.post.PublishPostVo;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.databinding.ActivityPublishPostBinding;
import com.czy.smartmedicine.viewModel.activity.PublishPostVm;
import com.czy.smartmedicine.viewModel.base.ApiViewModelFactory;

/**
 * 发布帖子界面
 */
public class PublishPostActivity extends BaseActivity<ActivityPublishPostBinding> {

    public PublishPostActivity() {
        super(PublishPostActivity.class);
    }

    @Override
    public ActivityPublishPostBinding getBinding() {
        return ActivityPublishPostBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void init() {
        super.init();
        initViewModel();
        initActivityLauncher();
    }

    @Override
    protected void setListener() {
        super.setListener();

        // 返回
        binding.topBar.setBack(v -> finish());

        // 发布
        binding.btnPublish.setOnClickListener(v -> {
            // 因为后端需要先检查是否合法
            // 所以前端需要调用第一个接口

            NetworkLoadUtils.showDialog(this);
            vm.doPostPublishFirst(
                    this,
                    new SyncRequestCallback() {
                        @Override
                        public void onThrowable(Throwable throwable) {
                            Log.e(TAG, "发布异常：", throwable);
                            NetworkLoadUtils.dismissDialog();
                        }

                        @Override
                        public void onAllRequestSuccess() {
                            NetworkLoadUtils.dismissDialog();
                        }
                    }
            );
            // 再调用第二个接口（viewModel内部调用）
        });

        // 选择图片
        binding.imgvArticlePic.setOnClickListener(v -> {
            SelectPhotoUtil.selectImageFromAlbum(selectImageLauncher);
        });
    }

    private PublishPostVm vm;

    private void initViewModel(){
        ApiViewModelFactory apiViewModelFactory = new ApiViewModelFactory(MainApplication.getApiRequestImplInstance(), MainApplication.getInstance().getMessageSender());
        vm = ViewModelUtil.newViewModel(this, apiViewModelFactory, PublishPostVm.class);

        initViewModelVo();

        observeLivedata();

        // 绑定viewModel
        binding.setViewModel(vm);
        // 设置监听者
        binding.setLifecycleOwner(this);
    }

    private void initViewModelVo() {
        PublishPostVo publishPostVo = new PublishPostVo();
        vm.init(publishPostVo);
    }

    private void observeLivedata() {
    }

    // 选择图片
    private ActivityResultLauncher<Intent> selectImageLauncher;

    private void initActivityLauncher() {
        ImageManager imageManager = new ImageManager();

        selectImageLauncher = SelectPhotoUtil.initActivityResultLauncher(
                this,
                binding.imgvArticlePic,
                vm.selectImageUriAtomic,
                imageManager,
                () -> {
                    binding.vSelectImage.setVisibility(View.GONE);
                }
        );
    }

}