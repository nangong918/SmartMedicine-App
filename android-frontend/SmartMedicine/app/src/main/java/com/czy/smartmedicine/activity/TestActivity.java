package com.czy.smartmedicine.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.czy.appcore.network.netty.api.receive.ReceiveMessageApi;
import com.czy.baseUtilsLib.activity.BaseActivity;
import com.czy.baseUtilsLib.image.ImageLoadUtil;
import com.czy.baseUtilsLib.permission.GainPermissionCallback;
import com.czy.baseUtilsLib.permission.PermissionUtil;
import com.czy.baseUtilsLib.ui.ToastUtils;
import com.czy.baseUtilsLib.viewModel.ViewModelUtil;
import com.czy.dal.constant.Constants;
import com.czy.dal.dto.netty.forwardMessage.GroupTextDataResponse;
import com.czy.dal.dto.netty.forwardMessage.SendTextDataRequest;
import com.czy.dal.dto.netty.forwardMessage.UserImageResponse;
import com.czy.dal.dto.netty.forwardMessage.UserTextDataResponse;
import com.czy.dal.dto.netty.response.HaveReadMessageResponse;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.databinding.ActivityTestBinding;
import com.czy.smartmedicine.test.TestConfig;
import com.czy.smartmedicine.viewModel.activity.PublishViewModel;
import com.czy.smartmedicine.viewModel.activity.TestViewModel;
import com.czy.smartmedicine.viewModel.base.ApiViewModelFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Optional;

/**
 * @author 13225
 */
public class TestActivity extends BaseActivity<ActivityTestBinding> {

    public TestActivity() {
        super(TestActivity.class);
    }

    @Override
    protected void init() {
        super.init();
        if (TestConfig.IS_TEST){
            initReceiveMessageApi();
        }
        new Handler(Looper.getMainLooper()).post(() -> {
            MainApplication.getInstance().showGlobalToast("Global Toast Test");
        });
        initViewModel();
        initPictureSelectLauncher();
    }

    @Override
    protected void setListener() {
        super.setListener();

        binding.btnInit.setOnClickListener(v -> {
            Long sendId = Optional.ofNullable(binding.etSenderId.getText())
                    .map(Editable::toString)
                    .map(Long::parseLong)
                    .orElse(Constants.ERROR_ID);
            if (Constants.ERROR_ID.equals(sendId)){
                ToastUtils.showToastActivity(this, "请输入发送者id");
                return;
            }
            // 初始化Service
            MainApplication.getInstance().startNettySocketService(sendId);
        });

        binding.btnSend.setOnClickListener(v -> {
            Long senderId = Optional.ofNullable(binding.etSenderId.getText())
                    .map(Editable::toString)
                    .map(Long::parseLong)
                    .orElse(Constants.ERROR_ID);

            Long receiverId = Optional.ofNullable(binding.etReceiverId.getText())
                    .map(Editable::toString)
                    .map(Long::parseLong)
                    .orElse(Constants.ERROR_ID);
            Log.i("Socket", "senderId: " + senderId + " receiverId: " + receiverId);
            String content = binding.etMessage.getText().toString();

            SendTextDataRequest sendTextDataRequest = new SendTextDataRequest();
            sendTextDataRequest.setContent(content);
            sendTextDataRequest.setSenderId(senderId);
            sendTextDataRequest.setReceiverId(receiverId);

            try {
                Optional.ofNullable(MainApplication.getInstance())
                        .map(MainApplication::getMessageSender)
                        .ifPresent(
                                msgSender -> msgSender.sendTextToUser(sendTextDataRequest)
                        );
            } catch (Exception e){
                Log.e(TAG, "Failed to send text: one of the components is null");
            }
        });

        binding.btnDisconnect.setOnClickListener(v -> {
            MainApplication.getInstance().disconnectNettySocketService();
        });

        binding.imgvSelectImage.setOnClickListener(v -> {
            PermissionUtil.requestPermissionsX(this, new String[]{
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, new GainPermissionCallback() {
                @Override
                public void allGranted() {
                    com.czy.baseUtilsLib.photo.SelectPhotoUtil.selectImageFromAlbum(selectImageLauncher);
                }

                @Override
                public void notGranted(String[] notGrantedPermissions) {
                    ToastUtils.showToastActivity(TestActivity.this, "获取权限失败");
                }
            });
        });

        binding.btnUpload.setOnClickListener(v -> {
            viewModel.uploadImageTest(this);
        });

        binding.btnLoad.setOnClickListener(v -> {
            String url = Optional.ofNullable(binding.etUrl.getText())
                    .map(Editable::toString)
                    .orElse("");
            if (!TextUtils.isEmpty(url)){
                ImageLoadUtil.loadImageViewByUrl(url, binding.imgvLoadImage);
            }
        });
    }

    //------------------viewModel------------------

    private TestViewModel viewModel;

    private void initViewModel(){
        ApiViewModelFactory apiViewModelFactory = new ApiViewModelFactory(MainApplication.getApiRequestImplInstance(), MainApplication.getInstance().getMessageSender());
        viewModel = ViewModelUtil.newViewModel(this, apiViewModelFactory, TestViewModel.class);


    }

    //------------------img------------------
    ;
    //===========Picture

    private ActivityResultLauncher<Intent> selectImageLauncher;
    private void initPictureSelectLauncher(){
        selectImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent data = result.getData();
                    if (data != null){
                        Uri imageUri = data.getData();
                        viewModel.uriAtomicReference.set(imageUri);
                        Bitmap bitmap = MainApplication.getInstance().getImageManager().uriToBitmapMediaStore(this, imageUri);
                        if (bitmap != null){
                            binding.imgvSelectImage.setImageBitmap(bitmap);
                        }
                    }
                }
        );
    }

    //------------------Message------------------

    private ReceiveMessageApi receiveMessageApi;

    private void initReceiveMessageApi(){
        initEventBus();
        receiveMessageApi = new ReceiveMessageApi() {
            @Override
            public void receiveUserText(@NonNull UserTextDataResponse response) {
                binding.tvMessage.setText(response.getContent());
            }

            @Override
            public void receiveGroupText(@NonNull GroupTextDataResponse response) {

            }

            @Override
            public void haveReadMessage(@NonNull HaveReadMessageResponse response) {

            }

            @Override
            public void receiveUserImage(@NonNull UserImageResponse response) {

            }
        };
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageReceived(UserTextDataResponse response) {
        if (response != null){
            // 根据 message 的 type 执行对应的方法
            receiveMessageApi.receiveUserText(response);
            Log.d("Socket", "onMessageReceived: " + response.getContent());
        }
    }

    private void initEventBus() {
        EventBus.getDefault().register(this);
    }

    private void unInitEventBus() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unInitEventBus();
    }

}