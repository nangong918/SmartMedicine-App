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
import com.czy.dal.vo.fragmentActivity.HomeVo;
import com.czy.smartmedicine.MainApplication;
import com.czy.smartmedicine.activity.PublishPostActivity;
import com.czy.smartmedicine.databinding.FragmentHomeBinding;
import com.czy.smartmedicine.viewModel.base.ApiViewModelFactory;
import com.czy.smartmedicine.viewModel.fragment.HomeViewModel;


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
        viewModel.initPostClickManager(this);
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

//        // 初始化RecyclerView
//        viewModel.initRecyclerView(
//                binding.rclvRecommend,
//                requireActivity()
//        );
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
            viewModel.getRecommendPosts(requireContext(), new SyncRequestCallback() {
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

    private HomeViewModel viewModel;

    private void initViewModel(){
        // 创建viewModel
        ApiViewModelFactory apiViewModelFactory = new ApiViewModelFactory(MainApplication.getApiRequestImplInstance(), MainApplication.getInstance().getMessageSender());
        viewModel = ViewModelUtil.newViewModel(this, apiViewModelFactory, HomeViewModel.class);

        // 初始化viewModel
        viewModel.init(new HomeVo());
    }



    //-----------------------intent-----------------------

}