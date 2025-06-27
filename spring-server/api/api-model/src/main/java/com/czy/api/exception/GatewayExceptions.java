package com.czy.api.exception;

import lombok.Getter;

/**
 * @author 13225
 * @date 2025/6/26 17:22
 */
@Getter
public enum GatewayExceptions implements ExceptionEnums{

    // accessToken为空
    ACCESS_TOKEN_EMPTY("Gateway_10001", "accessToken为空"),
    // refreshToken为空
    REFRESH_TOKEN_EMPTY("Gateway_10002", "refreshToken为空"),
    // accessToken无效
    ACCESS_TOKEN_INVALID("Gateway_10003", "accessToken无效"),
    // accessToken失效且refreshToken解析失败无法获得JwtPayload
    ACCESS_TOKEN_EXPIRED_AND_REFRESH_TOKEN_INVALID("Gateway_10004", "accessToken失效且refreshToken解析失败无法获得JwtPayload"),
    // accessToken验证出现异常
    ACCESS_TOKEN_VERIFY_ERROR("Gateway_10005", "accessToken验证出现异常"),
    // refreshToken过期，请重新登录
    REFRESH_TOKEN_EXPIRED("Gateway_10006", "refreshToken过期，请重新登录"),
    // refreshToken无效
    REFRESH_TOKEN_INVALID("Gateway_10007", "refreshToken无效"),
    ;

    private final String code;
    private final String message;

    GatewayExceptions(String code, String message) {
        this.code = code;
        this.message = message;
    }

    // code -> o
    public static GatewayExceptions getByCode(String code) {
        for (GatewayExceptions value : GatewayExceptions.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
