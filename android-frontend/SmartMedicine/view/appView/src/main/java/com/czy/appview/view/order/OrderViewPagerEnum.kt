package com.czy.appview.view.order

enum class OrderViewPagerEnum(val index: Int) {

    // 预约订单
    APPOINTMENT_ORDER(0),

    // 购物订单
    PURCHASE_ORDER(1);

    // 根据 index 获取枚举值
    companion object {
        fun getEnumByIndex(index: Int): OrderViewPagerEnum {
            return values().find { it.index == index } ?: APPOINTMENT_ORDER
        }

        // 获取枚举总数
        fun getCount(): Int {
            return values().size
        }
    }
}