package com.czy.baseUtilsLib.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


public class BaseFragment<viewBinding extends ViewBinding> extends Fragment {
    protected viewBinding binding;
    protected String TAG;

    // 通常情况下，只要fragment执行了onCreateView方法，Fragment就是isAdded Activity
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initViewBinding(inflater, container);
        init();
        setListener();
        return binding.getRoot();
    }

    protected String fragmentName;

    public BaseFragment(Class<?> classType){
        this.fragmentName = classType.getSimpleName();
        TAG = this.fragmentName;
    }

    private void initViewBinding(LayoutInflater inflater, ViewGroup container) {
        // 获取泛型的父类型
        Type superclass = getClass().getGenericSuperclass();
        // 获取参数类型
        if (superclass == null){return;}
        Class<?> aClass = (Class<?>) ((ParameterizedType) superclass).getActualTypeArguments()[0];

        try {
            // 反射获取 inflate 方法
            Method method = aClass.getDeclaredMethod("inflate", LayoutInflater.class, ViewGroup.class, boolean.class);
            // 填充Binding
            binding = (viewBinding) method.invoke(null, inflater, container, false);
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

    protected void init() {
        // 初始化逻辑
    }

    protected void setListener() {
        // 设置监听逻辑
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
