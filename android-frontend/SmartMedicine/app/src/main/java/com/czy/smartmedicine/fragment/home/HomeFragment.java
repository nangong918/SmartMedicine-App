package com.czy.smartmedicine.fragment.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.czy.appview.view.home.HomeViewPagerEnum;
import com.czy.baseutil.image.ImageLoadUtil;
import com.czy.domain.ao.chat.UserLoginInfoAo;
import com.czy.domain.fragmentActivityAo.home.HomeFAo;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.activity.PublishPostActivity;
import com.czy.smartmedicine.activity.search.SearchPostActivity;
import com.czy.smartmedicine.databinding.FragmentHomeBinding;
import com.czy.smartmedicine.utils.BaseVmFragment;
import com.czy.smartmedicine.viewModel.fragment.home.HomeVm;

import java.util.Optional;

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
        binding.homeSelectBar.setOnViewPagerBarClickListener(position -> {
            vm.homeFAo.currentPosition.setValue(position);
            binding.vPager2.setCurrentItem(position, true);
        });

        binding.vPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                vm.homeFAo.currentPosition.setValue(position);
            }
        });

        // searchBar
        binding.homeSearchBar.setImageClickListener(v -> {
            MainApplication.onHomeSearchAvatarClicked.run();
        });

        binding.homeSearchBar.setSearchBarClickListener(v -> {
            Intent intent = new Intent(requireActivity(), SearchPostActivity.class);
            startActivity(intent);
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

        UserLoginInfoAo userLoginInfoAo = MainApplication.getInstance().getUserLoginInfoAo();

        String avatarUrl = Optional.ofNullable(userLoginInfoAo)
                .map(ao -> ao.avatarUrl)
                .orElse("");

        // 加载头像
        if (!TextUtils.isEmpty(avatarUrl)){
            ImageLoadUtil.loadImageViewByResource(
                    avatarUrl,
                    binding.homeSearchBar.getImageView()
            );
        }
    }


    //-----------------------intent-----------------------

}