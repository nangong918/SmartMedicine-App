package com.czy.smartmedicine.activity.order

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.czy.smartmedicine.R
import com.czy.smartmedicine.databinding.ActivityOrderAppointmentBinding
import com.czy.smartmedicine.databinding.ActivityOrderListBinding
import com.czy.smartmedicine.utils.BaseVmActivity
import com.czy.smartmedicine.viewModel.order.OrderAppointmentVm
import com.czy.smartmedicine.viewModel.order.OrderListVm

class OrderAppointmentActivity : BaseVmActivity<ActivityOrderAppointmentBinding, OrderAppointmentVm>(
    OrderAppointmentActivity::class,
    OrderAppointmentVm::class
) {
    override fun initBinding(): ActivityOrderAppointmentBinding {
        return ActivityOrderAppointmentBinding.inflate(layoutInflater)
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