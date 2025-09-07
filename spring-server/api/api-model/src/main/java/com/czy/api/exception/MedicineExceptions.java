package com.czy.api.exception;

import exception.ExceptionEnums;
import lombok.Getter;

/**
 * @author 13225
 * @date 2025/6/26 17:22
 */
@Getter
public enum MedicineExceptions implements ExceptionEnums {

    // 预约的医生商户不存在
    DOCTOR_MERCHANT_NOT_EXIST("MD_10001", "预约的医生商户不存在"),
    // 商户信息已过期
    MERCHANT_INFO_EXPIRED("MD_10002", "商户信息已过期"),
    // 已全部预约完毕，无可预约的商户
    NO_AVAILABLE_MERCHANT("MD_10003", "已全部预约完毕，无可预约的商户"),
    // 等待开放，在开放预约之后在进行预约
    WAITING_OPEN("MD_10004", "等待开放，在开放预约之后在进行预约"),
    // 用户经纬度位置不能为空
    LOCATION_NOT_NULL("MD_10005", "用户经纬度位置不能为空"),
    // 预约单已存在
    APPOINTMENT_DOCTOR_ORDER_EXIST("MD_10006", "预约单已存在"),
    ;

    private final String code;
    private final String message;

    MedicineExceptions(String code, String message) {
        this.code = code;
        this.message = message;
    }

    // code -> o
    public static MedicineExceptions getByCode(String code) {
        for (MedicineExceptions value : MedicineExceptions.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
