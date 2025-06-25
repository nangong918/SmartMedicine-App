package com.czy.smartmedicine.viewModel.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;

import com.czy.appcore.network.api.SyncRequestCallback;
import com.czy.appcore.network.netty.api.send.SocketMessageSender;
import com.czy.appcore.utils.OnTextInputEnd;
import com.czy.appcore.utils.TextChangeLegalCallback;
import com.czy.appcore.utils.phone.PhoneTextUtil;
import com.czy.appcore.utils.vcode.VcodeTextUtil;
import com.czy.baseUtilsLib.network.BaseResponse;
import com.czy.baseUtilsLib.network.networkLoad.NetworkLoadUtils;
import com.czy.baseUtilsLib.permission.GainPermissionCallback;
import com.czy.baseUtilsLib.permission.PermissionUtil;
import com.czy.baseUtilsLib.ui.ToastUtils;
import com.czy.dal.ao.chat.UserLoginInfoAo;
import com.czy.dal.ao.login.LoginTokenAo;
import com.czy.dal.constant.Constants;
import com.czy.dal.dto.http.request.IsRegisterRequest;
import com.czy.dal.dto.http.request.LoginUserRequest;
import com.czy.dal.dto.http.response.IsRegisterResponse;
import com.czy.dal.vo.fragmentActivity.SignVo;
import com.czy.dal.dto.http.response.LoginSignResponse;
import com.czy.datalib.networkRepository.ApiRequestImpl;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.activity.MainActivity;

import java.util.Optional;

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

    // 初始化VO
    public void initVo(SignVo signVo){
        this.signVo = signVo;
    }

    public void onPhoneChanged(EditText editText, Context context){
        PhoneTextUtil.addPhoneTextChangeListener(signVo.phone, editText,
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

    private TextChangeLegalCallback getPhoneTextChangeLegalCallback(Context context){
        return new TextChangeLegalCallback() {
            @Override
            public void legal() {
                signVo.isPhoneValid.setValue(true);
                doCheckIsRegistered(context);
            }

            @Override
            public void illegal() {
                signVo.isPhoneValid.setValue(false);
            }
        };
    }

    //---------------------------NetWork---------------------------
    ;
    // checkIsRegistered
    public void doCheckIsRegistered(Context context){
        String phone = signVo.phone.getValue();
        IsRegisterRequest request = new IsRegisterRequest();
        request.phone = phone;
        apiRequestImpl.isPhoneRegistered(
                request,
                response -> {
                    handleCheckIsRegisteredResponse(response, context);
                }
                ,
                throwable -> {
                    NetworkLoadUtils.dismissDialog();
                    Log.e(TAG, "error：" + throwable.getMessage());
                }
        );
    }

    private void handleCheckIsRegisteredResponse(BaseResponse<IsRegisterResponse> response, Context context){
        this.signVo.isRegistered.setValue(Optional.ofNullable(response.getData())
                .map(res -> res.isRegister)
                .orElse(false)
        );
    }

    // sign
    public void doSign(Context context, String phone, String pwd, SyncRequestCallback callback){
        LoginUserRequest request = new LoginUserRequest();
        request.phone = phone;
        request.password = pwd;
        apiRequestImpl.passwordLogin(
                request,
                response -> {
                    handleLogin(response, context, callback);
                },
                callback::onThrowable
        );
    }

    private void handleLogin(BaseResponse<LoginSignResponse> response, Context context, SyncRequestCallback callback){
        LoginSignResponse loginSignResponse = response.getData();
        Long userId = Optional.ofNullable(loginSignResponse)
                .map(re -> re.userVo)
                .map(u -> u.userId)
                .orElse(Constants.ERROR_ID);
        String userAccount = Optional.ofNullable(loginSignResponse)
                .map(re -> re.userVo)
                .map(u -> u.account)
                .orElse("");
        String userPhone = Optional.ofNullable(loginSignResponse)
                .map(re -> re.userVo)
                .map(u -> u.phone)
                .orElse("");
        String userName = Optional.ofNullable(loginSignResponse)
                .map(re -> re.userVo)
                .map(u -> u.userName)
                .orElse("");
        String accessToken = Optional.ofNullable(loginSignResponse)
                .map(re -> re.loginTokenAo)
                .map(t -> t.accessToken)
                .orElse("");
        String refreshToken = Optional.ofNullable(loginSignResponse)
                .map(re -> re.loginTokenAo)
                .map(t -> t.refreshToken)
                .orElse("");
        // save
        Log.i(TAG, "handleLogin: " + response.toJsonString());
        UserLoginInfoAo userLoginInfoAo = MainApplication.getInstance().getUserLoginInfoAo();
        userLoginInfoAo.setUserId(userId);
        userLoginInfoAo.setAccount(userAccount);
        userLoginInfoAo.setPhone(userPhone);
        userLoginInfoAo.setUserName(userName);
        userLoginInfoAo.setLogin(true);
        Log.i(TAG, "设置结果::userLoginInfoAo: " + userLoginInfoAo.toJsonString());
        MainApplication.getInstance().setUserLoginInfoAo(userLoginInfoAo);
        LoginTokenAo loginTokenAo = MainApplication.getInstance().getLoginTokenAo();
        loginTokenAo.accessToken = accessToken;
        loginTokenAo.refreshToken = refreshToken;
        Log.i(TAG, "设置结果::loginTokenAo: " + loginTokenAo.toJsonString());
        MainApplication.getInstance().setLoginTokenAo(loginTokenAo);

        userLoginInfoAo = MainApplication.getInstance().getUserLoginInfoAo();
        Log.i(TAG, "存储结果::userLoginInfoAo: " + userLoginInfoAo.toJsonString());
        loginTokenAo = MainApplication.getInstance().getLoginTokenAo();
        Log.i(TAG, "存储结果::loginTokenAo: " + loginTokenAo.toJsonString());

        // runnable
        callback.onAllRequestSuccess();
    }

/*
    //==========短信登录

    *//**
     * 短信登录
     *//*
    public void doSmsLogin(PhoneLoginInfoRequest phoneLoginRequest){
        this.apiRequestImpl.smsLogin(
                phoneLoginRequest,
                this::handleSmsLogin,
                ViewModelUtil::globalThrowableToast
        );
    }

    *//**
     * 处理短信登录
     * @param response  BaseResponse
     *//*
    private void handleSmsLogin(BaseResponse<LoginSignResponse> response){
        if (ViewModelUtil.handleResponse(response)){

            LoginSignResponse loginSignResponse = response.getData();
            Long userId = Optional.ofNullable(loginSignResponse)
                    .map(re -> re.userVo)
                    .map(u -> u.userId)
                    .orElse(Constants.ERROR_ID);
            String userAccount = Optional.ofNullable(loginSignResponse)
                    .map(re -> re.userVo)
                    .map(u -> u.account)
                    .orElse("");
            String userPhone = Optional.ofNullable(loginSignResponse)
                    .map(re -> re.userVo)
                    .map(u -> u.phone)
                    .orElse("");
            String userName = Optional.ofNullable(loginSignResponse)
                    .map(re -> re.userVo)
                    .map(u -> u.userName)
                    .orElse("");
            String accessToken = Optional.ofNullable(loginSignResponse)
                    .map(re -> re.loginTokenAo)
                    .map(t -> t.accessToken)
                    .orElse("");
            String refreshToken = Optional.ofNullable(loginSignResponse)
                    .map(re -> re.loginTokenAo)
                    .map(t -> t.refreshToken)
                    .orElse("");

            UserLoginInfoAo userLoginInfoAo = MainApplication.getInstance().getUserLoginInfoAo();
            userLoginInfoAo.setUserId(userId);
            userLoginInfoAo.setAccount(userAccount);
            userLoginInfoAo.setPhone(userPhone);
            userLoginInfoAo.setUserName(userName);
            userLoginInfoAo.setLogin(true);
            MainApplication.getInstance().setUserLoginInfoAo(userLoginInfoAo);

            LoginTokenAo loginTokenAo = MainApplication.getInstance().getLoginTokenAo();
            loginTokenAo.accessToken = accessToken;
            loginTokenAo.refreshToken = refreshToken;
            MainApplication.getInstance().setLoginTokenAo(loginTokenAo);
        }
        else {
            MainApplication.getInstance().showGlobalToast(com.czy.customviewlib.R.string.login_failed);
        }
    }
    */
//    private final UserModel userModel = UserModel.getInstance(MainApplication.getInstance());

    //---------------------------Vo---------------------------

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

    //---------------------------logic---------------------------

    // 权限获取
    public void getPermission(FragmentActivity fragmentActivity){
        PermissionUtil.requestPermissionSelectX(
                fragmentActivity,
                new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_PHONE_STATE,
                },
                new String[]{},
                new GainPermissionCallback() {
                    @Override
                    public void allGranted() {

                    }

                    @Override
                    public void notGranted(String[] notGrantedPermissions) {
                        ToastUtils.showToastActivity(
                                fragmentActivity,
                                fragmentActivity.getString(com.czy.customviewlib.R.string.please_give_permission)
                        );
                    }
                }
        );
    }

    // Test
    public void onTestClick(AppCompatActivity activity){
        activity.startActivity(new Intent(activity, MainActivity.class));
    }
}
