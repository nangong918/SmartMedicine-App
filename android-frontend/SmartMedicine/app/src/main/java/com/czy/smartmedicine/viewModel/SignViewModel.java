package com.czy.smartmedicine.viewModel;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;

import com.czy.appcore.network.netty.api.send.SocketMessageSender;
import com.czy.appcore.service.UserModel;
import com.czy.appcore.utils.OnTextInputEnd;
import com.czy.appcore.utils.TextChangeLegalCallback;
import com.czy.appcore.utils.phone.PhoneTextUtil;
import com.czy.appcore.utils.vcode.VcodeTextUtil;
import com.czy.baseUtilsLib.callback.OnFinish;
import com.czy.baseUtilsLib.network.BaseResponse;
import com.czy.baseUtilsLib.timer.CountDownTimerUtil;
import com.czy.baseUtilsLib.timer.CountdownCallback;
import com.czy.dal.ao.chat.UserLoginInfoAo;
import com.czy.dal.dto.http.request.SendSmsRequest;
import com.czy.dal.vo.viewModelVo.sign.SignVo;
import com.czy.dal.dto.http.request.PhoneLoginRequest;
import com.czy.dal.dto.http.response.LoginSignResponse;
import com.czy.datalib.networkRepository.ApiRequestImpl;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.activity.MainActivity;
import com.czy.smartmedicine.test.TestConfig;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author 13225
 * ViewModel用来保存数据，处理逻辑
 */
public class SignViewModel extends ViewModel {

    private static final String TAG = SignViewModel.class.getSimpleName();

    private final ApiRequestImpl apiRequestImpl;
    private final SocketMessageSender socketMessageSender;

    public SignViewModel(ApiRequestImpl apiRequestImpl, SocketMessageSender socketMessageSender){
        this.apiRequestImpl = apiRequestImpl;
        this.socketMessageSender = socketMessageSender;
    }

    //---------------------------Vo Ld---------------------------


    public SignVo signVo = new SignVo();

    public void init(SignVo signVo){
        initVo(signVo);
        initView();
    }

    // 初始化View
    private void initView(){
        initTimer();
    }

    // 初始化VO
    private void initVo(SignVo signVo){
        this.signVo = signVo;
    }


    //---------------------------NetWork---------------------------
;
    //==========发送短息

    public void doSendSms(
            SendSmsRequest sendSmsRequest,
            CountDownTimerUtil countDownTimerUtil
    ){
        this.apiRequestImpl.sendSms(
                sendSmsRequest,
                response -> handleSendSms(response, countDownTimerUtil),
                ViewModelUtil::globalThrowableToast
        );
    }

    private void handleSendSms(BaseResponse<String> response, CountDownTimerUtil countDownTimerUtil){
        if (ViewModelUtil.handleResponse(response)){
            countDownTimerUtil.startCountDown();
        }
        else {
            countDownTimerUtil.isStartCountDown = new AtomicBoolean(false);
        }
    }

    //==========短信登录

    /**
     * 短信登录
     */
    public void doSmsLogin(PhoneLoginRequest phoneLoginRequest){
        this.apiRequestImpl.smsLoginUser(
                phoneLoginRequest,
                this::handleSmsLogin,
                ViewModelUtil::globalThrowableToast
        );
    }

    private final UserModel userModel = UserModel.getInstance(MainApplication.getInstance());

    /**
     * 处理短信登录
     * @param response  BaseResponse
     */
    private void handleSmsLogin(BaseResponse<LoginSignResponse> response){
        if (ViewModelUtil.handleResponse(response)){
            Optional.ofNullable(response)
                    .map(BaseResponse::getData)
                    .ifPresent(data -> {
                        userModel.saveLoginToken(data.loginTokenAo);
                        UserLoginInfoAo userInfo = new UserLoginInfoAo(data.userId, data.account, data.phone, data.userName);
                        MainApplication.getInstance().setUserLoginInfoAo(userInfo);
                        userModel.saveUserInfo(userInfo);
                        onLoginSuccess(data.comeConnectWebsocket, data.account);
                    });
        }
        else {
            MainApplication.getInstance().showGlobalToast(com.czy.customviewlib.R.string.login_failed);
        }
    }


    //---------------------------Vo---------------------------
    ;
    public boolean isAgree = false;
    public void onPrivacyClick(){
        isAgree = !isAgree;
        signVo.imgvAgreeImageResource.setValue(
                isAgree ? com.czy.customviewlib.R.mipmap.ok5 :
                        com.czy.customviewlib.R.mipmap.circle4
        );
    }

    // 倒计时
    private CountDownTimerUtil countDownTimerUtil;

    private void initTimer(){
        int countTime = (TestConfig.IS_TEST) ?
                3 : 60;
        @SuppressLint("SetTextI18n")
        CountdownCallback callback = new CountdownCallback() {
            @Override
            public void timeCountDown(int countDownTime) {
                signVo.btvGetCodeText.setValue(countDownTime + "s");
            }

            @Override
            public void countDownFinish() {
                // 结束的时候设为没有开始倒计时
                countDownTimerUtil.isStartCountDown.compareAndSet(true,false);

                String text = MainApplication.getInstance().getStringByResId(com.czy.customviewlib.R.string.getVcodeAgain);
                signVo.btvGetCodeText.setValue(text);
            }
        };
        countDownTimerUtil = new CountDownTimerUtil(countTime, callback);
    }

    public void onGetCodeClick(){
        if (!PhoneTextUtil.phoneNumberLegitimateJudge(Objects.requireNonNull(signVo.edtvPhone.getValue()))){
            MainApplication.getInstance().showGlobalToast(com.czy.customviewlib.R.string.please_enter_the_correct_phone_number);
            signVo.edtvPhoneTextColor.setValue(com.czy.customviewlib.R.color.red);
            return;
        }
        // 如果没有开始倒计时就设为开始
        if (countDownTimerUtil.isStartCountDown.compareAndSet(false,true)){
            String phone = signVo.edtvPhone.getValue();
            if (TextUtils.isEmpty(phone)){
                MainApplication.getInstance().showGlobalToast(com.czy.customviewlib.R.string.please_enter_the_correct_phone_number);
                return;
            }
            SendSmsRequest sendSmsRequest = new SendSmsRequest();
            sendSmsRequest.phone = phone;
            sendSmsRequest.senderId = phone;
            // 发送验证码
            doSendSms(
                    sendSmsRequest,
                    countDownTimerUtil
            );
        }
    }

    private boolean isEnteredVcode = false;

    // 监听验证码
    public void onCodeChange(EditText editText){
        VcodeTextUtil.addPhoneTextChangeListener(editText, new TextChangeLegalCallback() {
            private boolean isEndInput = false;
            @Override
            public void legal() {
                isEnteredVcode = true;
                if (!isEndInput){
                    isEndInput = true;
                }
            }

            @Override
            public void illegal() {
                isEnteredVcode = false;
            }
        }, new OnTextInputEnd() {
            private boolean isTyping = false; // 标记是否正在输入
            @Override
            public void onTextInput(CharSequence s, int start, int count, int after) {
                // 在文本变化前调用
                if (!isTyping) {
                    isTyping = true; // 开始输入
                    // 风控：“开始输入验证码时间上报"，
//                    LabelEventAddUtil.addTimeEventLabel(LabelEventConfig.EventPage.LOGIN_PAGE, LabelEventConfig.EventKey.LOGIN_PAGE_CODE_FILL);
                }
            }

            @Override
            public void onTextEnd(Editable s) {
                // 风控:输入完成
            }
        });
    }

    // 手机合法性监听回调
    private TextChangeLegalCallback getPhoneTextChangeCallback(){
        return new TextChangeLegalCallback() {

            private boolean isEndInput = false;

            @Override
            public void legal() {
                signVo.btvGetCodeBackground.setValue(com.czy.customviewlib.R.drawable.button_grey);
                signVo.btvLoginBackground.setValue(com.czy.customviewlib.R.drawable.button_selected);
                // 输入手机号结束时间
                if (!isEndInput){
                    isEndInput = true;
                    // 风控: 输入手机号结束时间
//                    LabelEventAddUtil.addTimeEventLabel(LabelEventConfig.EventPage.LOGIN_PAGE, LabelEventConfig.EventKey.LOGIN_PAGE_PHONE_END_FILL);
                }
            }

            @Override
            public void illegal() {
                signVo.btvGetCodeBackground.setValue(com.czy.customviewlib.R.drawable.button_grey_not_select);
                signVo.btvLoginBackground.setValue(com.czy.customviewlib.R.drawable.button_not_select);
            }
        };
    }

    // 监听手机号
    public void onPhoneChange(EditText editText) {
        PhoneTextUtil.addPhoneTextChangeListener(editText, getPhoneTextChangeCallback(), new OnTextInputEnd() {
            private boolean isTyping = false; // 标记是否正在输入
            @Override
            public void onTextInput(CharSequence s, int start, int count, int after) {
                // 在文本变化前调用
                if (!isTyping) {
                    isTyping = true; // 开始输入
                    // 风控:开始输入手机号时间
//                    LabelEventAddUtil.addTimeEventLabel(LabelEventConfig.EventPage.LOGIN_PAGE, LabelEventConfig.EventKey.LOGIN_PAGE_PHONE_START_FILL);
                }
            }

            @Override
            public void onTextEnd(Editable s) {

            }
        });
    }
    private OnFinish onLoginSuccess;
    // 登录按钮
    public void onLoginClick(OnFinish onLoginSuccess){
        this.onLoginSuccess = onLoginSuccess;
        Log.d("Intercept", "onLoginClick: " + signVo.edtvPhone.getValue());
        if (!PhoneTextUtil.phoneNumberLegitimateJudge(signVo.edtvPhone.getValue())){
            MainApplication.getInstance().showGlobalToast(com.czy.customviewlib.R.string.please_enter_the_correct_phone_number);
            signVo.edtvPhoneTextColor.setValue(com.czy.customviewlib.R.color.red);
            return;
        }
        if (isAgree){
            // 风控：“点击登录按钮时间上报"，
//                LabelEventAddUtil.addTimeEventLabel(LabelEventConfig.EventPage.LOGIN_PAGE, LabelEventConfig.EventKey.LOGIN_PAGE_LOGIN_POINT);
            if (isEnteredVcode){
                String code = Objects.requireNonNull(signVo.edtvCode.getValue());
                if (TextUtils.isEmpty(code)){
                    MainApplication.getInstance().showGlobalToast(com.czy.customviewlib.R.string.please_enter_the_correct_vcode);
                    signVo.edtvCodeTextColor.setValue(com.czy.customviewlib.R.color.red);
                    return;
                }
                String phone = Objects.requireNonNull(signVo.edtvPhone.getValue());
                if (TextUtils.isEmpty(phone)){
                    MainApplication.getInstance().showGlobalToast(com.czy.customviewlib.R.string.please_enter_the_correct_phone_number);
                    signVo.edtvPhoneTextColor.setValue(com.czy.customviewlib.R.color.red);
                    return;
                }
                PhoneLoginRequest phoneLoginRequest = new PhoneLoginRequest(phone, code);
                Log.d("Intercept", "1BaseNettyRequest: senderId: " + phone);
//                phoneLoginRequest.phone = phone;
//                phoneLoginRequest.code = code;
                doSmsLogin(
                        phoneLoginRequest
                );
            }
            else {
                MainApplication.getInstance().showGlobalToast(com.czy.customviewlib.R.string.please_enter_the_correct_vcode);
                signVo.edtvCodeTextColor.setValue(com.czy.customviewlib.R.color.red);
                signVo.vCodeBackgroundColor.setValue(com.czy.customviewlib.R.color.red);
            }
        }
        else {
            MainApplication.getInstance().showGlobalToast(com.czy.customviewlib.R.string.please_agree_to_the_privacy_agreement);
            signVo.tvAgreeTextColor.setValue(com.czy.customviewlib.R.color.red);
            signVo.tvPrivacyTextColor.setValue(com.czy.customviewlib.R.color.red);
        }
    }

    //---------------------------logic---------------------------

    /**
     * 跳转到MainActivity
     * @param connectWebSocket  是否连接
     * @param senderAccount     account就是uid?重构响应体，要求响应体由uid
     */
    public void onLoginSuccess(boolean connectWebSocket, String senderAccount){
        // 登录成功
        if (connectWebSocket){
            MainApplication.getInstance().startNettySocketService(senderAccount);
        }

        // 跳转到MainActivity
//        Intent intent = new Intent(contextWeakReference.get(), MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        contextWeakReference.get().startActivity(intent);
        onLoginSuccess.onFinish();
    }

    // Test
    public void onTestClick(AppCompatActivity activity){
        activity.startActivity(new Intent(activity, MainActivity.class));
    }
}
