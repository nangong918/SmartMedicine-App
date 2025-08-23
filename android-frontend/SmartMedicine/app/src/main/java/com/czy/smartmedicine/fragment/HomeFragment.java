package com.czy.smartmedicine.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.czy.appcore.network.api.handle.SyncRequestCallback;
import com.czy.baseUtilsLib.activity.BaseFragment;
import com.czy.baseUtilsLib.network.networkLoad.NetworkLoadUtils;
import com.czy.baseUtilsLib.viewModel.ViewModelUtil;
import com.czy.domain.constant.SelectItemEnum;
import com.czy.domain.fragmentActivityAo.HomeVo;
import com.czy.domain.vo.view.mainTop.MainTopBarVo;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.activity.MainActivity;
import com.czy.smartmedicine.activity.PublishPostActivity;
import com.czy.smartmedicine.databinding.FragmentHomeBinding;
import com.czy.smartmedicine.viewModel.base.ApiViewModelFactory;
import com.czy.smartmedicine.viewModel.fragment.HomeVm;


/**
 * @author 13225
 */
public class HomeFragment extends BaseFragment<FragmentHomeBinding> {


    public HomeFragment() {
        super(HomeFragment.class);
    }

    @Override
    public FragmentHomeBinding getBinding() {
        return FragmentHomeBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 初始化viewModel
        initViewModel();

        // 初始化点击管理器
        vm.initPostClickManager(this);
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

        ((MainActivity)requireActivity()).setMainTopBar(new MainTopBarVo(SelectItemEnum.HOME));

        // 初始化RecyclerView
        vm.initRecyclerView(binding.rclvRecommend, requireActivity());

        // 初始化网络请求；网络请求之后会触发回调，回调会调用rclAdapter，所以在initRecyclerView之后初始化请求
        vm.initialNetworkRequest(requireContext(), new SyncRequestCallback() {
            @Override
            public void onThrowable(Throwable throwable) {

            }

            @Override
            public void onAllRequestSuccess() {
                binding.lyMain.setRefreshing(false);
            }
        });
    }

    @Override
    protected void setListener() {
        super.setListener();

        binding.fbtnPublishPost.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), PublishPostActivity.class);
            startActivity(intent);
        });

        binding.lyMain.setOnRefreshListener(() -> {
            NetworkLoadUtils.showDialog(requireContext());
            vm.getRecommendPostsP(requireContext(), new SyncRequestCallback() {
                @Override
                public void onThrowable(Throwable throwable) {
                    NetworkLoadUtils.dismissDialog();
                }

                @Override
                public void onAllRequestSuccess() {
                    NetworkLoadUtils.dismissDialog();
                    binding.lyMain.setRefreshing(false);
                }
            });
        });
    }

    //---------------------------viewModel---------------------------

    private HomeVm vm;

    private void initViewModel(){
        // 创建viewModel
        ApiViewModelFactory apiViewModelFactory = new ApiViewModelFactory(MainApplication.getApiRequestImplInstance(), MainApplication.getInstance().getMessageSender());
        vm = ViewModelUtil.newViewModel(this, apiViewModelFactory, HomeVm.class);

        // 初始化viewModel
        vm.init(new HomeVo());
    }



    //-----------------------intent-----------------------

}