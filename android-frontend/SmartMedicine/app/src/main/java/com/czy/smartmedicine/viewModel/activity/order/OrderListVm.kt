package com.czy.smartmedicine.viewModel.activity.order

import androidx.lifecycle.ViewModel
import com.czy.appcore.network.netty.api.send.SocketMessageSender
import com.czy.dao.networkRepository.ApiRequestImpl
import com.czy.domain.fragmentActivityAo.medicine.order.OrderListAAo
import com.czy.smartmedicine.fragment.order.OrderViewPagerAdapter

class OrderListVm(
    private val apiRequestImpl: ApiRequestImpl,
    private val socketMessageSender: SocketMessageSender
): ViewModel() {

    companion object {
        val TAG: String = OrderListVm::class.java.name
    }

    //---------------------------AAo Ld---------------------------

    lateinit var aao : OrderListAAo

    lateinit var viewPagerAdapter : OrderViewPagerAdapter

    //---------------------------NetWork---------------------------



    //---------------------------Logic---------------------------



}