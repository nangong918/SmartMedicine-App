package com.czy.smartmedicine.activity;


import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.czy.appcore.BaseConfig;
import com.czy.baseUtilsLib.activity.ActivityLaunchUtils;
import com.czy.baseUtilsLib.activity.BaseActivity;
import com.czy.baseUtilsLib.ui.ToastUtils;
import com.czy.dal.constant.Constants;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.databinding.ActivityStartBinding;

import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

public class StartActivity extends BaseActivity<ActivityStartBinding> {

    public StartActivity() {
        super(StartActivity.class);
    }

    // 启动页不用viewModel，请求不用livedata观察响应而是callback
    @Override
    protected void init() {
        super.init();

        initTimer();
    }

    // 请求缓存数据，用户数据，用refreshToken刷新accessToken，验证刷新token是否过期等等

    //-------------------------------定时跳转-------------------------------

    private Timer timer;
    private TimerTask timerTask;
    // 2秒的冷却

    private void initTimer(){
        // 创建 Timer 对象
        timer = new Timer();

        // 创建 TimerTask 对象
        timerTask = new TimerTask() {
            @Override
            public void run() {
                // 在这里执行跳转到 MainActivity 的逻辑
                activityTurn();
            }
        };

        // 2 秒后执行 TimerTask
        timer.schedule(timerTask, BaseConfig.DELAY_TIME);
    }

    private void destroyTimer(){
        // 确保在 Activity 销毁时取消 Timer
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    private Long userId = null;

    // 检查是否未登录 ;后面改为验证refreshToken
    private boolean checkIsNotLogin(){
        userId = Optional.ofNullable(MainApplication.getInstance())
                .map(MainApplication::getUserLoginInfoAo)
                .map(ao -> ao.userId)
                .orElse(null);
        Log.i(TAG, "检查是否未登录::userId: " + userId);
        return userId != null && !Constants.ERROR_ID.equals(userId);
    }

    // 跳转页面
    private void activityTurn(){
        if (checkIsNotLogin()){
            Intent intent = new Intent(StartActivity.this, SignActivity.class);
            ActivityLaunchUtils.launchNewTask(this, intent, null);
            finish();
        }
        else {
            try {
                assert userId != null;
                // 启用登录长连接
                MainApplication.getInstance().startNettySocketService(userId);
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                ActivityLaunchUtils.launchNewTask(this, intent, null);
                finish();
            } catch (Exception e){
                Log.d(TAG, "登录长连接失败");
                ToastUtils.showToastActivity(this, "登录长连接失败");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyTimer();
    }
}