package com.czy.smartmedicine.viewModel.fragment.medicine

import androidx.lifecycle.ViewModel
import com.czy.appcore.network.netty.api.send.SocketMessageSender
import com.czy.dao.networkRepository.ApiRequestImpl
import com.czy.smartmedicine.viewModel.fragment.home.RecommendVm

class MedicineVm(
    private val apiRequestImpl: ApiRequestImpl,
    private val socketMessageSender: SocketMessageSender
) : ViewModel(){

    companion object {
        val TAG: String = MedicineVm::class.java.name
    }

}