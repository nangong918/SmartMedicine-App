package com.czy.api.exception;

import exception.ExceptionEnums;
import lombok.Getter;

/**
 * @author 13225
 * @date 2025/6/26 17:22
 */
@Getter
public enum PurchaseExceptions implements ExceptionEnums {

    // 重复申请，请耐心等待
    REPEAT_APPLY_LOCK("PUR_10001", "重复申请，请耐心等待"),
    // 同商户已存在订单, 暂不可申请
    EXIST_ORDER_LOCK("PUR_10002", "同商户已存在订单, 暂不可申请"),
    // 重复支付, 请勿重复支付
    REPEAT_PAY_LOCK("PUR_10003", "重复支付, 请勿重复支付"),
    // 系统忙碌, 支付请求超时, 请稍后再试
    SYSTEM_BUSY_LOCK("PUR_10004", "系统忙碌, 支付请求超时, 请稍后再试"),
    // 订单不存在
    ORDER_NOT_EXIST("PUR_10005", "订单不存在"),
    // 订单状态错误
    ORDER_STATUS_ERROR("PUR_10006", "订单状态错误"),
    // 充值金额有误
    RECHARGE_AMOUNT_ERROR("PUR_10007", "充值金额有误"),
    // 充值失败
    RECHARGE_FAIL("PUR_10008", "充值失败"),
    // 订单超时
    ORDER_TIMEOUT("PUR_10009", "订单超时"),
    // 订单不存在
    ORDER_EXPIRED("PUR_10010", "订单不存在"),
    // 支付的订单不是待支付状态
    ORDER_NOT_WAIT_PAY("PUR_10011", "支付的订单不是待支付状态"),
    // 订单价格状态异常
    ORDER_PRICE_ERROR("PUR_10012", "订单价格错误"),
    // 订单申请失败, 获取商户库存请求失败
    ORDER_INVENTORY_APPLY_FAILED("PUR_10013", "订单申请失败, 获取商户库存请求失败"),
    ;

    private final String code;
    private final String message;

    PurchaseExceptions(String code, String message) {
        this.code = code;
        this.message = message;
    }

    // code -> o
    public static PurchaseExceptions getByCode(String code) {
        for (PurchaseExceptions value : PurchaseExceptions.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
