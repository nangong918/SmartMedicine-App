package com.czy.api.constant.purchase;

import lombok.Getter;

/**
 * @author 13225
 * @date 2025/8/19 15:04
 * 可购买，已下架，售罄，等待开放
 */
@Getter
public enum PurchaseStatusEnum {

    AVAILABLE(0, "可购买"),

    SOLD_OUT(1, "已下架"),

    SOLD_OUT_WAITING_OPEN(2, "售罄，等待开放"),

    WAITING_OPEN(3, "等待开放"),
    ;

    private final int code;
    private final String status;

    PurchaseStatusEnum(int code, String status) {
        this.code = code;
        this.status = status;
    }

    public static PurchaseStatusEnum getByCode(int code) {
        for (PurchaseStatusEnum value : PurchaseStatusEnum.values()) {
            if (value.code == code) {
                return value;
            }
        }
        return AVAILABLE;
    }
}
