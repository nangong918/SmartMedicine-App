package com.czy.api.constant;

import lombok.Getter;

/**
 * @author 13225
 * @date 2025/8/19 15:08
 * 待支付，待使用，待评价，退款中，退款失败，已取消
 * 用户状态：user_status
 */
@Getter
public enum UserOrderStatusEnum {

    WAITING_PAYMENT(0, "待支付"),
    WAITING_USE(1, "待使用"),
    WAITING_EVALUATION(2, "待评价"),
    REFUNDING(3, "退款中"),
    REFUND_FAILED(4, "退款失败"),
    CANCELED(5, "已取消")
    ;

    private final int code;
    private final String status;

    UserOrderStatusEnum(int code, String status) {
        this.code = code;
        this.status = status;
    }

    // code -> o
    public static UserOrderStatusEnum getByCode(int code) {
        for (UserOrderStatusEnum value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return WAITING_PAYMENT;
    }
}
