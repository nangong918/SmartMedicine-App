package com.czy.smartmedicine.activity.order

import android.annotation.SuppressLint
import android.os.Bundle
import com.czy.appcore.network.api.handle.SyncRequestCallback
import com.czy.baseutil.network.networkLoad.NetworkLoadUtils
import com.czy.domain.constant.OrderStatusCalculator
import com.czy.domain.constant.UserOrderStatusEnum
import com.czy.domain.constant.medicine.AppointmentMerchantStatusEnum
import com.czy.domain.constant.purchase.OrderStatusEnum
import com.czy.domain.fragmentActivityAo.medicine.order.OrderAppointmentAAo
import com.czy.smartmedicine.databinding.ActivityOrderAppointmentBinding
import com.czy.smartmedicine.utils.BaseVmActivity
import com.czy.smartmedicine.viewModel.activity.order.OrderAppointmentAVm
import java.time.LocalDateTime

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

        initVmAAo()

        observeDate()

        initRequest()
    }

    private fun initVmAAo() {
        vm.aao = OrderAppointmentAAo()
    }

    @SuppressLint("SetTextI18n")
    private fun observeDate() {
        vm.aao.isDateChangeLd.observe(this){
            if (it) {
                binding.tvPrice.text = vm.aao.detailsVo.listVo?.cost?:"-"
                binding.tvOrderTime.text = vm.aao.detailsVo.listVo?.approveDate?:"-"

                val userOrderStatus : UserOrderStatusEnum = UserOrderStatusEnum.getByCode(
                    vm.aao.detailsVo.listVo?.customerStatus?:0
                )
                val merchantOrderStatus : AppointmentMerchantStatusEnum = AppointmentMerchantStatusEnum.getByCode(
                    vm.aao.detailsVo.listVo?.merchantStatus?:0
                )
                val orderStatus : OrderStatusEnum = OrderStatusCalculator.calculateOrderStatus(
                    merchantOrderStatus, userOrderStatus
                )
                binding.tvOrderStatus.text = orderStatus.name

                val duration : String = (vm.aao.detailsVo.listVo?.beginDate?:"-") + " - " +
                        (vm.aao.detailsVo.listVo?.endDate?:"")
                binding.tvDuration.text = duration

                binding.tvDoctorName.text = vm.aao.detailsVo.listVo?.doctorVo?.doctorName?:"-"
                binding.tvHospitalName.text = vm.aao.detailsVo.listVo?.hospitalAo?.hospitalVo?.name?:"-"
                binding.tvRemainingPayTime.text = "todo"
            }
        }
    }

    private fun initRequest() {
        NetworkLoadUtils.showDialogSafety(this)
        vm.doGetAppointmentRecordDetails(this, object : SyncRequestCallback {
            override fun onThrowable(throwable: Throwable?) {
                NetworkLoadUtils.dismissDialogSafety(this@OrderAppointmentActivity)
            }

            override fun onAllRequestSuccess() {
                NetworkLoadUtils.dismissDialogSafety(this@OrderAppointmentActivity)
            }
        })
    }
}