package com.czy.domain.fragmentActivityAo.medicine.order

import androidx.lifecycle.MutableLiveData
import com.czy.domain.ao.medicine.AppointmentDoctorOrderListAo
import com.czy.domain.constant.medicine.AppointmentSortTypeEnum

class OrderListAAo {
    // 当前的page
    val currentPageLd = MutableLiveData(0)

    // fragment的当前排序方式
    val fragmentCurrentSortTypeLds = listOf(
        MutableLiveData(AppointmentSortTypeEnum.TIME.code),
        MutableLiveData(AppointmentSortTypeEnum.TIME.code)
    )

    // fragment 当前的 currentOrders
    val fragmentCurrentAppointmentOrders = mutableListOf<AppointmentDoctorOrderListAo>()
//    val fragmentCurrentPurchaseOrders = mutableListOf<PurchaseOrderListAo>()

    // list count
    val currentAllCount = listOf(
        MutableLiveData(0),
        MutableLiveData(0)
    )
}