package com.czy.smartmedicine.activity


import android.os.Bundle
import com.czy.smartmedicine.databinding.ActivityOrderBinding
import com.czy.smartmedicine.utils.BaseVmActivity
import com.czy.smartmedicine.viewModel.activity.OrderVm

class OrderActivity : BaseVmActivity<ActivityOrderBinding, OrderVm>(
    OrderActivity::class,
    OrderVm::class
) {
    override fun initBinding(): ActivityOrderBinding {
        return ActivityOrderBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    //---------------------------Listener---------------------------

    override fun setListener() {
        super.setListener()
    }

    //---------------------------VM---------------------------

    override fun initViewModel() {
        super.initViewModel()

        initViewModelAAo()

        observeData()
    }

    private fun initViewModelAAo() {
    }

    private fun observeData() {
    }

}