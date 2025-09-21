package com.czy.smartmedicine.fragment.home.children

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.czy.appcore.network.api.handle.SyncRequestCallback
import com.czy.baseutil.network.networkLoad.NetworkLoadUtils
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
    }

    //---------------------------ViewModel---------------------------

    override fun initViewModel() {
        super.initViewModel()

        initVmFAo()

        observeData()

        // 初始化点击管理器 (RecyclerView中用到了, 必须在其之前)
        vm.initPostClickManager(this)

        // 初始化RecyclerView
        vm.initRecyclerView(binding.rclvRecommend, requireActivity())

        // 初始化网络请求
        vm.initialNetworkRequest(requireContext(), object : SyncRequestCallback {
            override fun onThrowable(throwable: Throwable) {
                Log.e(TAG, "onThrowable: $throwable")
                binding.progressBar.visibility = View.GONE
                binding.lyMain.isRefreshing = false
                NetworkLoadUtils.dismissDialog()
            }

            override fun onAllRequestSuccess() {
                binding.progressBar.visibility = View.GONE
                binding.lyMain.isRefreshing = false
                NetworkLoadUtils.dismissDialog()
            }
        })
    }

    private fun initVmFAo(){

    }

    private fun observeData(){
        vm.fao.recommendPostCount.observe(viewLifecycleOwner){
            count ->
            if (count == null){
                binding.progressBar.visibility = View.VISIBLE
                return@observe
            }
            binding.progressBar.visibility = if (count > 0) View.GONE else View.VISIBLE
        }
    }

}