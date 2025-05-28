package com.czy.smartmedicine.activity;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.czy.appcore.network.netty.api.receive.ReceiveMessageApi;
import com.czy.baseUtilsLib.activity.BaseActivity;
import com.czy.baseUtilsLib.ui.ToastUtils;
import com.czy.dal.dto.netty.forwardMessage.GroupTextDataResponse;
import com.czy.dal.dto.netty.forwardMessage.SendTextDataRequest;
import com.czy.dal.dto.netty.forwardMessage.UserImageResponse;
import com.czy.dal.dto.netty.forwardMessage.UserTextDataResponse;
import com.czy.dal.dto.netty.response.HaveReadMessageResponse;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.databinding.ActivityTestBinding;
import com.czy.smartmedicine.test.TestConfig;

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
    }

    @Override
    protected void setListener() {
        super.setListener();

        binding.btnInit.setOnClickListener(v -> {
            String sendId = binding.etSenderId.getText().toString();
            if (TextUtils.isEmpty(sendId)){
                ToastUtils.showToastActivity(this, "请输入发送者id");
                return;
            }
            // 初始化Service
            MainApplication.getInstance().startNettySocketService(sendId);
        });

        binding.btnSend.setOnClickListener(v -> {
            String senderId = binding.etSenderId.getText().toString();
            String receiveId = binding.etReceiverId.getText().toString();
            Log.i("Socket", "senderId: " + senderId + " receiveId: " + receiveId);
            String content = binding.etMessage.getText().toString();

            SendTextDataRequest sendTextDataRequest = new SendTextDataRequest();
            sendTextDataRequest.setContent(content);
            sendTextDataRequest.setSenderId(senderId);
            sendTextDataRequest.setReceiverId(receiveId);

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