package com.czy.smartmedicine.viewModel.activity.order

import androidx.lifecycle.ViewModel
import com.czy.appcore.network.netty.api.send.SocketMessageSender
import com.czy.dao.networkRepository.ApiRequestImpl

class OrderPurchaseAVm(
    private val apiRequestImpl: ApiRequestImpl,
    private val socketMessageSender: SocketMessageSender
): ViewModel() {

    companion object {
        val TAG: String = OrderPurchaseAVm::class.java.name
    }

}