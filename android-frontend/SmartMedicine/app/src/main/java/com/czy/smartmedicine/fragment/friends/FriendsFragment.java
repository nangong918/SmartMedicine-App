package com.czy.smartmedicine.fragment.friends;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.czy.baseUtilsLib.activity.BaseFragment;
import com.czy.baseUtilsLib.viewModel.ViewModelUtil;
import com.czy.customviewlib.view.viewPager.ViewPagerConstant;
import com.czy.dal.ao.NewUserGroupActivityStartAo;
import com.czy.dal.constant.SearchEnum;
import com.czy.dal.constant.SelectItemEnum;
import com.czy.dal.constant.newUserGroup.UserGroupEnum;
import com.czy.dal.vo.view.mainTop.MainTopBarVo;
import com.czy.dal.vo.viewModelVo.friends.FriendsVo;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.activity.MainActivity;
import com.czy.smartmedicine.activity.NewUserGroupActivity;
import com.czy.smartmedicine.activity.SearchActivity;
import com.czy.smartmedicine.databinding.FragmentFriendsBinding;
import com.czy.smartmedicine.viewModel.ApiViewModelFactory;
import com.czy.smartmedicine.viewModel.FriendsViewModel;

import java.util.Optional;

/**
 * @author 13225
 * 1.朋友圈
 * 2.新的朋友
 * 3.群聊（最后制作）
 */
public class FriendsFragment extends BaseFragment<FragmentFriendsBinding> {


    public FriendsFragment() {
        super(FriendsFragment.class);
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

        // 获取屏幕高度
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels; // 获取屏幕高度

        // 在视图完全布局后获取高度
        binding.getRoot().post(() -> {
            // 获取 FriendsFragment 的高度
            int fragmentHeight = binding.getRoot().getHeight();

            // 获取各个视图的高度
            int friendsCircleHeight = binding.lyFriendsCircle.getHeight();
            int newFriendHeight = binding.lyNewFriend.getHeight();
            int vpbHeight = binding.vpb.getHeight();

            // 计算 ViewPager2 的高度
            int viewPagerHeight = screenHeight - (friendsCircleHeight + newFriendHeight + vpbHeight);

            // 设置 ViewPager2 的高度
            ViewGroup.LayoutParams params = binding.viewPager2.getLayoutParams();
            params.height = Math.max(viewPagerHeight, 0); // 确保高度不为负值
            binding.viewPager2.setLayoutParams(params);
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void init() {
        super.init();
        initView();
        initViewModel();
    }

    //-----------------------ViewModel-----------------------

    private FriendsViewModel viewModel;

    private void initViewModel() {
        ApiViewModelFactory apiViewModelFactory = new ApiViewModelFactory(MainApplication.getApiRequestImplInstance(), MainApplication.getInstance().getMessageSender());
        viewModel = ViewModelUtil.newViewModel(this, apiViewModelFactory, FriendsViewModel.class);

        initViewModelVo();

        observeData();
    }

    private void initViewModelVo() {
        FriendsVo friendsVo = new FriendsVo();

        viewModel.init(friendsVo);
    }

    private void observeData() {
        Optional.ofNullable(viewModel)
                .map(vm -> vm.friendsVo)
                .map(vo -> vo.newFriends)
                .ifPresent(intLd -> {
                    intLd.observe(this, integer -> {
                        binding.vMessagePromptNewFriends.setMessageNum(integer);
                    });
                });
    }

    private void initView(){
//        initRecyclerView();
        MainTopBarVo mainTopBarVo = new MainTopBarVo();
        mainTopBarVo.selectItemEnum = SelectItemEnum.FRIENDS;
        mainTopBarVo.onFriendCallback = () -> {
            Intent intent = new Intent(requireActivity(), SearchActivity.class);
            intent.putExtra(SearchEnum.INTENT_EXTRA_NAME, SearchEnum.USER);
            searchUserLauncher.launch(intent);
        };
        ((MainActivity)requireActivity()).setMainTopBar(mainTopBarVo);

        binding.vpb.setText(new String[]{getString(com.czy.customviewlib.R.string.friends), getString(com.czy.customviewlib.R.string.groups)});

        initViewPager2();
    }

    @Override
    protected void setListener() {
        super.setListener();
        setActivityLauncher();

        binding.vpb.setOnViewPagerBarClickListener(position -> {
            binding.viewPager2.setCurrentItem(position, true);
        });

        binding.lyNewFriend.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), NewUserGroupActivity.class);
            NewUserGroupActivityStartAo newUserGroupActivityStartAo = new NewUserGroupActivityStartAo();
            newUserGroupActivityStartAo.userGroupEnum = UserGroupEnum.USER;
            intent.putExtra(NewUserGroupActivityStartAo.class.getName(), newUserGroupActivityStartAo);
            newFriendLauncher.launch(intent);
        });
    }

    private ActivityResultLauncher<Intent> searchUserLauncher;
    private ActivityResultLauncher<Intent> newFriendLauncher;

    private void setActivityLauncher(){
        searchUserLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            // 返回之后刷新
        });

        newFriendLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            // 处理返回刷新
        });
    }

    //-----------------------ViewPager2-----------------------

    // 设置ViewPager2的Adapter
    public void initViewPager2() {
        ViewPagerUserGroupAdapter adapter = new ViewPagerUserGroupAdapter(this, getFragmentCount());
        // 设置适配器
        binding.viewPager2.setAdapter(adapter);
        // 注册适配器
        binding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            // 滚动
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            // Child Fragment切换调用
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // 点击Bar之后调用切换
                onBarPageSelected(position);
                Log.i("CHECK", "onPageSelected 1::position  " + position);
                if(adapter.fragmentList.get(position) != null){
                    Log.i("CHECK", "onPageSelected 2::position  " + position);
                    // 创建Child实例：切换到目标Child调用
                    adapter.fragmentList.get(position).changeToDo(position);
                    // 创建Child实例：切换到其他Fragment调用
                    adapter.fragmentList.get(position).setTurnToOtherFragmentListener(selectedItem ->
                            binding.viewPager2.setCurrentItem(selectedItem, true)
                    );
                }
            }

            // 滑动状态
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }

    // 获取Fragment的数量
    public int getFragmentCount() {
        return ViewPagerConstant.getViewPagerCount();
    }

    // 顶部导航栏点击
    private void onBarPageSelected(int position) {
        binding.vpb.setCurrentPosition(position);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (viewModel != null){
            viewModel.onDestroy();
        }
    }
}