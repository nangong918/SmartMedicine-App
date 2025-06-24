package com.czy.smartmedicine.activity;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.czy.appcore.network.api.SyncRequestCallback;
import com.czy.baseUtilsLib.activity.BaseActivity;
import com.czy.baseUtilsLib.network.networkLoad.NetworkLoadUtils;
import com.czy.baseUtilsLib.permission.GainPermissionCallback;
import com.czy.baseUtilsLib.permission.PermissionUtil;
import com.czy.baseUtilsLib.ui.ToastUtils;
import com.czy.baseUtilsLib.viewModel.ViewModelUtil;
import com.czy.dal.ao.intent.RegisterActivityIntentAo;
import com.czy.dal.constant.intent.RegisterActivityType;
import com.czy.dal.vo.fragmentActivity.RegisterVo;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.databinding.ActivityRegisterBinding;
import com.czy.smartmedicine.viewModel.activity.RegisterViewModel;
import com.czy.smartmedicine.viewModel.base.ApiViewModelFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public class RegisterActivity extends BaseActivity<ActivityRegisterBinding> {

    public RegisterActivity() {
        super(RegisterActivity.class);
    }

    @Override
    protected void init() {
        super.init();

        initIntent();

        initViewModel();

        initPictureSelectLauncher();
    }

    @Override
    protected void setListener() {
        super.setListener();

        binding.infoBar.setBack(v -> finish());

        viewModel.onPhoneChanged(binding.edtvPhone, this);
        viewModel.onVcodeChanged(binding.edtvVcode, this);
        viewModel.onPasswordInputEnd(binding.edtvPassword, this);
        viewModel.onConfirmPasswordInputEnd(binding.edtvConfirmPassword, this);

        binding.btnGetVcode.setOnClickListener(v -> {
            if (Boolean.TRUE.equals(viewModel.registerVo.isPhoneValid.getValue())){
                NetworkLoadUtils.showDialog(this);
                viewModel.tryRegisterSendSms(
                        this,
                        new SyncRequestCallback() {
                            @Override
                            public void onThrowable(Throwable throwable) {
                                viewModel.countDownTimerUtil.isStartCountDown = new AtomicBoolean(false);
                                NetworkLoadUtils.dismissDialog();
                            }

                            @Override
                            public void onAllRequestSuccess() {
                                viewModel.countDownTimerUtil.isStartCountDown = new AtomicBoolean(false);
                                NetworkLoadUtils.dismissDialog();
                            }
                        }
                );
            }
        });

        binding.btnConfirm.setOnClickListener(v -> {
            if (Boolean.FALSE.equals(viewModel.registerVo.isPhoneValid.getValue())){
                
                ToastUtils.showToast(this, getString(com.czy.customviewlib.R.string.please_enter_right_phone));
                return;
            }
            else if (Boolean.FALSE.equals(viewModel.registerVo.isVcodeValid.getValue())){
                ToastUtils.showToast(this, getString(com.czy.customviewlib.R.string.please_enter_right_vcode));
                return;
            }
            else if (Boolean.FALSE.equals(viewModel.registerVo.isPwdValid.getValue())){
                String message = getString(com.czy.customviewlib.R.string.pwdNotLegal);
                ToastUtils.showToast(this, message);
                return;
            }
            else if (viewModel.uriAtomicReference == null || viewModel.uriAtomicReference.get() == null){
                String message = getString(com.czy.customviewlib.R.string.avatarNotSelected);
                ToastUtils.showToast(this, message);
                 return;
            }
//            else if (Boolean.FALSE.equals(viewModel.registerVo.isPwdAgainConsist.getValue())){
//                ToastUtils.showToast(this, getString(com.czy.customviewlib.R.string.pwdAgainNotConsist));
//                return;
//            }
            String phone = viewModel.registerVo.phone.getValue();
            String vcode = viewModel.registerVo.vcode.getValue();
            String password = viewModel.registerVo.pwd.getValue();
            String pwdAgain = viewModel.registerVo.pwdAgain.getValue();
            String userName = viewModel.registerVo.userName.getValue();
            String account = viewModel.registerVo.account.getValue();
            if (viewModel.intentAo.activityType == RegisterActivityType.REGISTER.getType()){
                NetworkLoadUtils.showDialog(this);
                viewModel.doRegister(
                        this,
                        phone,
                        vcode,
                        userName,
                        account,
                        pwdAgain,
                        new SyncRequestCallback() {
                            @Override
                            public void onThrowable(Throwable throwable) {
                                NetworkLoadUtils.dismissDialog();
                            }

                            @Override
                            public void onAllRequestSuccess() {
//                                NetworkLoadUtils.dismissDialog();
                                // 立刻上传选择的头像
                            }
                        }
                );
            }
/*            else if (viewModel.intentAo.activityType == RegisterActivityType.RESET_PWD.getType()){
                viewModel.doResetMemberPwd(
                        this,
                        phone,
                        vcode,
                        password,
                        pwdAgain,
                        this::finish
                );
            }*/
        });

        binding.imvgAvatar.setOnClickListener(v -> {
            PermissionUtil.requestPermissionsX(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, new GainPermissionCallback() {
                @Override
                public void allGranted() {
                    com.czy.baseUtilsLib.photo.SelectPhotoUtil.selectImageFromAlbum(selectImageLauncher);
                }

                @Override
                public void notGranted(String[] notGrantedPermissions) {
                    ToastUtils.showToastActivity(RegisterActivity.this, "获取权限失败");
                }
            });
        });
    }


    private RegisterViewModel viewModel;

    private RegisterActivityIntentAo intentAo;

    private void initIntent(){
        try {
            intentAo = (RegisterActivityIntentAo) getIntent().getSerializableExtra(
                    RegisterActivityIntentAo.INTENT_KEY
            );
        } catch (Exception e){
            Log.e(TAG, "initIntent error", e);
            finish();
        }
    }

    private void initViewModel(){
        ApiViewModelFactory apiViewModelFactory = new ApiViewModelFactory(MainApplication.getApiRequestImplInstance(), MainApplication.getInstance().getMessageSender());
        viewModel = ViewModelUtil.newViewModel(this, apiViewModelFactory, RegisterViewModel.class);

        initViewModelVo();

        observeLivedata();

        // 绑定viewModel
        binding.setViewModel(viewModel);
        // 设置监听者
        binding.setLifecycleOwner(this);
    }

    private void initViewModelVo() {
        RegisterVo registerVo = new RegisterVo();

        viewModel.intentAo = intentAo;
        viewModel.registerVo.phone.setValue(intentAo.phone);

        viewModel.initVo(registerVo);
    }

    private void observeLivedata() {
        // 观察验证码发送值
        viewModel.registerVo.vcodeCountDown.observe(this, vcodeCountDown -> {
            if (vcodeCountDown > 0){
                String time = vcodeCountDown + "s";
                binding.btnGetVcode.setText(time);
            }
            else {
                binding.btnGetVcode.setText(getText(com.czy.customviewlib.R.string.get_vcode));
            }
            checkConfirmIsEnable();
        });

        // 观察密码是否合法
        viewModel.registerVo.isPwdValid.observe(this, isPwdValid -> {
            checkConfirmIsEnable();
        });

        // 观察两次密码是否一致
        viewModel.registerVo.isPwdAgainConsist.observe(this, isPwdAgainConsist -> {
            checkConfirmIsEnable();
        });

        // 观察验证码输入
        viewModel.registerVo.isVcodeValid.observe(this, isVcodeValid -> {
            checkConfirmIsEnable();
        });

        // 观察是否可以注册、重置密码
        viewModel.registerVo.isConfirmBtnEnable.observe(this,
                isConfirmBtnEnable -> {
                    binding.btnConfirm.setBackgroundResource(
                            isConfirmBtnEnable ?
                                    com.czy.customviewlib.R.drawable.button_selected :
                                    com.czy.customviewlib.R.drawable.button_not_select
                    );
//                    binding.btnConfirm.setClickable(isConfirmBtnEnable);
                }
        );

    }

    private void checkConfirmIsEnable(){
        boolean enable =
                Boolean.TRUE.equals(viewModel.registerVo.isPhoneValid.getValue())
                        && Boolean.TRUE.equals(viewModel.registerVo.isVcodeValid.getValue())
                        && Boolean.TRUE.equals(viewModel.registerVo.isPwdValid.getValue())
                        && Boolean.TRUE.equals(viewModel.registerVo.isPwdAgainConsist.getValue());
        if (enable){
            viewModel.registerVo.isConfirmBtnEnable.setValue(true);
        }
        else {
            viewModel.registerVo.isConfirmBtnEnable.setValue(false);
        }
    }

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
                            binding.imvgAvatar.setImageBitmap(bitmap);
                        }
                    }
                }
        );
    }
}