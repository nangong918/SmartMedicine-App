package com.czy.api.constant.medicine;

import lombok.Getter;

/**
 * @author 13225
 * @date 2025/8/19 15:07
 * 可预约，已结束，售罄，等待开放
 */
@Getter
public enum AppointmentStatusEnum {
    AVAILABLE(0, "可预约"),
    ENDED(1, "已结束"),
    SOLD_OUT(2, "售罄"),
    WAITING_OPEN(3, "等待开放")
    ;

    private final int code;
    private final String status;
    AppointmentStatusEnum(int code, String status) {
        this.code = code;
        this.status = status;
    }
    public static AppointmentStatusEnum getByCode(int code) {
        for (AppointmentStatusEnum value : AppointmentStatusEnum.values()) {
            if (value.code == code) {
                return value;
            }
        }
        return AVAILABLE;
    }
}
