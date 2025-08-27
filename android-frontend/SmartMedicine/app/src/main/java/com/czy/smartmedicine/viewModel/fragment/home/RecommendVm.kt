package com.czy.smartmedicine.viewModel.fragment.home

import androidx.lifecycle.ViewModel
import com.czy.appcore.network.netty.api.send.SocketMessageSender
import com.czy.dao.networkRepository.ApiRequestImpl

open class RecommendVm(
    private val apiRequestImpl: ApiRequestImpl,
    private val socketMessageSender: SocketMessageSender
) : ViewModel(){

    companion object {
        val TAG: String = RecommendVm::class.java.name
    }

    open fun getSocketMessageSender() : SocketMessageSender {
        return socketMessageSender
    }

    //---------------------------Vo Ld---------------------------



}