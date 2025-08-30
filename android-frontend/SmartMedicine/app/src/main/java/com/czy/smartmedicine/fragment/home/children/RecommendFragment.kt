package com.czy.smartmedicine.fragment.home.children

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.czy.appcore.network.api.handle.SyncRequestCallback
import com.czy.smartmedicine.databinding.FragmentRecommendBinding
import com.czy.smartmedicine.utils.BaseVmFragment
import com.czy.smartmedicine.viewModel.fragment.home.RecommendVm


class RecommendFragment : BaseVmFragment<FragmentRecommendBinding, RecommendVm>(
    RecommendFragment::class,
    RecommendVm::class
) {
    override fun initBinding(): FragmentRecommendBinding {
        return FragmentRecommendBinding.inflate(layoutInflater)
    }

    // init
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    // initView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 初始化RecyclerView
        vm.initRecyclerView(binding.rclvRecommend, requireActivity())

        // 初始化点击管理器
        vm.initPostClickManager(this)

        // 初始化网络请求
        vm.initialNetworkRequest(requireContext(), object : SyncRequestCallback {
            override fun onThrowable(throwable: Throwable) {
                // 处理异常
            }

            override fun onAllRequestSuccess() {
                binding.lyMain.isRefreshing = false
            }
        })
    }

    //---------------------------ViewModel---------------------------

    override fun initViewModel() {
        super.initViewModel()
    }

}