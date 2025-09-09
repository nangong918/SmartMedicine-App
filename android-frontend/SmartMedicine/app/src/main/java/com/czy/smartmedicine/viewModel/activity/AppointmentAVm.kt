package com.czy.smartmedicine.viewModel.activity

import android.content.Context
import androidx.lifecycle.ViewModel
import com.czy.appcore.network.api.handle.SyncRequestCallback
import com.czy.appcore.network.netty.api.send.SocketMessageSender
import com.czy.baseutil.date.DateUtils
import com.czy.baseutil.network.BaseResponse
import com.czy.dao.networkRepository.ApiRequestImpl
import com.czy.domain.ao.LocationAo
import com.czy.domain.ao.medicine.AppointmentDoctorSelectAo
import com.czy.domain.dto.http.request.GetRegisterAppointmentListRequest
import com.czy.domain.dto.http.response.GetAllRegisterAppointmentDateResponse
import com.czy.domain.fragmentActivityAo.appointment.AppointmentAAo
import com.czy.smartmedicine.utils.ResponseTool
import java.time.LocalDateTime


open class AppointmentAVm(
    private val apiRequestImpl: ApiRequestImpl,
    private val socketMessageSender: SocketMessageSender
) : ViewModel(){

    companion object {
        val TAG: String = AppointmentAVm::class.java.name
    }

    //---------------------------FAo Ld---------------------------

    open var aao = AppointmentAAo()

    //---------------------------NetWork---------------------------

    fun doGetRegisterAppointmentAllDate(context: Context, callback : SyncRequestCallback, registerTime: LocalDateTime?){
        val request = GetRegisterAppointmentListRequest()
        request.requestAo = AppointmentDoctorSelectAo()
        request.requestAo.registerDepartmentCode = aao.registerDepartmentCode
        request.requestAo.registerSubjectCode = aao.registerSubjectCode
        request.requestAo.registerLocation = LocationAo()
        request.requestAo.registerLocation.province = aao.province
        request.requestAo.registerLocation.city = aao.city
        request.requestAo.registerLocation.province = aao.area
        request.requestAo.registerTime = if (registerTime != null) {
            // 使用传入的时间
            DateUtils.yyyyMMddHHmmssToString(registerTime)
        } else {
            // 使用当前时间
            DateUtils.yyyyMMddHHmmssToString(LocalDateTime.now())
        }
        request.requestAo.latitude = aao.latitude
        request.requestAo.longitude = aao.longitude
        apiRequestImpl.getRegisterAppointmentAllDate(
            request,
            {
                response -> ResponseTool.handleSyncResponseEx(
                    response,
                    context,
                    callback,
                    this::handleGetRegisterAppointmentAllDateResponse
                )
            },
            {
                error -> callback.onThrowable(error)
            }
        )
    }

    private fun handleGetRegisterAppointmentAllDateResponse
                (response: BaseResponse<GetAllRegisterAppointmentDateResponse>, context: Context, callback: SyncRequestCallback) {
        callback.onAllRequestSuccess()
    }

    //---------------------------Logic---------------------------



}