package com.czy.smartmedicine.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.czy.baseutil.activity.BaseFragment;
import com.czy.smartmedicine.activity.TestActivity;
import com.czy.smartmedicine.databinding.FragmentAiBinding;

/**
 * @author 13225
 */
public class AiFragment extends BaseFragment<FragmentAiBinding> {


    public AiFragment() {
        super(AiFragment.class);
    }

    @Override
    public FragmentAiBinding getBinding() {
        return FragmentAiBinding.inflate(getLayoutInflater());
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

        binding.btnTest.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), TestActivity.class);
            startActivity(intent);
        });
    }
}