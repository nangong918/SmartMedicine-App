package com.czy.appview.view.medicine.order;

import androidx.annotation.NonNull;

public enum OrderViewPagerEnum {

    // appointment
    APPOINTMENT_ORDER(0),
    // purchase
    PURCHASE_ORDER(1),
    ;
    private final int index;
    OrderViewPagerEnum(int index) {
        this.index = index;
    }

    // index
    public int getIndex() {
        return index;
    }

    // index -> o
    @NonNull
    public static OrderViewPagerEnum getByValue(int index) {
        for (OrderViewPagerEnum value : OrderViewPagerEnum.values()) {
            if (value.index == index) {
                return value;
            }
        }
        return APPOINTMENT_ORDER;
    }

    public static int getCount() {
        return OrderViewPagerEnum.values().length;
    }

}
