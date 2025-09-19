package com.czy.smartmedicine.fragment.order

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.czy.appview.view.medicine.order.AppointmentDoctorOrderAdapter
import com.czy.appview.view.medicine.order.OnAppointmentOrderClick
import com.czy.domain.ao.medicine.AppointmentDoctorOrderListAo
import com.czy.domain.constant.OrderStatusCalculator
import com.czy.domain.constant.UserOrderStatusEnum
import com.czy.domain.constant.medicine.AppointmentMerchantStatusEnum
import com.czy.domain.constant.medicine.AppointmentSortTypeEnum
import com.czy.domain.constant.purchase.OrderStatusEnum
import com.czy.domain.fragmentActivityAo.medicine.order.OrderAppointmentFAo
import com.czy.smartmedicine.activity.order.OrderAppointmentActivity
import com.czy.smartmedicine.databinding.FragmentOrderAppointmentBinding
import com.czy.smartmedicine.utils.BaseVmFragment
import com.czy.smartmedicine.viewModel.fragment.order.OrderAppointmentFVm


open class OrderAppointmentFragment : BaseVmFragment<FragmentOrderAppointmentBinding, OrderAppointmentFVm>(
    OrderAppointmentFragment::class,
    OrderAppointmentFVm::class
) {
    override fun initBinding(): FragmentOrderAppointmentBinding {
        return FragmentOrderAppointmentBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun setListener() {
        super.setListener()
    }

    override fun initViewModel() {
        super.initViewModel()

        initVmFAo()

        observeData()

        initRequest()
    }

    private fun initVmFAo() {
        vm.fao = OrderAppointmentFAo()

        vm.adapter = AppointmentDoctorOrderAdapter(
            vm.fao.currentOrders?: mutableListOf(),
            object : OnAppointmentOrderClick {
                override fun onBaseCardClick(position: Int, merchantId: Long?, orderId: Long?) {
                    vm.fao.currentOrders?.get(position)?.let {
                        val merchantStatusEnum = AppointmentMerchantStatusEnum.getByCode(it.listVo?.merchantStatus?:0)
                        val userOrderStatusEnum = UserOrderStatusEnum.getByCode(it.listVo?.customerStatus?:0)
                        val orderStatus: OrderStatusEnum = OrderStatusCalculator.calculateOrderStatus(
                            merchantStatusEnum,
                            userOrderStatusEnum
                        )

                        // 待支付
                        if (orderStatus == OrderStatusEnum.WAIT_PAY){
                            val intent = Intent(activity, OrderAppointmentActivity::class.java)
                            intent.putExtra("merchantId", merchantId)
                            intent.putExtra("orderId", orderId)
                            startActivity(intent)
                        }
                        else{
                            Log.i(TAG, "onButton1Click: 订单状态: $orderStatus")
                        }
                    }
                }

                override fun onButton1Click(position: Int, merchantId: Long?, orderId: Long?) {

                }

                override fun onButton2Click(position: Int, merchantId: Long?, orderId: Long?) {

                }
            }
        )

        binding.rclv.adapter = vm.adapter
    }

    open fun setSortType(sortType: Int) {
        vm.fao.currentSortType.value = sortType
    }

    open fun setCurrentList(list: MutableList<AppointmentDoctorOrderListAo>) {
        vm.fao.currentOrders = list
        vm.fao.currentAllCount.value = list.size
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeData() {
        vm.fao.currentAllCount.observe(viewLifecycleOwner){
            count ->
            vm.adapter.notifyDataSetChanged()
        }
    }

    private fun initRequest() {
    }
}