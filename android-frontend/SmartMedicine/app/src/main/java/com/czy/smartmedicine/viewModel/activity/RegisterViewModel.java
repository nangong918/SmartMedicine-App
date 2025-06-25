package com.czy.smartmedicine.viewModel.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.czy.appcore.BaseConfig;
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
import com.czy.dal.dto.http.request.SendSmsRequest;
import com.czy.dal.dto.http.response.SendSmsResponse;
import com.czy.dal.dto.http.response.UserRegisterResponse;
import com.czy.dal.vo.entity.UserVo;
import com.czy.dal.vo.fragmentActivity.RegisterVo;
import com.czy.datalib.networkRepository.ApiRequestImpl;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.test.TestConfig;
import com.czy.smartmedicine.utils.ResponseTool;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


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
    //===========Picture

    public AtomicReference<Uri> uriAtomicReference = new AtomicReference<>(null);
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

    private void handleRegisterResponse(BaseResponse<UserRegisterResponse> response, Context context, SyncRequestCallback callback) {
        if (response.getData().snowflakeId == null){
            callback.onThrowable(new Throwable("注册失败, id 存在问题"));
        }
        uploadAvatar(context, response.getData().snowflakeId, callback);
    }

    public void uploadAvatar(Context context,
                             Long userId,
                             SyncRequestCallback callback){
        Bitmap bitmap = MainApplication.getInstance().getImageManager().uriToBitmapMediaStore(context, this.uriAtomicReference.get());
        bitmap = MainApplication.getInstance().getImageManager().processImage(bitmap, BaseConfig.BITMAP_MAX_SIZE_AVATAR);
        // Http Send
        File imageFile = null;
        // 确保您在这里传入正确的 Uri
//        Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), imageName));
        imageFile = MainApplication.getInstance().getImageManager().bitmapToFile(bitmap, uriAtomicReference.get(), context);
        if (imageFile == null || !imageFile.exists()) {
            // 处理文件未创建或路径不正确的情况
            Log.e(TAG, "Image file creation failed");
            return;
        }
        // 获取文件名
        String originalFilename = imageFile.getName(); // 使用 getName() 获取文件名
        // 获取文件扩展名
        String fileExtension = originalFilename.contains(".") ?
                originalFilename.substring(originalFilename.lastIndexOf(".")) : ""; // 获取扩展名

        MultipartBody.Part filePart = com.czy.baseUtilsLib.file.FileUtil.createMultipartBodyPart(imageFile);
        // 文件名称，方便后端保存
        String phone = Optional.ofNullable(registerVo.phone)
                        .map(LiveData::getValue)
                        .orElse("");
        apiRequestImpl.registerUserUploadImg(
                filePart,
                RequestBody.create(MediaType.parse("text/plain"), phone),
                // 前端发送userId的string
                RequestBody.create(MediaType.parse("text/plain"), String.valueOf(userId))
                , response -> {
                    ResponseTool.handleSyncResponseEx(
                            response,
                            context,
                            callback,
                            this::handleFileUpload
                    );
                }
                , callback::onThrowable
        );
    }

    private void handleFileUpload(BaseResponse<UserVo> userVoBaseResponse, Context context, SyncRequestCallback callback) {
        // 后面userVo可能有用，目前还未开放，毕竟user的全部vo信息都在这里了
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
