package com.czy.api.exception;

import exception.ExceptionEnums;
import lombok.Getter;

/**
 * @author 13225
 * @date 2025/6/26 17:22
 */
@Getter
public enum UserExceptions implements ExceptionEnums {

    // 用户资料存在问题
    USER_INFO_ERROR("U_10001", "用户资料存在问题"),

    // 手机号已注册
    PHONE_REGISTERED("U_10003", "手机号已注册"),
    // 账号已注册
    ACCOUNT_REGISTERED("U_10004", "账号已注册"),
    // 手机号不存在
    PHONE_NOT_EXIST("U_10005", "手机号不存在"),
    // 账号不存在
    ACCOUNT_NOT_EXIST("U_10006", "账号不存在"),
    // 用户不存在
    USER_NOT_EXIST("U_10007", "用户不存在"),
    // 密码错误
    PASSWORD_ERROR("U_10008", "密码错误"),
    // 重置密码失败
    RESET_PASSWORD_FAIL("U_10009", "重置密码失败"),
    // 重置用户信息失败
    RESET_USER_INFO_FAIL("U_10010", "重置用户信息失败"),
    // 登录失败
    LOGIN_FAIL("U_10011", "登录失败"),
    // 头像未上传
    IMAGE_NOT_UPLOAD("U_10012", "头像未上传"),
    ;

    private final String code;
    private final String message;

    UserExceptions(String code, String message) {
        this.code = code;
        this.message = message;
    }

    // code -> o
    public static UserExceptions getByCode(String code) {
        for (UserExceptions value : UserExceptions.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
