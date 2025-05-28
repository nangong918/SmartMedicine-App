package com.czy.smartmedicine.activity;


import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;

import com.czy.baseUtilsLib.activity.BaseActivity;
import com.czy.baseUtilsLib.image.ImageManager;
import com.czy.baseUtilsLib.photo.SelectPhotoUtil;
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
        initActivityLauncher();
    }

    @Override
    protected void setListener() {
        super.setListener();
        // 发布
        binding.btnPublish.setOnClickListener(v -> {
            // 因为后端需要先检查是否合法
            // 所以前端需要调用第一个接口
            String title = viewModel.publishPostVo.postTitleLd.getValue();
            String content = viewModel.publishPostVo.postContentLd.getValue();
            if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)){
                return;
            }
            boolean isHaveFile = !(viewModel.publishPostVo.imageUriLd.getValue() == null);
            viewModel.doPostPublishFirst(
                    title, content,
                    isHaveFile,
                    this
            );
            // 再调用第二个接口（viewModel内部调用）
        });

        // 选择图片
        binding.imgvArticlePic.setOnClickListener(v -> {
            SelectPhotoUtil.selectImageFromAlbum(selectImageLauncher);
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
    private ActivityResultLauncher<Intent> selectImageLauncher;

    private void initActivityLauncher() {
        ImageManager imageManager = new ImageManager();

        selectImageLauncher = SelectPhotoUtil.initActivityResultLauncher(
                this,
                binding.imgvArticlePic,
                viewModel.selectImageUriAtomic,
                imageManager,
                () -> {
                    binding.vSelectImage.setVisibility(View.GONE);
                }
        );
    }

}