package com.czy.smartmedicine.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.czy.appcore.network.netty.api.receive.ChatApiHandler;
import com.czy.baseutil.activity.BaseActivity;
import com.czy.baseutil.image.ImageLoadUtil;
import com.czy.baseutil.permission.GainPermissionCallback;
import com.czy.baseutil.permission.PermissionUtil;
import com.czy.baseutil.ui.ToastUtils;
import com.czy.baseutil.viewModel.ViewModelUtil;
import com.czy.domain.constant.NettyConstants;
import com.czy.domain.dto.netty.forwardMessage.GroupTextDataResponse;
import com.czy.domain.dto.netty.forwardMessage.SendTextDataRequest;
import com.czy.domain.dto.netty.forwardMessage.UserImageResponse;
import com.czy.domain.dto.netty.forwardMessage.UserTextDataResponse;
import com.czy.domain.dto.netty.response.HaveReadMessageResponse;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.databinding.ActivityTestBinding;
import com.czy.smartmedicine.test.TestConfig;
import com.czy.smartmedicine.viewModel.activity.TestVm;
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
    public ActivityTestBinding getBinding() {
        return ActivityTestBinding.inflate(getLayoutInflater());
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
                    .orElse(NettyConstants.ERROR_ID);
            if (NettyConstants.ERROR_ID.equals(sendId)){
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
                    .orElse(NettyConstants.ERROR_ID);

            Long receiverId = Optional.ofNullable(binding.etReceiverId.getText())
                    .map(Editable::toString)
                    .map(Long::parseLong)
                    .orElse(NettyConstants.ERROR_ID);
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
                    com.czy.baseutil.photo.SelectPhotoUtil.selectImageFromAlbum(selectImageLauncher);
                }

                @Override
                public void notGranted(String[] notGrantedPermissions) {
                    ToastUtils.showToastActivity(TestActivity.this, "获取权限失败");
                }
            });
        });

        binding.btnUpload.setOnClickListener(v -> {
            vm.uploadImageTest(this);
        });

        binding.btnLoad.setOnClickListener(v -> {
            String url = Optional.ofNullable(binding.etUrl.getText())
                    .map(Editable::toString)
                    .orElse("");
            if (!TextUtils.isEmpty(url)){
                ImageLoadUtil.loadImageViewByResource(url, binding.imgvLoadImage);
            }
        });
    }

    //------------------viewModel------------------

    private TestVm vm;

    private void initViewModel(){
        ApiViewModelFactory apiViewModelFactory = new ApiViewModelFactory(MainApplication.getApiRequestImplInstance(), MainApplication.getInstance().getMessageSender());
        vm = ViewModelUtil.newViewModel(this, apiViewModelFactory, TestVm.class);


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
//                        viewModel.uriAtomicReference.set(imageUri);
//                        Bitmap bitmap = MainApplication.getInstance().getImageManager().uriToBitmapMediaStore(this, imageUri);
//                        if (bitmap != null){
//                            binding.imgvSelectImage.setImageBitmap(bitmap);
//                        }
                        ImageLoadUtil.loadImageViewByResource(
                                Optional.ofNullable(imageUri)
                                        .map(Uri::toString)
                                        .orElse(""),
                                binding.imgvSelectImage
                        );
                    }
                }
        );
    }

    //------------------Message------------------

    private ChatApiHandler chatApiHandler;

    private void initReceiveMessageApi(){
        initEventBus();
        chatApiHandler = new ChatApiHandler() {
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
            chatApiHandler.receiveUserText(response);
            Log.d("Socket", "onMessageReceived: " + response.getContent());
            // 移除已处理的粘性事件
            EventBus.getDefault().removeStickyEvent(response);
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