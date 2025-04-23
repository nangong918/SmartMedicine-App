package com.czy.easysocial.activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;


import com.czy.baseUtilsLib.activity.ActivityLaunchUtils;
import com.czy.baseUtilsLib.activity.BaseActivity;
import com.czy.baseUtilsLib.viewModel.ViewModelUtil;
import com.czy.dal.vo.viewModeVo.sign.SignVo;
import com.czy.easysocial.MainApplication;
import com.czy.easysocial.databinding.ActivitySignBinding;
import com.czy.easysocial.test.TestConfig;
import com.czy.easysocial.viewModel.ApiViewModelFactory;
import com.czy.easysocial.viewModel.SignViewModel;

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
    }


    //---------------------------listener---------------------------

    @SuppressLint("ResourceAsColor")
    @Override
    protected void setListener() {
        super.setListener();

        // 倒计时
        binding.btvGetCode.setOnClickListener(v -> {
            viewModel.onGetCodeClick();
        });

        // 验证码
        viewModel.onCodeChange(binding.edtvCode);

        // 手机号合法监听
        viewModel.onPhoneChange(binding.edtvPhone);

        // 隐私协议
        binding.tvPrivacy.setOnClickListener(v -> {
            // 跳转隐私协议
        });

        // 登录
        binding.btvLogin.setOnClickListener(v -> {
            viewModel.onLoginClick(() -> {
                ActivityLaunchUtils.launchNewTask(this, MainActivity.class, null);
            });
        });

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

    //---------------------------viewModel---------------------------

    private SignViewModel viewModel;

    private void initViewModel(){
        ApiViewModelFactory apiViewModelFactory = new ApiViewModelFactory(MainApplication.getApiRequestImplInstance(), MainApplication.getInstance().getMessageSender());
        viewModel = ViewModelUtil.newViewModel(this, apiViewModelFactory, SignViewModel.class);

        initViewModelVo();

        // 绑定viewModel
        binding.setViewModel(viewModel);
        // 设置监听者
        binding.setLifecycleOwner(this);
    }

    private void initViewModelVo(){
        // SignVO
        SignVo signVo = new SignVo();

        signVo.imgvAgreeImageResource.setValue(com.czy.customviewlib.R.mipmap.circle4);
        signVo.btvGetCodeText.setValue(getString(com.czy.customviewlib.R.string.get_vcode));
        signVo.btvGetCodeBackground.setValue(com.czy.customviewlib.R.drawable.button_grey_not_select);
        signVo.btvLoginBackground.setValue(com.czy.customviewlib.R.drawable.button_not_select);
        signVo.edtvPhoneTextColor.setValue(com.czy.customviewlib.R.color.text_color);
        signVo.edtvCodeTextColor.setValue(com.czy.customviewlib.R.color.text_color);
        signVo.vCodeBackgroundColor.setValue(com.czy.customviewlib.R.color.grey_line_color);
        signVo.tvAgreeTextColor.setValue(com.czy.customviewlib.R.color.text_color);
        signVo.tvPrivacyTextColor.setValue(com.czy.customviewlib.R.color.origin_text_color);
        signVo.edtvPhone.setValue("");
        signVo.edtvCode.setValue("");

        viewModel.init(signVo);
    }
}