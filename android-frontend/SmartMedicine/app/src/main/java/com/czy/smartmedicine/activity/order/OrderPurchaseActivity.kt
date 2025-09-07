package com.czy.smartmedicine.activity.order

import android.os.Bundle
import com.czy.smartmedicine.databinding.ActivityOrderPurchaseBinding
import com.czy.smartmedicine.utils.BaseVmActivity
import com.czy.smartmedicine.viewModel.activity.order.OrderPurchaseAVm

class OrderPurchaseActivity : BaseVmActivity<ActivityOrderPurchaseBinding, OrderPurchaseAVm>(
    OrderPurchaseActivity::class,
    OrderPurchaseAVm::class
) {
    override fun initBinding(): ActivityOrderPurchaseBinding {
        return ActivityOrderPurchaseBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun setListener() {
        super.setListener()
    }

    override fun initViewModel() {
        super.initViewModel()
    }
}