package com.czy.domain.constant.medicine;



/**
 * @author 13225
 * @date 2025/8/18 15:56
 * 商户状态：merchant_status
 * 相互参考
 * @see com.czy.domain.constant.purchase.PurchaseMerchantStatusEnum
 */
public enum AppointmentMerchantStatusEnum {
    NULL(-1, "暂未查询到状态"),
    // 可预约
    AVAILABLE(0, "可预约"),
    // 已过期
    EXPIRED(1, "已过期"),
    // 已无可预约数量
    NO_AVAILABLE(2, "已无可预约数量"),
    // 等待开放预约
    WAITING_OPEN(3, "等待开放预约"),
    ;

    private final String name;
    private final int code;

    AppointmentMerchantStatusEnum(int code, String name) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }

    // code -> o
    public static AppointmentMerchantStatusEnum getByCode(int code) {
        for (AppointmentMerchantStatusEnum value : AppointmentMerchantStatusEnum.values()) {
            if (value.code == code) {
                return value;
            }
        }
        return WAITING_OPEN;
    }

}
