package com.czy.smartmedicine.fragment.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.czy.appview.view.home.HomeViewPagerEnum;
import com.czy.domain.fragmentActivityAo.HomeFAo;
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

        // 设置顶部导航栏的点击监听器
        binding.homeTopBar.setOnViewPagerBarClickListener(position -> {
            binding.vPager2.setCurrentItem(position, true);
        });
    }

    //---------------------------viewModel---------------------------

    @Override
    protected void initViewModel() {
        super.initViewModel();
        // 初始化viewModel
        HomeFAo homeFAo = new HomeFAo();
        homeFAo.currentPosition.setValue(HomeViewPagerEnum.RECOMMEND.getIndex());
        vm.init(homeFAo, this);

        // 设置 ViewPager2 的适配器
        binding.vPager2.setAdapter(vm.viewPagerAdapter);
    }


    //-----------------------intent-----------------------

}