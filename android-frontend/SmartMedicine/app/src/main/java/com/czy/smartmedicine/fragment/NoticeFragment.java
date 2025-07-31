package com.czy.smartmedicine.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.czy.baseUtilsLib.activity.BaseFragment;
import com.czy.smartmedicine.databinding.FragmentNoticeBinding;

/**
 * @author 13225
 */
public class NoticeFragment extends BaseFragment<FragmentNoticeBinding> {


    public NoticeFragment() {
        super(NoticeFragment.class);
    }

    @Override
    public FragmentNoticeBinding getBinding() {
        return FragmentNoticeBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void setListener() {
        super.setListener();
    }
}