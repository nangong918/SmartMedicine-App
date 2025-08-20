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
