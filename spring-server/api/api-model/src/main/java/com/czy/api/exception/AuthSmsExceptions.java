package com.czy.api.exception;

import lombok.Getter;

/**
 * @author 13225
 * @date 2025/6/26 17:58
 */
@Getter
public enum AuthSmsExceptions implements ExceptionEnums{

    // 发送短信失败
    SEND_SMS_FAIL("A_10001", "发送短信失败"),
    // 验证码错误
    VCODE_ERROR("A_10002", "验证码错误"),


    ;

    private final String code;
    private final String message;

    AuthSmsExceptions(String code, String message) {
        this.code = code;
        this.message = message;
    }

    // code -> o
    public static AuthSmsExceptions getByCode(String code) {
        for (AuthSmsExceptions value : AuthSmsExceptions.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
