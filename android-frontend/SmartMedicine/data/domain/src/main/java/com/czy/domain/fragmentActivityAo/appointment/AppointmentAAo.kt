package com.czy.domain.fragmentActivityAo.appointment

import androidx.lifecycle.MutableLiveData
import com.czy.domain.ao.medicine.RegisterAppointmentDoctorCardAo

class AppointmentAAo {
    // 日期vo
    // 预约时间：yyyy-MM-dd
    val dateStrLd = MutableLiveData("")
    // 剩余可预约数量
    val leftAppointmentCount = MutableLiveData(0)
    // 最低费用
    val minPrice = MutableLiveData("-")

    // 医生卡片voList
    var doctorVoList: List<RegisterAppointmentDoctorCardAo> = listOf()
    val doctorVoSizeLd: MutableLiveData<Int> = MutableLiveData(0)
}