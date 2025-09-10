package com.czy.smartmedicine.viewModel.activity

import android.content.Context
import androidx.lifecycle.ViewModel
import com.czy.appcore.network.api.handle.SyncRequestCallback
import com.czy.appcore.network.netty.api.send.SocketMessageSender
import com.czy.appview.view.medicine.appointment.AppointmentMerchantAdapter
import com.czy.baseutil.date.DateUtils
import com.czy.baseutil.network.BaseResponse
import com.czy.dao.networkRepository.ApiRequestImpl
import com.czy.domain.ao.LocationAo
import com.czy.domain.ao.medicine.AppointmentDoctorSelectAo
import com.czy.domain.dto.http.request.AppointmentDoctorRequest
import com.czy.domain.dto.http.request.GetRegisterAppointmentListRequest
import com.czy.domain.dto.http.response.AppointmentDoctorResponse
import com.czy.domain.dto.http.response.GetAllRegisterAppointmentDateResponse
import com.czy.domain.dto.http.response.GetRegisterAppointmentListResponse
import com.czy.domain.fragmentActivityAo.appointment.AppointmentAAo
import com.czy.smartmedicine.MainApplication
import com.czy.smartmedicine.utils.ResponseTool
import java.time.LocalDateTime
import java.util.Optional


open class AppointmentAVm(
    private val apiRequestImpl: ApiRequestImpl,
    private val socketMessageSender: SocketMessageSender
) : ViewModel(){

    companion object {
        val TAG: String = AppointmentAVm::class.java.name
    }

    //---------------------------FAo Ld---------------------------

    open var aao = AppointmentAAo()

    open lateinit var merchantAdapter : AppointmentMerchantAdapter

    //---------------------------NetWork---------------------------

    fun doGetRegisterAppointmentList(context: Context, callback: SyncRequestCallback) {
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
        val daysToAdd = aao.currentSelectDatePosition.value?.coerceIn(0, 4) ?: 0
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

    private fun handleGetRegisterAppointmentList(
        response: BaseResponse<GetRegisterAppointmentListResponse>,
        context: Context,
        callback: SyncRequestCallback
    ) {
        response.data?.let { data ->
            data.pageVo?.let { pvo ->

                // 更新 doctorVoList
                aao.doctorVoList.clear()
                aao.doctorVoList.addAll(pvo.cardAos ?: emptyList())

                // 更新 dateList
                val position = aao.currentSelectDatePosition.value
                if (pvo.dataVo != null && position != null) {
                    val finalInt = position.toInt()
                    if (finalInt in aao.dateList.indices) {
                        with(aao.dateList[finalInt]) {
                            date = pvo.dataVo.date
                            minCost = pvo.dataVo.minCost
                            remainCount = pvo.dataVo.remainCount
                        }
                    }
                }

                // liveData通知
                aao.doctorVoSizeLd.value = aao.doctorVoList.size
            }
        }
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
                (response: BaseResponse<GetAllRegisterAppointmentDateResponse>,
                 context: Context, callback: SyncRequestCallback){
        response.data?.let { data ->
            data.dataVos?.let { list ->
                val size = minOf(aao.dateList.size, list.size)
                for (i in 0 until size) {
                    aao.dateList[i].date = list[i].date
                    aao.dateList[i].remainCount = list[i].remainCount
                    aao.dateList[i].minCost = list[i].minCost
                }
                aao.isAppointmentDateChanged.value = true
            }
        }
        callback.onAllRequestSuccess()
    }

    fun doAppointmentMerchant(context: Context, callback: SyncRequestCallback, doctorMerchantAppointmentId: String){
        val request = AppointmentDoctorRequest()
        request.userId = MainApplication.getInstance().userLoginInfoAo?.userId
        request.doctorMerchantAppointmentId = doctorMerchantAppointmentId.toLong()
        apiRequestImpl.appointmentDoctorMerchant(
            request,
            {
                response ->
                ResponseTool.handleSyncResponseEx(
                    response,
                    context,
                    callback,
                    ::handleAppointmentMerchant
                )
            },
            {
                error -> callback.onThrowable(error)
            }
        )
    }

    private fun handleAppointmentMerchant
                (response: BaseResponse<AppointmentDoctorResponse>, context: Context, callback: SyncRequestCallback){
                    // 其他逻辑: doctorMerchantAppointmentId; orderId
                    callback.onAllRequestSuccess()
                }

    //---------------------------Logic---------------------------



}