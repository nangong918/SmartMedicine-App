package com.czy.api.constant;

import lombok.Getter;

import java.io.Serializable;

/**
 * @author 13225
 * @date 2025/8/19 15:08
 * 未订购, 审核中, 待支付, 待使用, 待评价(已使用), 退款中, 退款失败, 已取消(退款成功)
 * 用户状态：user_status
 */
@Getter
public enum UserOrderStatusEnum implements Serializable {
    NULL(-1, "暂未查询到状态"),
    NOT_ORDERED(0, "未订购"),
    WAITING_AUDIT(1, "审核中"),
    WAITING_PAYMENT(2, "待支付"),
    WAITING_USE(3, "待使用"),
    WAITING_EVALUATION(4, "待评价"),
    REFUNDING(5, "退款中"),
    REFUND_FAILED(6, "退款失败"),
    CANCELED(7, "已取消")
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
        return NULL;
    }

    public static boolean isPaid(int code){
        UserOrderStatusEnum statusEnum = getByCode(code);

        return  // 待使用
                statusEnum.equals(WAITING_USE) ||
                // 待评价
                statusEnum.equals(WAITING_EVALUATION) ||
                // 退款中
                statusEnum.equals(REFUNDING) ||
                // 退款失败
                statusEnum.equals(REFUND_FAILED);
    }
}
