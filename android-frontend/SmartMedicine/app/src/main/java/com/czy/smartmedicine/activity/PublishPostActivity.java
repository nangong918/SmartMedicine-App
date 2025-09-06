package com.czy.smartmedicine.activity;


import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.czy.appcore.BaseConfig;
import com.czy.appcore.network.api.handle.SyncRequestCallback;
import com.czy.baseutil.activity.BaseActivity;
import com.czy.baseutil.network.networkLoad.NetworkLoadUtils;
import com.czy.baseutil.photo.SelectPhotoUtil;
import com.czy.baseutil.viewModel.ViewModelUtil;
import com.czy.domain.fragmentActivityAo.post.PublishPostAAo;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.databinding.ActivityPublishPostBinding;
import com.czy.smartmedicine.viewModel.activity.PublishPostVm;
import com.czy.smartmedicine.viewModel.base.ApiViewModelFactory;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

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
        vm.initSelectImageLaunchers(
                // imageViews.length == addViews.length == BaseConfig.MAX_POST_IMAGE_COUNT
                new ConstraintLayout[]{
                        binding.lyP1,
                        binding.lyP2,
                        binding.lyP3
                },
                new ImageView[]{
                        binding.imgP1,
                        binding.imgP2,
                        binding.imgP3
                },
                new View[]{
                        binding.vAdd1,
                        binding.vAdd2,
                        binding.vAdd3
                },
                this
        );
    }

    @Override
    protected void setListener() {
        super.setListener();

        // 返回
        binding.btnBack.setOnClickListener(v -> finish());

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
        ImageView[] imageViews = new ImageView[]{
                binding.imgP1,
                binding.imgP2,
                binding.imgP3
        };
        for (int i = 0; i < imageViews.length; i++) {
            int finalI = i;
            imageViews[i].setOnClickListener(v -> {
                SelectPhotoUtil.selectImageFromAlbum(vm.selectImageLaunchers.get(finalI));
            });
        }
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
        PublishPostAAo aao = new PublishPostAAo();
        aao.imageUriArList = new ArrayList<>(BaseConfig.MAX_POST_IMAGE_COUNT);
        for (int i = 0; i < BaseConfig.MAX_POST_IMAGE_COUNT; i++){
            aao.imageUriArList.add(new AtomicReference<>(null));
        }
        vm.init(aao);
    }

    private void observeLivedata() {
    }

}