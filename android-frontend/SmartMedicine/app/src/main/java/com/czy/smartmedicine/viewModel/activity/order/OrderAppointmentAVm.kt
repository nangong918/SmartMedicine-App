package com.czy.smartmedicine.viewModel.activity.order

import android.content.Context
import androidx.lifecycle.ViewModel
import com.czy.appcore.network.api.handle.SyncRequestCallback
import com.czy.appcore.network.netty.api.send.SocketMessageSender
import com.czy.baseutil.network.BaseResponse
import com.czy.dao.networkRepository.ApiRequestImpl
import com.czy.domain.ao.medicine.AppointmentDoctorOrderDetailsAo
import com.czy.domain.fragmentActivityAo.medicine.order.OrderAppointmentAAo
import com.czy.smartmedicine.MainApplication
import com.czy.smartmedicine.utils.ResponseTool

class OrderAppointmentAVm(
    private val apiRequestImpl: ApiRequestImpl,
    private val socketMessageSender: SocketMessageSender
): ViewModel() {

    companion object {
        val TAG: String = OrderAppointmentAVm::class.java.name
    }

    //---------------------------AAo Ld---------------------------

    lateinit var aao: OrderAppointmentAAo

    //---------------------------NetWork---------------------------

    fun doGetAppointmentRecordDetails(context: Context, callback: SyncRequestCallback){
        val userId = MainApplication.getInstance().userLoginInfoAo?.userId
        if (userId == null){
            callback.onThrowable(Throwable("userId == null"))
            return
        }
        if (aao.orderId == null){
            callback.onThrowable(Throwable("orderId == null"))
            return
        }
        apiRequestImpl.getAppointmentRecordDetails(
            userId,
            aao.orderId,
            {
                response ->
                ResponseTool.handleSyncResponseEx(
                    response,
                    context,
                    callback,
                    ::handleGetAppointmentRecordDetails
                )
            },
            {
                error -> callback.onThrowable(error)
            }
        )
    }

    private fun handleGetAppointmentRecordDetails(response: BaseResponse<AppointmentDoctorOrderDetailsAo>, context: Context, callback: SyncRequestCallback) {
        response.data?.let {
            aao.orderId = it.orderId
            aao.merchantId = it.doctorMerchantId
            aao.detailsVo = it.detailsVo

            aao.isDateChangeLd.value = true

            callback.onAllRequestSuccess()
            return
        }
        callback.onThrowable(Throwable("获取预约详情失败"))
    }

    //---------------------------Logic---------------------------

}