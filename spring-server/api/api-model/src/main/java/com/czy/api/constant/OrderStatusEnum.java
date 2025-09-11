package com.czy.api.constant;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * @author 13225
 * @date 2025/9/11 11:42
 */
public enum OrderStatusEnum implements Serializable {
    // 未知状态
    NULL(-1, "未知状态"),
    // 未订购
    UNORDERED(0, "未订购"),
    // 审核中
    WAITING_AUDIT(1, "审核中"),
    // 待支付
    WAIT_PAY(2, "待支付"),
    // 待使用
    WAIT_USE(3, "待使用"),
    // 待评价 (已使用)
    WAIT_COMMENT(4, "待评价"),
    // 退款中
    REFUNDING(5, "退款中"),
    // 退款成功
    REFUND_SUCCESS(6, "退款成功"),
    // 退款失败
    REFUND_FAILED(7, "退款失败"),
    // 已完成 (评价之后)
    COMPLETED(8, "已完成"),
    // 订单已取消
    CANCELED(9, "订单已取消"),
    // 订单过期
    EXPIRED(10, "订单过期"),
    ;
    private final int code;
    private final String name;

    OrderStatusEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    // code -> o
    @NotNull
    public static OrderStatusEnum getOrderStatusEnumByCode(int code) {
        for (OrderStatusEnum value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return NULL;
    }
}
