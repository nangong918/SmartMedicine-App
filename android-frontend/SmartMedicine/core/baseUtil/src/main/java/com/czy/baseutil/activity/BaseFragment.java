package com.czy.baseutil.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;


public abstract class BaseFragment<VB extends ViewBinding> extends Fragment {

    protected VB binding;
    protected String TAG;

    public abstract VB getBinding();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    // 通常情况下，只要fragment执行了onCreateView方法，Fragment就是isAdded Activity
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.binding = getBinding();
        this.setListener();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 此处binding才生效
        initView();
    }

    // 创建此方法是为了规范data绑定到binding需要在binding创建之后; onCreateView是开始创建view;onViewCreated是view创建完成
    protected void initView(){}

    protected String fragmentName;

    public BaseFragment(Class<?> classType){
        this.fragmentName = classType.getSimpleName();
        TAG = this.fragmentName;
    }


    // 设置监听逻辑
    protected void setListener() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
