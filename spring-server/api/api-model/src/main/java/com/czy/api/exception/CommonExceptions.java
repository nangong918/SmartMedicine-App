package com.czy.api.exception;

import exception.ExceptionEnums;
import lombok.Getter;

/**
 * @author 13225
 * @date 2025/6/26 17:22
 */
@Getter
public enum CommonExceptions implements ExceptionEnums {

    // 参数错误
    PARAM_ERROR("C_10001", "参数错误、不全"),
    // 系统异常 (与前端无关系的熊异常)
    SYSTEM_ERROR("C_10002", "系统异常"),
    // 频繁点击
    FREQUENTLY_CLICK("C_10003", "频繁点击，请稍后再试"),
    ;

    private final String code;
    private final String message;

    CommonExceptions(String code, String message) {
        this.code = code;
        this.message = message;
    }

    // code -> o
    public static CommonExceptions getByCode(String code) {
        for (CommonExceptions value : CommonExceptions.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
