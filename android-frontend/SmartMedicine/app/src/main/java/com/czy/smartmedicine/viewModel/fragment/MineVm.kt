package com.czy.smartmedicine.viewModel.fragment

import androidx.lifecycle.ViewModel
import com.czy.appcore.network.netty.api.send.SocketMessageSender
import com.czy.dao.networkRepository.ApiRequestImpl
import com.czy.domain.fragmentActivityAo.mine.MineFAo


open class MineVm(
    private val apiRequestImpl: ApiRequestImpl,
    private val socketMessageSender: SocketMessageSender
) : ViewModel(){

    companion object {
        val TAG: String = MineVm::class.java.name
    }

    //---------------------------FAo Ld---------------------------

    open lateinit var fao : MineFAo

    open fun init(fao: MineFAo){
        this.fao = fao
    }

    //---------------------------NetWork---------------------------



    //---------------------------Logic---------------------------

}