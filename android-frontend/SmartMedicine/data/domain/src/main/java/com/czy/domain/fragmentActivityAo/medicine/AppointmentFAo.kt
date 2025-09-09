package com.czy.domain.fragmentActivityAo.medicine

import androidx.lifecycle.MutableLiveData
import java.time.LocalDateTime

class AppointmentFAo {

    var currentItem = MutableLiveData(0)

    lateinit var imageList: List<Any>

    /// location
    // 省
    var province = MutableLiveData("")
    // 市
    var city = MutableLiveData("")
    // 区
    var area = MutableLiveData("")

    // date
    var date: MutableLiveData<LocalDateTime> = MutableLiveData(LocalDateTime.now())

    // department
    var department = MutableLiveData("")
    var registerDepartmentCode = MutableLiveData(1)
    var registerSubjectCode = MutableLiveData(1)
}