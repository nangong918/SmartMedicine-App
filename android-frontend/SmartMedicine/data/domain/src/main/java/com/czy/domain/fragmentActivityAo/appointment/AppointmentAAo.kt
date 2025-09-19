package com.czy.domain.fragmentActivityAo.appointment

import androidx.lifecycle.MutableLiveData
import com.czy.domain.ao.medicine.RegisterAppointmentDoctorCardAo
import com.czy.domain.vo.entity.medicine.AppointmentDoctorDataVo
import java.time.LocalDateTime

class AppointmentAAo {
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

    // selectData
    var currentSelectDate: LocalDateTime = LocalDateTime.now().toLocalDate().atStartOfDay()

    // 顶部的预约日志
    val isAppointmentDateChanged = MutableLiveData(false)
    // 当前选择4个中的哪个?: 0~3
    var currentSelectDatePosition = MutableLiveData(0L)
    // 顶部记录数据 (Kotlin的list是不可变长度的)
    val dateList: List<AppointmentDoctorDataVo> =
        List(4) { AppointmentDoctorDataVo() }

    // 医生卡片voList
    val doctorVoList: MutableList<RegisterAppointmentDoctorCardAo> = mutableListOf()
    val doctorVoSizeLd: MutableLiveData<Int> = MutableLiveData(0)

    val locationLd: MutableLiveData<String> = MutableLiveData("")
    val departmentLd: MutableLiveData<String> = MutableLiveData("")
}