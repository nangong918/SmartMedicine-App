package com.czy.api.constant.exception;


import lombok.Getter;


/**
 * @author 13225
 * @date 2025/1/3 11:59
 */
@Getter
public enum AppCode {

    HELLO_WORLD("A00001","appCode hello world"),
    HELLO_JAVA("A00002","appCode hello java"),

    NET_SUCCESS("NET_000001","appCode net success"),
    NET_NO_FOUND("NET_000002","appCode net error");

    private final String code;
    private final String message;

    AppCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
