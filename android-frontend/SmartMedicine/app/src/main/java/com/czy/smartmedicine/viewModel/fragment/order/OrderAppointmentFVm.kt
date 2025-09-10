package com.czy.smartmedicine.viewModel.fragment.order

import androidx.lifecycle.ViewModel
import com.czy.appcore.network.netty.api.send.SocketMessageSender
import com.czy.dao.networkRepository.ApiRequestImpl

class OrderAppointmentFVm(
    private val apiRequestImpl: ApiRequestImpl,
    private val socketMessageSender: SocketMessageSender
): ViewModel() {

    companion object {
        val TAG: String = OrderAppointmentFVm::class.java.name
    }

    //---------------------------FAo Ld---------------------------



    //---------------------------NetWork---------------------------



    //---------------------------Logic---------------------------



}