package com.czy.domain.fragmentActivityAo.appointment

import androidx.lifecycle.MutableLiveData
import com.czy.domain.ao.medicine.RegisterAppointmentDoctorCardAo
import java.time.LocalDateTime

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

    /// location
    // 省
    var province = ""
    // 市
    var city = ""
    // 区
    var area = ""

    // 经纬度
    var latitude: Double? = null
    var longitude: Double? = null

    // department
    var department = ""
    var registerDepartmentCode = 1
    var registerSubjectCode = 1
}