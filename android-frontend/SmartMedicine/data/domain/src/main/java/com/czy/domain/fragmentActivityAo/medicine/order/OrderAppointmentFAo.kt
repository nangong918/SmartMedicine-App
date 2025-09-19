package com.czy.domain.fragmentActivityAo.medicine.order

import androidx.lifecycle.MutableLiveData
import com.czy.domain.ao.medicine.AppointmentDoctorOrderListAo
import com.czy.domain.constant.medicine.AppointmentSortTypeEnum


class OrderAppointmentFAo {

    // 当前选择的排序方式:
    var currentSortType: MutableLiveData<Int> =
        MutableLiveData(AppointmentSortTypeEnum.TIME.code)

    // list (不是搜索的结果, 而是OrderListActivity交给的参数; 因为有搜索功能, 是在activity执行, 将结果交给Fragment)
    var currentOrders: MutableList<AppointmentDoctorOrderListAo> = mutableListOf()
    // 未处理的订单
    @Deprecated("未处理的订单 暂时不使用; 开发那么多功能干什么")
    val unprocessedOrders: MutableList<AppointmentDoctorOrderListAo> = mutableListOf()

    // list count
    var currentAllCount: MutableLiveData<Int> = MutableLiveData(0)
}