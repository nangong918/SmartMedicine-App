package com.czy.smartmedicine.viewModel.fragment.medicine.children

import androidx.lifecycle.ViewModel
import com.czy.appcore.network.netty.api.send.SocketMessageSender
import com.czy.dao.networkRepository.ApiRequestImpl


open class MedicineWikiVm(
    private val apiRequestImpl: ApiRequestImpl,
    private val socketMessageSender: SocketMessageSender
) : ViewModel(){

    companion object {
        val TAG: String = MedicineWikiVm::class.java.name
    }

    //---------------------------FAo Ld---------------------------



    //---------------------------NetWork---------------------------



    //---------------------------Logic---------------------------

}