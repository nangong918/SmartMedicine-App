package com.czy.smartmedicine.activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.czy.appcore.network.api.SyncRequestCallback;
import com.czy.baseUtilsLib.activity.ActivityLaunchUtils;
import com.czy.baseUtilsLib.activity.BaseActivity;
import com.czy.baseUtilsLib.network.networkLoad.NetworkLoadUtils;
import com.czy.baseUtilsLib.ui.ToastUtils;
import com.czy.baseUtilsLib.viewModel.ViewModelUtil;
import com.czy.customviewlib.view.DialogPrompt;
import com.czy.dal.ao.intent.RegisterActivityIntentAo;
import com.czy.dal.constant.intent.RegisterActivityType;
import com.czy.dal.vo.fragmentActivity.SignVo;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.R;
import com.czy.smartmedicine.databinding.ActivitySignBinding;
import com.czy.smartmedicine.test.TestConfig;
import com.czy.smartmedicine.viewModel.base.ApiViewModelFactory;
import com.czy.smartmedicine.viewModel.activity.SignViewModel;

/**
 *  @author 13225
 */
public class SignActivity extends BaseActivity<ActivitySignBinding> {

    public SignActivity() {
        super(SignActivity.class);
    }

    //---------------------------init---------------------------

    @Override
    protected void init() {
        super.init();

        initViewModel();

        initRegisterLauncher();

        initDialogPrompt();
    }


    //---------------------------listener---------------------------

    @SuppressLint("ResourceAsColor")
    @Override
    protected void setListener() {
        super.setListener();

        // 监听手机号变化
        viewModel.onPhoneChanged(binding.edtvPhone, this);

        // 注册
        binding.btnRegister.setOnClickListener(v -> {
            register();
        });

        // 登录
        binding.btnLogin.setOnClickListener(v -> {
            login();
        });

        // 忘记密码
        binding.btnForgetPassword.setOnClickListener(v -> {
            if (Boolean.TRUE.equals(viewModel.signVo.isRegistered.getValue())){
                resetPassword();
            }
            else {
                ToastUtils.showToast(this, getString(com.czy.customviewlib.R.string.please_register_first));
            }
        });

        View.OnClickListener agreeClickListener = v -> {
            boolean isAgree = Boolean.TRUE.equals(viewModel.signVo.isAgree.getValue());
            viewModel.signVo.isAgree.setValue(!isAgree);
        };

        // 同意隐私协议
        binding.vAgree.setOnClickListener(agreeClickListener);
        binding.vAgreeClickZone.setOnClickListener(agreeClickListener);
        binding.tvAgree.setOnClickListener(agreeClickListener);

        // Test
        binding.btvTest.setVisibility(TestConfig.IS_TEST ? View.VISIBLE : View.GONE);
        if (TestConfig.IS_TEST) {
            binding.btvTest.setOnClickListener(v -> {
                viewModel.onTestClick(this);
            });
        }

        binding.btvTest2.setVisibility(TestConfig.IS_TEST ? View.VISIBLE : View.GONE);
        if (TestConfig.IS_TEST) {
            binding.btvTest2.setOnClickListener(v -> {
                startActivity(new Intent(this, TestActivity.class));
            });
        }
    }

    private void initDialogPrompt() {
        dialogPrompt = new DialogPrompt(this);

        dialogPrompt.setButtonClickListener(v -> {
            register();
        });
    }

    private DialogPrompt dialogPrompt;

    private void register(){
//        if (Boolean.FALSE.equals(viewModel.signVo.isAgree.getValue())){
//            Toast.makeText(this, getString(com.czy.customviewlib.R.string.please_agree_privacy_and_agreement), Toast.LENGTH_SHORT).show();
//            return;
//        }
        Intent intent = new Intent(this, RegisterActivity.class);
        RegisterActivityIntentAo ao = new RegisterActivityIntentAo();
        ao.activityType = RegisterActivityType.REGISTER.getType();
        ao.phone = viewModel.signVo.phone.getValue();
        intent.putExtra(RegisterActivityIntentAo.INTENT_KEY, ao);
        registerActivityResultLauncher.launch(intent);
        dialogPrompt.dismiss();
    }

    private void login(){
//        if (Boolean.FALSE.equals(viewModel.signVo.isAgree.getValue())){
//            Toast.makeText(this, getString(com.czy.customviewlib.R.string.please_agree_privacy_and_agreement), Toast.LENGTH_SHORT).show();
//            return;
//        }
        NetworkLoadUtils.showDialog(this);
        viewModel.doSign(
                this,
                viewModel.signVo.phone.getValue(),
                viewModel.signVo.pwd.getValue(),
                new SyncRequestCallback() {
                        @Override
                        public void onThrowable(Throwable throwable) {
                            NetworkLoadUtils.dismissDialog();
                        }

                        @Override
                        public void onAllRequestSuccess() {
                            NetworkLoadUtils.dismissDialog();
                            Intent intent = new Intent(SignActivity.this, MainActivity.class);
                            ActivityLaunchUtils.launchNewTask(SignActivity.this, intent, null);
                        }
                    }
                );
    }

    private void resetPassword(){
        Intent intent = new Intent(this, RegisterActivity.class);
        RegisterActivityIntentAo ao = new RegisterActivityIntentAo();
        ao.activityType = RegisterActivityType.RESET_PWD.getType();
        ao.phone = viewModel.signVo.phone.getValue();
        intent.putExtra(RegisterActivityIntentAo.INTENT_KEY, ao);
        registerActivityResultLauncher.launch(intent);
        dialogPrompt.dismiss();
    }

    private ActivityResultLauncher<Intent> registerActivityResultLauncher;

    private void initRegisterLauncher(){
        registerActivityResultLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        result -> {
                            viewModel.doCheckIsRegistered(this);
                        }
                );
    }

    //---------------------------viewModel---------------------------

    private SignViewModel viewModel;

    private void initViewModel(){
        ApiViewModelFactory apiViewModelFactory = new ApiViewModelFactory(MainApplication.getApiRequestImplInstance(), MainApplication.getInstance().getMessageSender());
        viewModel = ViewModelUtil.newViewModel(this, apiViewModelFactory, SignViewModel.class);

        initViewModelVo();

        // observeLivedata
        observeLivedata();

        // 绑定viewModel
        binding.setViewModel(viewModel);
        // 设置监听者
        binding.setLifecycleOwner(this);

        // 获取权限
        viewModel.getPermission(this);
    }

    private void initViewModelVo(){
        SignVo signVo = new SignVo();

        signVo.isRegistered.setValue(false);
        signVo.isAgree.setValue(false);
        signVo.isPhoneValid.setValue(false);
        signVo.isShowPwd.setValue(false);
        signVo.phone.setValue("");
        signVo.pwd.setValue("");

        viewModel.initVo(signVo);
    }

    private void observeLivedata(){
        viewModel.signVo.isPhoneValid.observe(this, isPhoneValid -> {
            binding.btnLogin.setBackgroundResource(
                    isPhoneValid ?
                            com.czy.customviewlib.R.drawable.button_selected :
                            com.czy.customviewlib.R.drawable.button_not_select
            );
            binding.btnRegister.setBackgroundResource(
                    isPhoneValid ?
                            com.czy.customviewlib.R.drawable.button_selected :
                            com.czy.customviewlib.R.drawable.button_not_select
            );
            binding.btnLogin.setClickable(isPhoneValid);
            binding.btnRegister.setClickable(isPhoneValid);
        });

        viewModel.signVo.isRegistered.observe(this, isRegistered -> {
            // 合法前提
            if (Boolean.TRUE.equals(viewModel.signVo.isPhoneValid.getValue())){

                binding.btnLogin.setVisibility(
                        isRegistered ?
                                View.VISIBLE :
                                View.GONE

                );
                binding.btnRegister.setVisibility(
                        isRegistered ?
                                View.GONE :
                                View.VISIBLE
                );
                if (isRegistered){
                    binding.edtvPassword.setVisibility(View.VISIBLE);
                }
                else {
                    binding.edtvPassword.setVisibility(View.GONE);
                    dialogPrompt.show();
                }
            }
            else {
                binding.btnLogin.setVisibility(View.VISIBLE);
                binding.btnRegister.setVisibility(View.GONE);
                binding.edtvPassword.setVisibility(View.GONE);
            }

            binding.lyPassword.setVisibility(isRegistered ? View.VISIBLE : View.GONE);
        });

        viewModel.signVo.isAgree.observe(this, isAgree -> {
            binding.vAgree.setImageResource(
                    isAgree ?
                            com.czy.customviewlib.R.mipmap.o :
                            com.czy.customviewlib.R.drawable.circle_corners_bg_grey
            );
        });
    }
}