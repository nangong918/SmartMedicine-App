package com.czy.smartmedicine.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.czy.baseUtilLib.activity.BaseFragment;
import com.czy.domain.constant.SelectItemEnum;
import com.czy.domain.vo.view.mainTop.MainTopBarVo;
import com.czy.smartmedicine.activity.MainActivity;
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

        ((MainActivity)requireActivity()).setMainTopBar(new MainTopBarVo(SelectItemEnum.NOTIFICATIONS));

    }

    @Override
    protected void setListener() {
        super.setListener();
    }
}