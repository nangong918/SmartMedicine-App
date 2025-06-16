package com.czy.baseUtilsLib.activity;



import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewbinding.ViewBinding;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


/**
 * 解决ViewBinding重复代码    （通过反射实现）
 * @param <viewBinding>     视图绑定类型
 */
public class BaseActivity<viewBinding extends ViewBinding> extends AppCompatActivity {
    protected viewBinding binding;
    protected final String activityName;
    protected final String TAG;

//    public BaseActivity(String activityName){
//        this.activityName = activityName;
//        TAG = activityName;
//    }

    public BaseActivity(Class<?> classType){
        this.activityName = classType.getSimpleName();
        TAG = activityName;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViewBinding();

        init();
        initData();
        setListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // TODO 再考虑
//        initView();
    }

    private void initViewBinding(){
        // 反射获取泛型的父类型即：BaseViewBindActivity<viewBinding extends ViewBinding>    （BaseViewBindActivity<ActivityMainBinding>）
        Type superclass = getClass().getGenericSuperclass();
        // 获取参数类型也就是 BaseViewBindActivity<ActivityMainBinding> -> ActivityMainBinding
        if(superclass == null){return;}
        Class<?> aClass = (Class<?>) ((ParameterizedType) superclass).getActualTypeArguments()[0];

        try {
            // 反射获取 ActivityMainBinding.inflate方法；inflate是方法名称，LayoutInflater.class是方法传入参数
            Method method = aClass.getDeclaredMethod("inflate", LayoutInflater.class);
            // 填充Binding
            binding = (viewBinding) method.invoke(null, getLayoutInflater());
            assert binding != null;
            setContentView(binding.getRoot());
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "inflate 方法未找到: " + aClass.getSimpleName(), e);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "无法访问 inflate 方法: " + aClass.getSimpleName(), e);
        } catch (InvocationTargetException e) {
            Log.e(TAG, "调用 inflate 方法时发生异常: " + aClass.getSimpleName() + "，原因: " + e.getCause(), e);
        } catch (Exception e) {
            Log.e(TAG, "发生未知错误: " + e.getMessage(), e);
        }
    }

    private void initWindow(){

/*        // 状态栏透明
        StatusBarUtil.setTranslucentStatusBar(this);

        // 隐藏顶部栏
        StatusBarUtil.setHideStatusBar(this);*/

        // 设置状态栏透明 （设置下面会让layout无法适应下拉列表放着输入框遮挡）
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//
//        // 隐藏状态栏 （时间wifi等信息）
//        View decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
//                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
//                View.SYSTEM_UI_FLAG_FULLSCREEN);

        EdgeToEdge.enable(this);

        // 处理窗口插入
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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
