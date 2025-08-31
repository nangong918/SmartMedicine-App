package com.czy.api.constant.purchase;

import lombok.Getter;

import java.io.Serializable;

/**
 * @author 13225
 * @date 2025/8/27 18:32
 */
@Getter
public enum RechargeEnum implements Serializable {
    NULL(0),
    M100(100),
    M500(500),
    M1000(1000),
    ;
    private final Integer amount;
    RechargeEnum(Integer amount) {
        this.amount = amount;
    }
    public static RechargeEnum getByCode(Integer code) {
        for (RechargeEnum value : values()) {
            if (value.amount.equals(code)) {
                return value;
            }
        }
        return NULL;
    }
}
