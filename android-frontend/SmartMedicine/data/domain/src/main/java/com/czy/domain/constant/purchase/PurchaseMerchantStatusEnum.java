package com.czy.domain.constant.purchase;


/**
 * @author 13225
 * @date 2025/8/19 15:04
 * 可购买，已下架，售罄，等待开放
 * 商户状态：merchant_status
 * 相互参考
 * @see com.czy.domain.constant.medicine.AppointmentMerchantStatusEnum
 */
public enum PurchaseMerchantStatusEnum {
    NULL(-1, "暂未查询到状态"),

    AVAILABLE(0, "可购买"),

    NO_AVAILABLE(1, "已下架"),

    SOLD_OUT(2, "售罄"),

    WAITING_OPEN(3, "等待开放"),
    ;

    private final int code;
    private final String status;

    PurchaseMerchantStatusEnum(int code, String status) {
        this.code = code;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public int getCode() {
        return code;
    }

    public static PurchaseMerchantStatusEnum getByCode(int code) {
        for (PurchaseMerchantStatusEnum value : PurchaseMerchantStatusEnum.values()) {
            if (value.code == code) {
                return value;
            }
        }
        return AVAILABLE;
    }
}
