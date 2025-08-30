package com.czy.smartmedicine.fragment.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.czy.domain.fragmentActivityAo.HomeVo;
import com.czy.smartmedicine.activity.PublishPostActivity;
import com.czy.smartmedicine.databinding.FragmentHomeBinding;
import com.czy.smartmedicine.utils.BaseVmFragment;
import com.czy.smartmedicine.viewModel.fragment.home.HomeVm;

import kotlin.jvm.JvmClassMappingKt;


/**
 * @author 13225
 */
public class HomeFragment extends BaseVmFragment<FragmentHomeBinding, HomeVm> {


    public HomeFragment() {
        super(JvmClassMappingKt.getKotlinClass(HomeFragment.class), JvmClassMappingKt.getKotlinClass(HomeVm.class));
    }

    @NonNull
    @Override
    public FragmentHomeBinding initBinding() {
        return FragmentHomeBinding.inflate(getLayoutInflater());

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 初始化viewModel
        initViewModel();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // super中替换为了 return binding.getRoot();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    protected void setListener() {
        super.setListener();

        binding.fbtnPublishPost.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), PublishPostActivity.class);
            startActivity(intent);
        });

        binding.homeTopBar.setOnViewPagerBarClickListener(position -> {

        });
    }

    //---------------------------viewModel---------------------------

    @Override
    protected void initViewModel() {
        super.initViewModel();
        // 初始化viewModel
        vm.init(new HomeVo());
    }


    //-----------------------intent-----------------------

}