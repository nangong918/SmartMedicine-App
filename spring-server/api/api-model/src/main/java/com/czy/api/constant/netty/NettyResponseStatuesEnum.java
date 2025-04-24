package com.czy.api.constant.netty;

/**
 * @author 13225
 * @date 2025/4/24 10:50
 */
public enum NettyResponseStatuesEnum {
    // 等待
    // 成功
    // 失败

    // code, message
    WAITING("waiting", "等待"),
    SUCCESS("success", "成功"),
    FAILURE("failure", "失败"),
    ;

    private final String code;
    private final String message;

    NettyResponseStatuesEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
