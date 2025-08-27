package com.czy.api.exception;

import exception.ExceptionEnums;
import lombok.Getter;

/**
 * @author 13225
 * @date 2025/6/26 17:22
 */
@Getter
public enum PurchaseExceptions implements ExceptionEnums {

    // 重复申请，请耐心等待
    REPEAT_APPLY_LOCK("PUR_10001", "重复申请，请耐心等待"),
    // 同商户已存在订单, 暂不可申请
    EXIST_ORDER_LOCK("PUR_10002", "同商户已存在订单, 暂不可申请"),
    // 重复支付, 请勿重复支付
    REPEAT_PAY_LOCK("PUR_10003", "重复支付, 请勿重复支付"),
    // 系统忙碌, 支付请求超时, 请稍后再试
    SYSTEM_BUSY_LOCK("PUR_10004", "系统忙碌, 支付请求超时, 请稍后再试"),
    ;

    private final String code;
    private final String message;

    PurchaseExceptions(String code, String message) {
        this.code = code;
        this.message = message;
    }

    // code -> o
    public static PurchaseExceptions getByCode(String code) {
        for (PurchaseExceptions value : PurchaseExceptions.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
