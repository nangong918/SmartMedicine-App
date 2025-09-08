package com.czy.appview.view.order;

public enum OrderViewPagerEnum {
    // 预约订单
    APPOINTMENT_ORDER(0),

    // 购物订单
    PURCHASE_ORDER(1)
    ;

    public final int value;
    OrderViewPagerEnum(int value) {
        this.value = value;
    }

    // code -> o
    public static OrderViewPagerEnum getByValue(int value) {
        for (OrderViewPagerEnum orderViewPagerEnum : values())
        {
            if (orderViewPagerEnum.value == value)
            {
                return orderViewPagerEnum;
            }
        }
        return APPOINTMENT_ORDER;
    }
}
