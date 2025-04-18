package com.czy.api.constant.netty;

import java.util.Objects;

/**
 * @author 13225
 * @date 2025/2/8 16:27
 */
public enum SocketErrorCode {
    // 接收用户不存在
    RECEIVER_NOT_EXIST("E10001", "接收用户不存在"),
    // 发送用户不存在
    SENDER_NOT_EXIST("E10002", "您还未登录"),
    // 注册数据不完整
    REGISTER_DATA_INCOMPLETE("E10003", "注册数据不完整"),
    ;

    public final String code;
    public final String desc;

    SocketErrorCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    // 通过code获取枚举
    public static SocketErrorCode getByCode(String code) {
        for (SocketErrorCode errorCode : SocketErrorCode.values()) {
            if (Objects.equals(errorCode.code, code)) {
                return errorCode;
            }
        }
        return null;
    }
}
