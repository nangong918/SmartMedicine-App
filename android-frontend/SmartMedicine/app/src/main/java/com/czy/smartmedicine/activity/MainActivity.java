package com.czy.smartmedicine.activity;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.czy.appview.view.DialogConfirm;
import com.czy.baseutil.activity.ActivityLaunchUtils;
import com.czy.baseutil.activity.BaseActivity;
import com.czy.baseutil.image.ImageLoadUtil;
import com.czy.domain.ao.chat.UserLoginInfoAo;
import com.czy.domain.constant.SelectItemEnum;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.databinding.ActivityMainBinding;
import com.czy.smartmedicine.fragment.MessageFragment;
import com.czy.smartmedicine.fragment.MineFragment;
import com.czy.smartmedicine.fragment.home.HomeFragment;
import com.czy.smartmedicine.fragment.medicine.MedicineFragment;

import java.util.Optional;

/**
 * @author 13225
 * 主界面
 */
public class MainActivity extends BaseActivity<ActivityMainBinding> {

    static {
        System.loadLibrary("smartmedicine");
    }

    public MainActivity() {
        super(MainActivity.class);
    }

    @Override
    public ActivityMainBinding initBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void init() {
        super.init();
        initView();
        initMotionEvent();
        initFragment();
    }

    //---------------------------view---------------------------

    private void initView(){
        exitDialog = new DialogConfirm(this);
        exitDialog.setContent(getString(com.czy.appview.R.string.are_you_sure_to_exit),0xFFF94040);

        View headerView = binding.mainNavigationView.getHeaderView(0);
        TextView nameTv = headerView.findViewById(com.czy.appview.R.id.nagi_name);
        TextView accountTv = headerView.findViewById(com.czy.appview.R.id.nagi_account);
        TextView phoneTv = headerView.findViewById(com.czy.appview.R.id.nagi_phone);
        ImageView avatarIv = headerView.findViewById(com.czy.appview.R.id.nagi_logo);

        UserLoginInfoAo userLoginInfoAo = MainApplication.getInstance().getUserLoginInfoAo();

        String account = Optional.ofNullable(userLoginInfoAo)
                .map(ao -> ao.account)
                .orElse("");
        String name = Optional.ofNullable(userLoginInfoAo)
                .map(ao -> ao.userName)
                .orElse(account);
        String phone = Optional.ofNullable(userLoginInfoAo)
                .map(ao -> ao.phone)
                .orElse("");
        String avatarUrl = Optional.ofNullable(userLoginInfoAo)
                .map(ao -> ao.avatarUrl)
                .orElse("");

        // 设置文本
        nameTv.setText(name);
        accountTv.setText(account);
        phoneTv.setText(phone);

        // 加载头像
        if (!TextUtils.isEmpty(avatarUrl)){
            ImageLoadUtil.loadImageViewByResource(
                    avatarUrl,
                    avatarIv
            );
        }

    }

    @Override
    protected void setListener() {
        super.setListener();
        binding.mainBottomBar.clickListener(position -> {
            SelectItemEnum fragmentType = SelectItemEnum.HOME;
            try {
                fragmentType = SelectItemEnum.getItem(position);
            } catch (Exception ignored){
            }
            changeFragment(fragmentType);
        });

        // MainActivity给HomeFragment初始化点击事件
        MainApplication.onHomeSearchAvatarClicked = () -> {
            if (!binding.mainDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.mainDrawerLayout.openDrawer(GravityCompat.START);
//                ToastUtils.showToastActivity(this, "打开了抽屉");
            } else {
                binding.mainDrawerLayout.closeDrawer(GravityCompat.START);
//                ToastUtils.showToastActivity(this, "关闭了抽屉");
            }
        };

        binding.mainNavigationView.setNavigationItemSelectedListener(item -> {
            if (com.czy.appview.R.id.setting_logOut == item.getItemId()){
                exitDialog.show();
                return true;
            }
            // else if
            return false;
        });

        // 登出
        exitDialog.setButtonClickListener(v -> {
            exitDialog.dismiss();
            // 清除用户信息
            MainApplication.getInstance().clearAllUserData();
            // 断开socket
            MainApplication.getInstance().disconnectNettySocketService();
            // 跳转到登录界面
            Intent exitIntent = new Intent(this, SignActivity.class);
            ActivityLaunchUtils.launchNewTask(this, exitIntent, null);
            finish();
        });
    }

    private DialogConfirm exitDialog;

    //---------------------------fragment---------------------------

    private FragmentManager fragmentManager;

    private void initFragment(){
        fragmentManager = getSupportFragmentManager();

        SelectItemEnum fragmentType = SelectItemEnum.HOME;
        try {
            if (getIntent().hasExtra(SelectItemEnum.INTENT_EXTRA_NAME)){
                fragmentType = (SelectItemEnum) getIntent().getSerializableExtra(SelectItemEnum.INTENT_EXTRA_NAME);
            }
        } catch (Exception ignored){
        }

        changeFragment(fragmentType);
    }

    private void changeFragment(SelectItemEnum fragmentType){
        if (fragmentType != null){
            switch(fragmentType){
                case HOME -> {
                    setStatusBarColor(
                            com.czy.appview.R.color.green_90
                    );
                    turnToTargetFragment(SelectItemEnum.HOME, HomeFragment.class, null);
                }
                case MEDICAL -> {
                    setStatusBarColor(
                            com.czy.appview.R.color.green_0
                    );
                    turnToTargetFragment(SelectItemEnum.MEDICAL, MedicineFragment.class, null);
                }
                case MESSAGE -> {
                    setStatusBarColor(
                            com.czy.appview.R.color.green_0
                    );
                    turnToTargetFragment(SelectItemEnum.MESSAGE, MessageFragment.class, null);
                }
                case MINE -> {
                    setStatusBarColor(
                            com.czy.appview.R.color.green_0
                    );
                    turnToTargetFragment(SelectItemEnum.MINE, MineFragment.class, null);
                }
            }
        }
    }

    public void turnToTargetFragment(SelectItemEnum fragmentType, Class<? extends Fragment> clazz, Bundle args){
        binding.mainBottomBar.setSelected(fragmentType);

        Fragment newFragment = null;

        try {
            // 如果没有参数，使用无参构造函数
            newFragment = clazz.getConstructor().newInstance();
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "No such constructor", e);
        } catch (Exception e) {
            Log.e(TAG, "Error creating fragment", e);
        }

        if (newFragment != null) {
            // 如果需要，可以为 Fragment 设置参数
            if (args != null) {
                newFragment.setArguments(args);
            }

            // 使用Add替代replace、Navigation
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(binding.fragmentContainer.getId(), newFragment);
            transaction.commit();
        }
    }

    //-------------------------------MotionEvent拦截-------------------------------

    private void initMotionEvent(){
        // 添加返回键处理
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (binding.mainDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    // 如果抽屉打开，则关闭它
                    binding.mainDrawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    // 否则执行默认的返回操作
                    setEnabled(false); // 禁用这个回调
                }
            }
        });
    }
}