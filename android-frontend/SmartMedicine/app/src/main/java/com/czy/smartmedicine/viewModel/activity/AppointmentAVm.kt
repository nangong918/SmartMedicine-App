package com.czy.smartmedicine.viewModel.activity

import androidx.lifecycle.ViewModel
import com.czy.appcore.network.netty.api.send.SocketMessageSender
import com.czy.dao.networkRepository.ApiRequestImpl
import com.czy.domain.fragmentActivityAo.appointment.AppointmentAAo


open class AppointmentAVm(
    private val apiRequestImpl: ApiRequestImpl,
    private val socketMessageSender: SocketMessageSender
) : ViewModel(){

    companion object {
        val TAG: String = AppointmentAVm::class.java.name
    }

    //---------------------------FAo Ld---------------------------

    open var aao = AppointmentAAo()

    //---------------------------NetWork---------------------------



    //---------------------------Logic---------------------------



}