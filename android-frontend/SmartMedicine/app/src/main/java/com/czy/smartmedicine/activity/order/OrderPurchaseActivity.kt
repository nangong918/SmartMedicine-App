package com.czy.smartmedicine.activity.order

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.czy.smartmedicine.R
import com.czy.smartmedicine.databinding.ActivityOrderListBinding
import com.czy.smartmedicine.databinding.ActivityOrderPurchaseBinding
import com.czy.smartmedicine.utils.BaseVmActivity
import com.czy.smartmedicine.viewModel.order.OrderListVm
import com.czy.smartmedicine.viewModel.order.OrderPurchaseVm

class OrderPurchaseActivity : BaseVmActivity<ActivityOrderPurchaseBinding, OrderPurchaseVm>(
    OrderPurchaseActivity::class,
    OrderPurchaseVm::class
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