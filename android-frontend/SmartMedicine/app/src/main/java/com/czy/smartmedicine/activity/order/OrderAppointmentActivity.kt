package com.czy.smartmedicine.activity.order

import android.os.Bundle
import com.czy.smartmedicine.databinding.ActivityOrderAppointmentBinding
import com.czy.smartmedicine.utils.BaseVmActivity
import com.czy.smartmedicine.viewModel.activity.order.OrderAppointmentAVm

class OrderAppointmentActivity : BaseVmActivity<ActivityOrderAppointmentBinding, OrderAppointmentAVm>(
    OrderAppointmentActivity::class,
    OrderAppointmentAVm::class
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

    override fun initView() {
        super.initView()


    }
}