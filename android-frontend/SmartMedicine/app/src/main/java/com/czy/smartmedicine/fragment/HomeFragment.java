package com.czy.smartmedicine.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.czy.baseUtilsLib.activity.BaseFragment;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void init() {
        super.init();

        initViewModel();

        viewModel.initRecyclerView(
                binding.rclvRecommend,
                requireActivity()
        );
    }

    @Override
    protected void setListener() {
        super.setListener();

        binding.fbtnPublishPost.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), PublishPostActivity.class);
            startActivity(intent);
        });
    }

    //---------------------------viewModel---------------------------

    private HomeViewModel viewModel;

    private void initViewModel(){
        // 创建viewModel
        ApiViewModelFactory apiViewModelFactory = new ApiViewModelFactory(MainApplication.getApiRequestImplInstance(), MainApplication.getInstance().getMessageSender());
        viewModel = ViewModelUtil.newViewModel(this, apiViewModelFactory, HomeViewModel.class);

        // 初始化viewModel
        viewModel.init(new HomeVo(), requireActivity());
    }



    //-----------------------intent-----------------------

}