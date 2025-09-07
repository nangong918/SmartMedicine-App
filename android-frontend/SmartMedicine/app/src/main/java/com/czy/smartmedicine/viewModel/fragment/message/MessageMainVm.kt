package com.czy.smartmedicine.viewModel.fragment.message

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.czy.appcore.network.netty.api.send.SocketMessageSender
import com.czy.dao.networkRepository.ApiRequestImpl
import com.czy.domain.fragmentActivityAo.message.MessageMainFAo
import com.czy.smartmedicine.fragment.message.children.MessageViewPagerAdapter

open class MessageMainVm(
    private val apiRequestImpl: ApiRequestImpl,
    private val socketMessageSender: SocketMessageSender
) : ViewModel(){

    companion object {
        val TAG: String = MessageMainVm::class.java.name
    }

    //---------------------------FAo Ld---------------------------

    open var fao : MessageMainFAo = MessageMainFAo()
    open lateinit var messageViewPagerAdapter: MessageViewPagerAdapter

    open fun init(fao: MessageMainFAo, fragment: Fragment){
        this.fao = fao
        messageViewPagerAdapter = MessageViewPagerAdapter(
            fragment.childFragmentManager,
            fragment.lifecycle
        )
    }

    //---------------------------NetWork---------------------------



    //---------------------------Logic---------------------------
}