package com.czy.smartmedicine.viewModel.activity

import androidx.lifecycle.ViewModel
import com.czy.appcore.network.netty.api.send.SocketMessageSender
import com.czy.dao.networkRepository.ApiRequestImpl
import com.czy.domain.fragmentActivityAo.OrderAAo

open class OrderVm(
    private val apiRequestImpl: ApiRequestImpl,
    private val socketMessageSender: SocketMessageSender
) : ViewModel() {

    companion object {
        val TAG: String = OrderVm::class.java.name
    }

    //---------------------------AAo Ld---------------------------

    open lateinit var aao: OrderAAo

    open fun init(aao: OrderAAo) {
        this.aao = aao
    }

    //---------------------------NetWork---------------------------



    //---------------------------Logic---------------------------

}