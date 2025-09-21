package com.czy.smartmedicine.viewModel.activity.order

import android.content.Context
import androidx.lifecycle.ViewModel
import com.czy.appcore.network.api.handle.SyncRequestCallback
import com.czy.appcore.network.netty.api.send.SocketMessageSender
import com.czy.appview.view.medicine.order.OrderViewPagerEnum
import com.czy.baseutil.network.BaseResponse
import com.czy.dao.networkRepository.ApiRequestImpl
import com.czy.domain.dto.http.request.GetUserAppointmentRecordRequest
import com.czy.domain.dto.http.response.GetUserAppointmentRecordResponse
import com.czy.domain.fragmentActivityAo.medicine.order.OrderListAAo
import com.czy.smartmedicine.MainApplication
import com.czy.smartmedicine.fragment.order.OrderViewPagerAdapter
import com.czy.smartmedicine.utils.ResponseTool

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

    fun doGetUserAppointmentRecord(context: Context, callback: SyncRequestCallback,
                                   userLongitude: Double?, userLatitude: Double?){
        val request = GetUserAppointmentRecordRequest()

        request.userId = MainApplication.getInstance().userLoginInfoAo?.userId
        request.sortType = aao.fragmentCurrentSortTypeLds[OrderViewPagerEnum.APPOINTMENT_ORDER.index].value
        request.userLongitude = userLongitude
        request.userLatitude = userLatitude

        apiRequestImpl.getUserAppointmentRecord(
            request,
            {
                    response -> ResponseTool.handleSyncResponseEx(
                response,
                context,
                callback,
                ::handleGetUserAppointmentRecord
            )
            },
            {
                    error -> callback.onThrowable(error)
            }
        )
    }

    private fun handleGetUserAppointmentRecord
                (response: BaseResponse<GetUserAppointmentRecordResponse>, context: Context, callback: SyncRequestCallback) {
        response.data?.let {
            aao.fragmentCurrentAppointmentOrders.clear()
            aao.fragmentCurrentAppointmentOrders.addAll(it.currentOrders)
            aao.currentAllCount[OrderViewPagerEnum.APPOINTMENT_ORDER.index].value = it.currentOrders?.size?:0
        }

        callback.onAllRequestSuccess()
    }

    //---------------------------Logic---------------------------



}