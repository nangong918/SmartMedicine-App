package com.czy.smartmedicine.activity

import android.os.Bundle
import com.czy.smartmedicine.databinding.ActivityAppointmentBinding
import com.czy.smartmedicine.utils.BaseVmActivity
import com.czy.smartmedicine.viewModel.activity.AppointmentAVm

class AppointmentActivity : BaseVmActivity<ActivityAppointmentBinding, AppointmentAVm>(
    AppointmentActivity::class,
    AppointmentAVm::class
) {
    override fun initBinding(): ActivityAppointmentBinding {
        return ActivityAppointmentBinding.inflate(layoutInflater)
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