package com.czy.smartmedicine.activity;



import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.czy.baseUtilsLib.activity.ActivityLaunchUtils;
import com.czy.baseUtilsLib.activity.BaseActivity;
import com.czy.customviewlib.view.DialogConfirm;
import com.czy.dal.constant.SelectItemEnum;
import com.czy.dal.vo.view.mainTop.MainTopBarVo;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.R;
import com.czy.smartmedicine.databinding.ActivityMainBinding;
import com.czy.smartmedicine.fragment.AiFragment;
import com.czy.smartmedicine.fragment.friends.FriendsFragment;
import com.czy.smartmedicine.fragment.HomeFragment;
import com.czy.smartmedicine.fragment.MessageFragment;
import com.czy.smartmedicine.fragment.NoticeFragment;
import com.czy.smartmedicine.fragment.SearchFragment;

import java.util.Optional;

/**
 * @author 13225
 * Android Mvvm设计模式：
 * Model：Activity，Fragment：负责交互；LiveData监听
 * ViewModel：数据双向绑定；LiveData保存；逻辑
 * View：展示
 */
public class MainActivity extends BaseActivity<ActivityMainBinding> {

    static {
        System.loadLibrary("smartmedicine");
    }

    public MainActivity() {
        super(MainActivity.class);
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
        exitDialog.setContent(getString(com.czy.customviewlib.R.string.are_you_sure_to_exit),0xFFF94040);

        View headerView = binding.mainNavigationView.getHeaderView(0);
        TextView nagiText = headerView.findViewById(com.czy.customviewlib.R.id.nagi_text);

        String accountName = Optional.ofNullable(MainApplication.getInstance().getUserLoginInfoAo())
                .map(userLoginInfoAo -> userLoginInfoAo.account)
                .orElse("");
        // 设置文本
        nagiText.setText(accountName);
    }

    public void setMainTopBar(@NonNull MainTopBarVo mainTopBarVo) {
        binding.mainTopBar.setView(mainTopBarVo);
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

        binding.mainTopBar.setImageClickListener(v -> {
            if (!binding.mainDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.mainDrawerLayout.openDrawer(GravityCompat.START);
//                ToastUtils.showToastActivity(this, "打开了抽屉");
            } else {
                binding.mainDrawerLayout.closeDrawer(GravityCompat.START);
//                ToastUtils.showToastActivity(this, "关闭了抽屉");
            }
        });

        binding.mainNavigationView.setNavigationItemSelectedListener(item -> {
            if (com.czy.customviewlib.R.id.setting_logOut == item.getItemId()){
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
            MainApplication.getInstance().clearUserLoginInfoAo();
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
                    turnToTargetFragment(SelectItemEnum.HOME, HomeFragment.class, null);
                }
                case SEARCH -> {
                    turnToTargetFragment(SelectItemEnum.SEARCH, SearchFragment.class, null);
                }
                case AI -> {
                    turnToTargetFragment(SelectItemEnum.AI, AiFragment.class, null);
                }
                case FRIENDS -> {
                    turnToTargetFragment(SelectItemEnum.FRIENDS, FriendsFragment.class, null);
                }
                case NOTIFICATIONS -> {
                    turnToTargetFragment(SelectItemEnum.NOTIFICATIONS, NoticeFragment.class, null);
                }
                case MESSAGE -> {
                    turnToTargetFragment(SelectItemEnum.MESSAGE, MessageFragment.class, null);
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