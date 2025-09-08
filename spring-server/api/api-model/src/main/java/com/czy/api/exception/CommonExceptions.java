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
    // 系统异常-SQL异常
    SYSTEM_SQL_ERROR("C_10002.1", "系统异常-SQL异常"),
    // 系统异常-IO异常
    SYSTEM_IO_ERROR("C_10002.2", "系统异常-IO异常"),
    // 系统异常-Redis异常
    SYSTEM_REDIS_ERROR("C_10002.3", "系统异常-Redis异常"),
    // 频繁点击
    FREQUENTLY_CLICK("C_10003", "频繁点击，请稍后再试"),
    // 排序方式未找到
    SORT_TYPE_NOT_FOUND("C_10004", "排序方式未找到"),
    // 系统内参数错误
    SYSTEM_PARAM_ERROR("C_10005", "系统内参数错误"),
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
