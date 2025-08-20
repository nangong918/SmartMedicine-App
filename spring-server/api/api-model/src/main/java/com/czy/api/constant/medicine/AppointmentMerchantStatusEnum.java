package com.czy.api.constant.medicine;

import com.czy.api.constant.BaseEnum;
import lombok.Getter;

/**
 * @author 13225
 * @date 2025/8/18 15:56
 * 商户状态：merchant_status
 * 相互参考
 * @see com.czy.api.constant.purchase.PurchaseMerchantStatusEnum
 */
@Getter
public enum AppointmentMerchantStatusEnum {

    // 可预约
    AVAILABLE("可预约", 0),
    // 已过期
    EXPIRED("已过期", 1),
    // 已无可预约数量
    NO_AVAILABLE( "已无可预约数量", 2),
    // 等待开放预约
    WAITING_OPEN("等待开放预约", 3),
    ;

    private final String name;
    private final int code;

    AppointmentMerchantStatusEnum(String name, int code) {
        this.name = name;
        this.code = code;
    }

    // o -> BaseEnum
    public BaseEnum toBaseEnum() {
        return new BaseEnum() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public int getCode() {
                return code;
            }
        };
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
