package com.czy.appview.view.medicine;

import androidx.annotation.NonNull;

public enum OrderViewPagerEnum {

    // appointment
    APPOINTMENT(0),
    // purchase
    PURCHASE(1),
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
    public static OrderViewPagerEnum getEnumByIndex(int index) {
        for (OrderViewPagerEnum value : OrderViewPagerEnum.values()) {
            if (value.index == index) {
                return value;
            }
        }
        return APPOINTMENT;
    }

    public static int getCount() {
        return OrderViewPagerEnum.values().length;
    }

}
