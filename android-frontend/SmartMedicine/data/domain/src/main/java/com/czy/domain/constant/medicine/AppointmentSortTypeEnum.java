package com.czy.domain.constant.medicine;


/**
 * @author 13225
 * @date 2025/8/19 16:04
 */
public enum AppointmentSortTypeEnum {
    DEFAULT(0, "默认"),
    TIME(1, "时间"),
    DISTANCE(2, "距离"),
    COST(3, "价格"),
    ;

    private final int code;
    private final String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    AppointmentSortTypeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static AppointmentSortTypeEnum getByCode(int code) {
        for (AppointmentSortTypeEnum value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return DEFAULT;
    }
}
