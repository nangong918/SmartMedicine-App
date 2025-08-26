package com.czy.api.constant.purchase;

import lombok.Getter;

import java.io.Serializable;

/**
 * @author 13225
 * @date 2025/8/26 16:11
 */
@Getter
public enum PayResultEnum implements Serializable {
    NULL(-2, "未知"),
    // 系统错误
    SYSTEM_ERROR(-1, "系统错误"),
    SUCCESS(0, "成功"),
    /// 失败
    // 售罄
    SOLD_OUT(1, "售罄"),
    // 已下架
    NO_AVAILABLE(2, "已下架"),
    // 支付受限
    LIMITED(3, "支付受限"),
    // 余额不足
    INSUFFICIENT_BALANCE(4, "余额不足"),
    ;

    private final int code;
    private final String status;
    PayResultEnum(int code, String status) {
        this.code = code;
        this.status = status;
    }

    // code -> o
    public static PayResultEnum getByCode(int code) {
        for (PayResultEnum value : PayResultEnum.values()) {
            if (value.code == code) {
                return value;
            }
        }
        return NULL;
    }
}
