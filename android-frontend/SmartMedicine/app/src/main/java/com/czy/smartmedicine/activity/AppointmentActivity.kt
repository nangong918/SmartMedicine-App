package com.czy.smartmedicine.activity

import android.os.Bundle
import android.util.Log
import com.czy.appcore.network.api.handle.SyncRequestCallback
import com.czy.baseutil.network.networkLoad.NetworkLoadUtils
import com.czy.domain.vo.entity.medicine.AppointmentDoctorPageVo
import com.czy.smartmedicine.databinding.ActivityAppointmentBinding
import com.czy.smartmedicine.utils.BaseVmActivity
import com.czy.smartmedicine.viewModel.activity.AppointmentAVm
import java.time.LocalDateTime
import java.util.Optional

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

        initRequest();
    }

    private fun initViewModelAAo() {
        val extras = intent.extras
        if (extras != null) {
            // location
            vm.aao.province = extras.getString("province", "")
            vm.aao.city = extras.getString("city", "")
            vm.aao.area = extras.getString("area", "")

            // register
            vm.aao.department = extras.getString("department", "")
            vm.aao.registerDepartmentCode = extras.getInt("registerDepartmentCode")
            vm.aao.registerSubjectCode = extras.getInt("registerSubjectCode")

            // date
            vm.aao.currentSelectDatePosition.value = extras.getLong("selectDate")
            vm.aao.currentSelectDate = Optional.ofNullable(vm.aao.currentSelectDatePosition.value)
                .filter { it > 0L }
                .map { LocalDateTime.now().plusDays(it).toLocalDate().atStartOfDay() }
                .orElse(LocalDateTime.now().toLocalDate().atStartOfDay())
        }

    }

    private fun observeData() {
        // 顶部的预约日志的数据
        vm.aao.isAppointmentDateChanged.observe(this){
            if (it){
                // 顶部的预约日志的数据变化了
            }
        }

        // 顶部的预约日志的数据的选择
        vm.aao.currentSelectDatePosition.observe(this){
            // 4个 0 ~ 3
        }

        // list的count
        vm.aao.doctorVoSizeLd.observe(this){
            size -> {

            }
        }
    }

    private fun initRequest() {
        NetworkLoadUtils.showDialogSafety(this@AppointmentActivity)
        vm.doGetRegisterAppointmentAllDate(
            this@AppointmentActivity,
            object : SyncRequestCallback{
                override fun onThrowable(throwable: Throwable?) {
                    Log.e(TAG, "获取 AllView 失败: ", throwable)
                    NetworkLoadUtils.dismissDialogSafety(this@AppointmentActivity)
                }

                override fun onAllRequestSuccess() {
                    NetworkLoadUtils.dismissDialogSafety(this@AppointmentActivity)
                }
            }
        )
        vm.doGetRegisterAppointmentList(
            this@AppointmentActivity,
            object : SyncRequestCallback {
                override fun onThrowable(throwable: Throwable?) {
                    Log.e(TAG, "获取 ViewList 失败: ", throwable)
                    NetworkLoadUtils.dismissDialogSafety(this@AppointmentActivity)
                }

                override fun onAllRequestSuccess() {
                    NetworkLoadUtils.dismissDialogSafety(this@AppointmentActivity)
                }
            },

        )
    }

}