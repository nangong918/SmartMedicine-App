package com.czy.smartmedicine.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import com.czy.appcore.network.api.handle.SyncRequestCallback
import com.czy.appview.VerticalSpacingItemDecoration
import com.czy.appview.view.medicine.appointment.AppointmentMerchantAdapter
import com.czy.baseutil.network.networkLoad.NetworkLoadUtils
import com.czy.baseutil.ui.ToastUtils
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

        binding.appointmentBar.setOnViewPagerBarClickListener{
            position ->
            vm.aao.currentSelectDatePosition.value = position.toLong()
        }

        binding.topBar.setBack {
            finish()
        }
    }

    //---------------------------VM---------------------------

    override fun initViewModel() {
        super.initViewModel()

        initViewModelAAo()

        observeData()

        initRequest()
    }

    private fun initViewModelAAo() {
        val extras = intent.extras
        if (extras != null) {
            // location
            vm.aao.province = extras.getString("province", "")
            vm.aao.city = extras.getString("city", "")
            vm.aao.area = extras.getString("area", "")

            vm.aao.locationLd.value = vm.aao.province + "-" + vm.aao.city + "-" + vm.aao.area

            // register
            vm.aao.department = extras.getString("department", "")
            vm.aao.registerDepartmentCode = extras.getInt("registerDepartmentCode")
            vm.aao.registerSubjectCode = extras.getInt("registerSubjectCode")

            vm.aao.departmentLd.value = vm.aao.department

            // date
            vm.aao.currentSelectDatePosition.value = extras.getLong("selectDate")
            vm.aao.currentSelectDate = Optional.ofNullable(vm.aao.currentSelectDatePosition.value)
                .filter { it > 0L }
                .map { LocalDateTime.now().plusDays(it).toLocalDate().atStartOfDay() }
                .orElse(LocalDateTime.now().toLocalDate().atStartOfDay())
        }

        // view
        binding.appointmentBar.dataVos = vm.aao.dateList

        // adapter
        vm.merchantAdapter = AppointmentMerchantAdapter(
            vm.aao.doctorVoList
        ) { position ->

            NetworkLoadUtils.showDialogSafety(this@AppointmentActivity)
            vm.doAppointmentMerchant(
                this@AppointmentActivity,
                object : SyncRequestCallback {
                    override fun onThrowable(throwable: Throwable?) {
                        NetworkLoadUtils.dismissDialogSafety(this@AppointmentActivity)
                        Log.e(TAG, "onThrowable: ", throwable)
                    }

                    override fun onAllRequestSuccess() {
                        NetworkLoadUtils.dismissDialogSafety(this@AppointmentActivity)
                        ToastUtils.showToastActivity(
                            this@AppointmentActivity,
                            getString(com.czy.appview.R.string.appointment_success)
                        )
                        // 跳转订单页面
                    }

                },
                vm.aao.doctorVoList[position].doctorMerchantAppointmentId
            )
        }
        binding.rclvAppointment.apply {
            adapter = vm.merchantAdapter
            addItemDecoration(VerticalSpacingItemDecoration(5))
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeData() {
        // 顶部的预约日志的数据
        vm.aao.isAppointmentDateChanged.observe(this){
            if (it){
                Log.i(TAG, "isAppointmentDateChanged: date = ${vm.aao.dateList[1].date}")
                // 顶部的预约日志的数据变化了
                binding.appointmentBar.updateUiDate()
            }
        }

        // 顶部的预约日志的数据的选择
        vm.aao.currentSelectDatePosition.observe(this){
            // 4个 0 ~ 3
            binding.appointmentBar.setCurrentPosition(it.toInt())
            NetworkLoadUtils.showDialogSafety(this@AppointmentActivity)
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
                }
            )
        }

        // list的count
        vm.aao.doctorVoSizeLd.observe(this){
            size ->
            // 0 暂无记录
            // adapter更新
            Log.i(TAG, "sizeLd: $size, adapterListSize: ${vm.merchantAdapter.itemCount}")
            vm.merchantAdapter.notifyDataSetChanged()
        }

        // location
        vm.aao.locationLd.observe(this){
            binding.tvLocation.text = it
        }
        // department
        vm.aao.departmentLd.observe(this){
            binding.tvDepartment.text = it
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


    override fun initView() {
        super.initView()

        binding.topBar.setTitle(getString(com.czy.appview.R.string.appointment_title))
    }

}