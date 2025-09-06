package com.czy.baseutil.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import java.util.Optional;


/**
 * 解决ViewBinding重复代码    （通过反射实现）
 * @param <VB>     视图绑定类型
 */
public abstract class BaseActivity<VB extends ViewBinding> extends AppCompatActivity {
    protected VB binding;
    protected final String activityName;
    protected final String TAG;

//    public BaseActivity(String activityName){
//        this.activityName = activityName;
//        TAG = activityName;
//    }

    public abstract VB initBinding();

    public BaseActivity(Class<?> classType){
        this.activityName = classType.getSimpleName();
        TAG = activityName;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.binding = initBinding();
        setContentView(binding.getRoot());

        init();
        initData();
        setListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        initView();
    }

    private void initWindow(){

//        //去除时间和电量等
//        getWindow().setFlags(
//                WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN
//        );


        //去除标题导航栏
        Optional.ofNullable(getSupportActionBar())
                .ifPresent(ActionBar::hide);

        // 获取 DecorView
        ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
        int count = decorView.getChildCount();

        // 判断是否已经添加了 StatusBarView
        if (count > 0 && decorView.getChildAt(count - 1) instanceof StatusBarView) {
            decorView.getChildAt(count - 1).setBackgroundColor(calculateStatusColor(Color.BLUE, 255)); // 你可以自定义颜色和透明度
        } else {
            // 创建并添加 StatusBarView
            statusBarView = createStatusBarView(this, statusBarColor, statusBarAlpha); // 你可以自定义颜色和透明度
            decorView.addView(statusBarView);
        }

        // 获取根视图并设置窗口插图
        ViewGroup rootView = (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        rootView.setOnApplyWindowInsetsListener((v, insets) -> {
            int statusBarHeight = insets.getSystemWindowInsetTop();
            v.setPadding(0, statusBarHeight, 0, 0); // 设置顶部填充以适应状态栏
            return insets;
        });
    }

    private StatusBarView statusBarView;
    private int statusBarColor = Color.BLUE;
    private int statusBarAlpha = 0;

    public void setStatusBarColor(int color, int alpha){
        statusBarColor = color;
        statusBarAlpha = alpha;
        Optional.ofNullable(statusBarView)
                .ifPresent(
                        v -> v.setBackgroundColor(calculateStatusColor(color, alpha))
                );
    }

    // 创建 StatusBarView
    private static StatusBarView createStatusBarView(Activity activity, int color, int alpha) {
        StatusBarView statusBarView = new StatusBarView(activity);
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity));
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(calculateStatusColor(color, alpha));
        return statusBarView;
    }

    // 获取状态栏高度
    @SuppressLint({"DiscouragedApi", "InternalInsetResource"})
    private static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    // 计算状态栏颜色
    private static int calculateStatusColor(int color, int alpha) {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    // 将头部的Bar隐藏

    protected void init(){
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        closeKeyBoard();
        return super.onTouchEvent(event);
    }

    // 点击其他位置关闭输入框
    public void closeKeyBoard() {
        if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
            View v = getCurrentFocus();
            closeSoftInput(this, v);
        }
    }

    // 关闭键盘输入法
    public static void closeSoftInput(Context context, View v) {
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }


    protected void setListener(){

    }

    protected void initData(){

    }

    @Override
    protected void onResume() {
        super.onResume();
        initWindow();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
