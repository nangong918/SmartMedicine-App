package com.czy.smartmedicine.viewModel.fragment.order

import android.content.Context
import androidx.lifecycle.ViewModel
import com.czy.appcore.network.api.handle.SyncRequestCallback
import com.czy.appcore.network.netty.api.send.SocketMessageSender
import com.czy.baseutil.network.BaseResponse
import com.czy.dao.networkRepository.ApiRequestImpl
import com.czy.domain.dto.http.request.GetUserAppointmentRecordRequest
import com.czy.domain.dto.http.response.GetUserAppointmentRecordResponse
import com.czy.domain.fragmentActivityAo.medicine.order.OrderAppointmentFAo
import com.czy.smartmedicine.MainApplication
import com.czy.smartmedicine.utils.ResponseTool

class OrderPurchaseFVm(
    private val apiRequestImpl: ApiRequestImpl,
    private val socketMessageSender: SocketMessageSender
): ViewModel() {

    companion object {
        val TAG: String = OrderPurchaseFVm::class.java.name
    }

    //---------------------------FAo Ld---------------------------

    lateinit var fao : OrderAppointmentFAo

    //---------------------------NetWork---------------------------

    fun doGetUserAppointmentRecord(context: Context, callback: SyncRequestCallback,
                                   sortType : Int?, userLongitude: Double?, userLatitude: Double?){
        val request = GetUserAppointmentRecordRequest()

        request.userId = MainApplication.getInstance().userLoginInfoAo?.userId
        request.sortType = sortType
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
                // todo
                callback.onAllRequestSuccess()
    }

    //---------------------------Logic---------------------------


}


