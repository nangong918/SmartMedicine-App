package com.czy.smartmedicine.viewModel.fragment.message

import androidx.lifecycle.ViewModel
import com.czy.appcore.network.netty.api.send.SocketMessageSender
import com.czy.dao.networkRepository.ApiRequestImpl
import com.czy.domain.fragmentActivityAo.message.AddressBookFAo

open class AddressBookVm(
    private val apiRequestImpl: ApiRequestImpl,
    private val socketMessageSender: SocketMessageSender
) : ViewModel(){

    companion object {
        val TAG: String = AddressBookVm::class.java.name
    }

    //---------------------------FAo Ld---------------------------

    open var fao: AddressBookFAo = AddressBookFAo()

    open fun init(fao: AddressBookFAo) {
        this.fao = fao
    }

    //---------------------------NetWork---------------------------



    //---------------------------Logic---------------------------
}