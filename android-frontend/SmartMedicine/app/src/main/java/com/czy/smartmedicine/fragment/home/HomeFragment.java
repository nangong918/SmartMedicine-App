package com.czy.smartmedicine.fragment.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.czy.smartmedicine.activity.MainActivity;
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

    @SuppressLint("ClickableViewAccessibility")
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

        // ai拖拽
        binding.btnAi.setX(dX);
        binding.btnAi.setY(dY);

        binding.btnAi.setOnTouchListener((view, motionEvent) -> {
            Log.i(getTAG(), "onTouchListener::motionEvent.getAction(): " + motionEvent.getAction());
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN -> {
                    dX = view.getX() - motionEvent.getRawX();
                    dY = view.getY() - motionEvent.getRawY();
                    isDragging = false; // 重置拖动状态
                    actionDownStartTime = System.currentTimeMillis();
                }

                case MotionEvent.ACTION_MOVE -> {
                    // 设置为正在拖动
                    isDragging = true;

                    float newX = motionEvent.getRawX() + dX;
                    float newY = motionEvent.getRawY() + dY;

                    // 获取父视图的边界
                    ViewGroup parent = (ViewGroup) view.getParent();
                    int leftBoundary = 0;
                    int rightBoundary = parent.getWidth() - view.getWidth();
                    int topBoundary = 0;
                    int bottomBoundary = parent.getHeight() - view.getHeight();

                    // 边界检查
                    if (newX < leftBoundary) {
                        newX = leftBoundary;
                    } else if (newX > rightBoundary) {
                        newX = rightBoundary;
                    }

                    if (newY < topBoundary) {
                        newY = topBoundary;
                    } else if (newY > bottomBoundary) {
                        newY = bottomBoundary;
                    }

                    view.animate()
                            .x(newX)
                            .y(newY)
                            .setDuration(0)
                            .start();
                }

                case MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    boolean isClick =
                            // 拖动结束，判断是否是点击
                            !isDragging ||
                            // 拖拽时间小于200
                            System.currentTimeMillis() - actionDownStartTime < 150;
                    if (isClick) {
                        view.performClick(); // 触发点击事件
                    }
                }

                default -> {
                    return false;
                }
            }
            return true;
        });

        binding.btnAi.setOnClickListener(v -> {
            ((MainActivity)requireActivity()).turnToAiFragment();
        });
    }

    // ai data: 需要整理到fao
    // 记录是否拖动
    boolean isDragging = false;
    long actionDownStartTime = 0L;
    private float dX = 50, dY = 50;


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