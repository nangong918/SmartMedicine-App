package com.czy.smartmedicine.viewModel.fragment.medicine.children

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import com.czy.appcore.network.api.handle.SyncRequestCallback
import com.czy.appcore.network.netty.api.send.SocketMessageSender
import com.czy.appview.view.ImageSliderAdapter
import com.czy.baseutil.date.DateUtils
import com.czy.baseutil.network.BaseResponse
import com.czy.dao.networkRepository.ApiRequestImpl
import com.czy.domain.ao.LocationAo
import com.czy.domain.ao.medicine.AppointmentDoctorSelectAo
import com.czy.domain.dto.http.request.GetRegisterAppointmentListRequest
import com.czy.domain.dto.http.response.GetRegisterAppointmentListResponse
import com.czy.domain.fragmentActivityAo.medicine.AppointmentFAo
import com.czy.domain.vo.entity.medicine.AppointmentDoctorPageVo
import com.czy.smartmedicine.utils.ResponseTool
import java.util.Optional


open class AppointmentFVm(
    private val apiRequestImpl: ApiRequestImpl,
    private val socketMessageSender: SocketMessageSender
) : ViewModel(){

    companion object {
        val TAG: String = AppointmentFVm::class.java.name
    }

    //---------------------------FAo Ld---------------------------

    open lateinit var imageSliderAdapter: ImageSliderAdapter

    open var aao = AppointmentFAo()

    fun initAdapter(){
        aao.imageList = listOf(
            com.czy.appview.R.drawable.round_corners_bg_commend,
            com.czy.appview.R.drawable.round_corners_bg_commend,
            com.czy.appview.R.drawable.round_corners_bg_commend
        )
        imageSliderAdapter = ImageSliderAdapter(aao.imageList)

        startAutoScroll()
    }

    //---------------------------NetWork---------------------------

    open fun doGetRegisterAppointmentList(context: Context, callback : SyncRequestCallback){
        val appointmentDoctorSelectAo = AppointmentDoctorSelectAo()
        val locationAo = LocationAo()
        locationAo.province = aao.province.value
        locationAo.city = aao.city.value
        locationAo.region = aao.area.value
        appointmentDoctorSelectAo.registerLocation = locationAo
        appointmentDoctorSelectAo.registerTime = DateUtils.yyyyMMddHHmmssToString(
            aao.date.value!!
        )
        appointmentDoctorSelectAo.registerSubjectCode = aao.registerSubjectCode.value
        appointmentDoctorSelectAo.registerDepartmentCode = aao.registerDepartmentCode.value

        appointmentDoctorSelectAo.latitude = null
        appointmentDoctorSelectAo.longitude = null

        val request = GetRegisterAppointmentListRequest()
        request.requestAo = appointmentDoctorSelectAo

        apiRequestImpl.getRegisterAppointmentList(
            request,
            {
                response -> ResponseTool.handleSyncResponseEx(
                    response,
                    context,
                    callback,
                    this::handleGetRegisterAppointmentList
                )
            },
            {
                error -> callback.onThrowable(error)
            }
        )
    }

    open lateinit var appointmentDoctorPageVo : AppointmentDoctorPageVo

    private fun handleGetRegisterAppointmentList
                (response: BaseResponse<GetRegisterAppointmentListResponse>,
                 context: Context,
                 callback: SyncRequestCallback){
        response.data?.pageVo?.let {
            appointmentDoctorPageVo = it
            callback.onAllRequestSuccess()
        }
        callback.onThrowable(Throwable("暂无数据"))
    }

    //---------------------------Logic---------------------------

    private val handler = Handler(Looper.getMainLooper())
    private fun startAutoScroll() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                aao.currentItem.value = (aao.currentItem.value!! + 1) % aao.imageList.size
                handler.postDelayed(this, 3000) // 3 秒间隔
            }
        }, 3000)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(null) // 清除所有回调
    }
}