package com.czy.smartmedicine.activity

import android.os.Bundle
import android.util.Log
import com.czy.domain.vo.entity.medicine.AppointmentDoctorPageVo
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
        val extras = intent.extras
        if (extras != null) {
            try {
                val appointmentDoctorPageVo = extras.get(AppointmentActivity::class.java.name)
                        as? AppointmentDoctorPageVo
                if (appointmentDoctorPageVo != null) {

                    vm.aao.dateStrLd.value = appointmentDoctorPageVo.dataVo.date
                    vm.aao.leftAppointmentCount.value = appointmentDoctorPageVo.dataVo.remainCount
                    vm.aao.minPrice.value = appointmentDoctorPageVo.dataVo.minCost

                    vm.aao.doctorVoList = appointmentDoctorPageVo.cardAos
                    vm.aao.doctorVoSizeLd.value = vm.aao.doctorVoList.size
                }
                else {
                    Log.e(TAG, "获取 AppointmentDoctorPageVo 失败: 转换失败")
                }
            } catch (e: ClassCastException) {
                Log.e(TAG, "获取 AppointmentDoctorPageVo 失败: 类型转换异常", e)
            }
        }

    }

    private fun observeData() {
    }

}