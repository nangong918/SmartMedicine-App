package com.czy.smartmedicine.viewModel.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import androidx.lifecycle.ViewModel;

import com.czy.appcore.network.api.SyncRequestCallback;
import com.czy.appcore.network.netty.api.send.SocketMessageSender;
import com.czy.appcore.utils.OnTextInputEnd;
import com.czy.appcore.utils.TextChangeLegalCallback;
import com.czy.appcore.utils.password.PasswordTextUtil;
import com.czy.appcore.utils.phone.PhoneTextUtil;
import com.czy.appcore.utils.vcode.VcodeTextUtil;
import com.czy.baseUtilsLib.network.BaseResponse;
import com.czy.baseUtilsLib.network.networkLoad.NetworkLoadUtils;
import com.czy.baseUtilsLib.timer.CountDownTimerUtil;
import com.czy.baseUtilsLib.timer.CountdownCallback;
import com.czy.dal.ao.intent.RegisterActivityIntentAo;
import com.czy.dal.dto.http.request.RegisterUserRequest;
import com.czy.dal.dto.http.request.SendSmsInfoRequest;
import com.czy.dal.dto.http.response.SendSmsResponse;
import com.czy.dal.dto.http.response.UserRegisterResponse;
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
    public void tryRegisterSendSms(Context context, SyncRequestCallback callback){
        doSendSms(context, registerVo.phone.getValue(), "1", callback);
    }

    private void doSendSms(Context context, String phone, String type, SyncRequestCallback callback){
        SendSmsInfoRequest request = new SendSmsInfoRequest(phone, type);
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
        callback.onAllRequestSuccess();
    }

    // 注册

    public void doRegister(Context context,
                           String phone,
                           String vcode,
                           String userName,
                           String account,
                           String pwd, SyncRequestCallback callback){
        NetworkLoadUtils.showDialog(context);
        RegisterUserRequest request = new RegisterUserRequest();
        request.password = pwd;
        request.phone = phone;
        request.vcode = vcode;
        if (TextUtils.isEmpty(account)){
            account = "Ac-" + phone;
        }
        if (TextUtils.isEmpty(userName)){
            userName = "N-" + phone;
        }
        request.account = account;
        request.userName = userName;

        apiRequestImpl.register(
                request,
                response -> {
                    ResponseTool.handleSyncResponseEx(
                            response,
                            context,
                            callback,
                            this::handleRegisterResponse
                    );
                }
                ,
                callback::onThrowable
        );
    }

    public Long userTempId = null;

    private void handleRegisterResponse(BaseResponse<UserRegisterResponse> response, Context context, SyncRequestCallback callback) {
        this.userTempId = response.getData().snowflakeId;
        callback.onAllRequestSuccess();
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

    public void onPhoneChanged(EditText editText, Context context){
        PhoneTextUtil.addPhoneTextChangeListener(
                registerVo.phone,
                editText,
                getPhoneTextChangeLegalCallback(context),
                new OnTextInputEnd() {
                    @Override
                    public void onTextInput(CharSequence s, int start, int count, int after) {
                        // 风控:开始输入手机号时间
                    }

                    @Override
                    public void onTextEnd(Editable s) {

                    }
                }
        );
    }

    public void onVcodeChanged(EditText editText, Context context){
        VcodeTextUtil.addPhoneTextChangeListener(
                registerVo.vcode,
                editText,
                getVcodeTextChangeLegalCallback(),
                new OnTextInputEnd() {
                    @Override
                    public void onTextInput(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextEnd(Editable s) {

                    }
                }
        );
    }

    public void onPasswordInputEnd(EditText editText, Context context){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String password = s.toString();
                registerVo.pwd.setValue(password);
                boolean isLegal = PasswordTextUtil.isPasswordLegal(password);
                registerVo.isPwdValid.setValue(isLegal);
            }
        });
    }

    public void onConfirmPasswordInputEnd(EditText editText, Context context){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String confirmPassword = s.toString();
                registerVo.pwdAgain.setValue(confirmPassword);
                boolean isLegal = PasswordTextUtil.isPasswordLegal(confirmPassword);
                boolean isEqual = confirmPassword.equals(registerVo.pwd.getValue());
                registerVo.isPwdAgainConsist.setValue(isLegal && isEqual);
            }
        });
    }

    private TextChangeLegalCallback getPhoneTextChangeLegalCallback(Context context){
        return new TextChangeLegalCallback() {
            @Override
            public void legal() {
                registerVo.isPhoneValid.setValue(true);
            }

            @Override
            public void illegal() {
                registerVo.isPhoneValid.setValue(false);
            }
        };
    }
    private TextChangeLegalCallback getVcodeTextChangeLegalCallback(){
        return new TextChangeLegalCallback() {
            @Override
            public void legal() {
                registerVo.isVcodeValid.setValue(true);
            }

            @Override
            public void illegal() {
                registerVo.isVcodeValid.setValue(false);
            }
        };
    }
}
