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
import com.czy.domain.dto.http.response.GetRegisterAppointmentListResponse
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

    fun doGetRegisterAppointmentList(context: Context, callback: SyncRequestCallback, registerTime: Int?) {
        val request = GetRegisterAppointmentListRequest()
        request.requestAo = AppointmentDoctorSelectAo()

        request.requestAo.registerDepartmentCode = aao.registerDepartmentCode
        request.requestAo.registerSubjectCode = aao.registerSubjectCode
        request.requestAo.registerLocation = LocationAo()
        request.requestAo.registerLocation.province = aao.province
        request.requestAo.registerLocation.city = aao.city
        request.requestAo.registerLocation.region = aao.area
        request.requestAo.latitude = aao.latitude
        request.requestAo.longitude = aao.longitude

        // 处理 registerTime: null/0 -> 当前时间; 1/2/3/4 -> 1/2/3/4天后的起始时间
        val daysToAdd = registerTime?.coerceIn(0, 4) ?: 0
        val targetDate = LocalDateTime.now().plusDays(daysToAdd.toLong()).toLocalDate().atStartOfDay()

        request.requestAo.registerTime = DateUtils.yyyyMMddHHmmssToString(targetDate)

        // 发送请求
        apiRequestImpl.getRegisterAppointmentList(
            request,
            { response ->
                ResponseTool.handleSyncResponseEx(
                    response,
                    context,
                    callback,
                    this::handleGetRegisterAppointmentList
                )
            },
            { error ->
                callback.onThrowable(error)
            }
        )
    }

    private fun handleGetRegisterAppointmentList
                (response: BaseResponse<GetRegisterAppointmentListResponse>, context: Context, callback: SyncRequestCallback) {
        callback.onAllRequestSuccess()
    }

    fun doGetRegisterAppointmentAllDate(context: Context, callback: SyncRequestCallback){
        val request = GetRegisterAppointmentListRequest()
        request.requestAo = AppointmentDoctorSelectAo()

        request.requestAo.registerDepartmentCode = aao.registerDepartmentCode
        request.requestAo.registerSubjectCode = aao.registerSubjectCode
        request.requestAo.registerLocation = LocationAo()
        request.requestAo.registerLocation.province = aao.province
        request.requestAo.registerLocation.city = aao.city
        request.requestAo.registerLocation.region = aao.area
        request.requestAo.latitude = aao.latitude
        request.requestAo.longitude = aao.longitude

        request.requestAo.registerTime = DateUtils.yyyyMMddHHmmssToString(LocalDateTime.now())

        apiRequestImpl.getRegisterAppointmentAllDate(
            request,
            {
                response ->
                ResponseTool.handleSyncResponseEx(
                    response,
                    context,
                    callback,
                    ::handleGetRegisterAppointmentAllDate
                )
            },
            {
                error -> callback.onThrowable(error)
            }
        )
    }

    private fun handleGetRegisterAppointmentAllDate
                (response: BaseResponse<GetAllRegisterAppointmentDateResponse>, context: Context, callback: SyncRequestCallback){
        callback.onAllRequestSuccess()
    }

    //---------------------------Logic---------------------------



}