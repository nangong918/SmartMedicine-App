package com.czy.smartmedicine.viewModel.fragment.order

import androidx.lifecycle.ViewModel
import com.czy.appcore.network.netty.api.send.SocketMessageSender
import com.czy.appview.view.medicine.order.AppointmentDoctorOrderAdapter
import com.czy.appview.view.medicine.order.OnAppointmentOrderClick
import com.czy.dao.networkRepository.ApiRequestImpl
import com.czy.domain.fragmentActivityAo.medicine.order.OrderAppointmentFAo

class OrderAppointmentFVm(
    private val apiRequestImpl: ApiRequestImpl,
    private val socketMessageSender: SocketMessageSender
): ViewModel() {

    companion object {
        val TAG: String = OrderAppointmentFVm::class.java.name
    }

    //---------------------------FAo Ld---------------------------

    lateinit var adapter: AppointmentDoctorOrderAdapter
    lateinit var fao: OrderAppointmentFAo

    //---------------------------NetWork---------------------------



    //---------------------------Logic---------------------------



}