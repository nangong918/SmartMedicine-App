package com.czy.domain.fragmentActivityAo.medicine.order

import androidx.lifecycle.MutableLiveData
import com.czy.domain.ao.medicine.AppointmentDoctorOrderListAo
import com.czy.domain.constant.medicine.AppointmentSortTypeEnum


class OrderAppointmentFAo {

    // 当前选择的排序方式:
    var currentSortType: MutableLiveData<Int> =
        MutableLiveData(AppointmentSortTypeEnum.TIME.code)

    // list
    var currentOrders: MutableList<AppointmentDoctorOrderListAo>? = null
    val unprocessedOrders: MutableList<AppointmentDoctorOrderListAo>? = null

    // list count
    var currentAllCount: MutableLiveData<Int> = MutableLiveData(0)
}