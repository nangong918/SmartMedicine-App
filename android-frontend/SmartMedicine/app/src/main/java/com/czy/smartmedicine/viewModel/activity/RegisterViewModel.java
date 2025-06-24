package com.czy.smartmedicine.viewModel.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.czy.appcore.network.api.SyncRequestCallback;
import com.czy.appcore.network.netty.api.send.SocketMessageSender;
import com.czy.baseUtilsLib.network.BaseResponse;
import com.czy.baseUtilsLib.timer.CountDownTimerUtil;
import com.czy.baseUtilsLib.timer.CountdownCallback;
import com.czy.dal.ao.intent.RegisterActivityIntentAo;
import com.czy.dal.dto.http.request.SendSmsRequest;
import com.czy.dal.dto.http.response.SendSmsResponse;
import com.czy.dal.vo.fragmentActivity.RegisterVo;
import com.czy.datalib.networkRepository.ApiRequestImpl;
import com.czy.smartmedicine.test.TestConfig;
import com.czy.smartmedicine.utils.ResponseTool;


public class RegisterViewModel extends ViewModel {

    private final static String TAG = RegisterViewModel.class.getName();

    private final ApiRequestImpl apiRequestImpl;
    private final SocketMessageSender socketMessageSender;

    public RegisterViewModel (ApiRequestImpl apiRequestImpl, SocketMessageSender socketMessageSender) {
        this.apiRequestImpl = apiRequestImpl;
        this.socketMessageSender = socketMessageSender;
    }

    //---------------------------Vo Ld---------------------------

    public RegisterVo registerVo = new RegisterVo();
    public RegisterActivityIntentAo intentAo;

    public void initVo(RegisterVo registerVo) {
        this.registerVo = registerVo;
        initTimer();
    }

    //---------------------------Network---------------------------
;
    // register

    // 重置密码
    public void doResetPwd(Context context, String phone, String pwd, SyncRequestCallback callback){
        // 以后再说
        doSendSms(context, phone, "2", callback);
    }
    // 发送验证码
    public void doRegister(Context context, String phone, SyncRequestCallback callback){
        doSendSms(context, phone, "1", callback);
    }

    private void doSendSms(Context context, String phone, String type, SyncRequestCallback callback){
        SendSmsRequest request = new SendSmsRequest(phone, type);
        apiRequestImpl.sendSms(
                request,
                response -> {
                    ResponseTool.handleSyncResponseEx(
                            response,
                            context,
                            callback,
                            type,
                            this::handleSms
                    );
                },
                callback::onThrowable
        );
    }

    private void handleSms(BaseResponse<SendSmsResponse> response, Context context, SyncRequestCallback callback, Object param) {
        String type = (String) param;
        Log.i(TAG, "handleSms: " + response.getData());
    }

    //---------------------------logic---------------------------

    // 倒计时
    public CountDownTimerUtil countDownTimerUtil;

    private void initTimer(){
        int countTime = (TestConfig.IS_TEST) ?
                3 : 60;
        @SuppressLint("SetTextI18n")
        CountdownCallback callback = new CountdownCallback() {
            @Override
            public void timeCountDown(int countDownTime) {
                registerVo.vcodeCountDown.setValue(countDownTime);
            }

            @Override
            public void countDownFinish() {
                // 结束的时候设为没有开始倒计时
                countDownTimerUtil.isStartCountDown.compareAndSet(true,false);

                registerVo.vcodeCountDown.setValue(0);
            }
        };
        countDownTimerUtil = new CountDownTimerUtil(countTime, callback);
    }

}
