package com.czy.domain.fragmentActivityAo.medicine.order

import androidx.lifecycle.MutableLiveData
import com.czy.domain.constant.purchase.PayResultEnum
import com.czy.domain.vo.entity.medicine.AppointmentDoctorOrderDetailsVo

class OrderAppointmentAAo {

    // view
    lateinit var detailsVo : AppointmentDoctorOrderDetailsVo
    val isDateChangeLd = MutableLiveData(false)

    // data
    var merchantId : Long? = null
    var orderId : Long? = null
    var payResult : Int = PayResultEnum.NULL.code
}