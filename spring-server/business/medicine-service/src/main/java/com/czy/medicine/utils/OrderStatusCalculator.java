package com.czy.medicine.utils;

import com.czy.api.constant.OrderStatusEnum;
import com.czy.api.constant.UserOrderStatusEnum;
import com.czy.api.constant.medicine.AppointmentMerchantStatusEnum;
import org.jetbrains.annotations.NotNull;

/**
 * @author 13225
 * @date 2025/9/11 11:52
 * 此份代码需要Spring端和Android端同步
 */
public class OrderStatusCalculator {

    @NotNull
    public static OrderStatusEnum calculateOrderStatus(
        @NotNull AppointmentMerchantStatusEnum merchantStatus,
        @NotNull UserOrderStatusEnum customerStatus
    ){
        /// 单个 商户 状态直接判断
        // null
        if (AppointmentMerchantStatusEnum.NULL.equals(merchantStatus)){
            return OrderStatusEnum.NULL;
        }
        // 未开放 -> 审核中
        if (AppointmentMerchantStatusEnum.WAITING_OPEN.equals(merchantStatus)){
            return OrderStatusEnum.WAITING_AUDIT;
        }
        // 下架了 -> 订单过期
        if (AppointmentMerchantStatusEnum.EXPIRED.equals(merchantStatus)){
            return OrderStatusEnum.EXPIRED;
        }

        /// 单个 用户 状态直接判断
        // 未知, 未订购 -> 未订购
        if (UserOrderStatusEnum.NULL.equals(customerStatus) ||
                UserOrderStatusEnum.NOT_ORDERED.equals(customerStatus)){
            return OrderStatusEnum.UNORDERED;
        }
        if (UserOrderStatusEnum.WAITING_AUDIT.equals(customerStatus)){
            return OrderStatusEnum.WAITING_AUDIT;
        }
        if (UserOrderStatusEnum.WAITING_PAYMENT.equals(customerStatus)){
            return OrderStatusEnum.WAIT_PAY;
        }
        if (UserOrderStatusEnum.WAITING_USE.equals(customerStatus)){
            return OrderStatusEnum.WAIT_USE;
        }
        if (UserOrderStatusEnum.WAITING_EVALUATION.equals(customerStatus)){
            return OrderStatusEnum.WAIT_COMMENT;
        }
        if (UserOrderStatusEnum.REFUNDING.equals(customerStatus)){
            return OrderStatusEnum.REFUNDING;
        }
        if (UserOrderStatusEnum.REFUND_FAILED.equals(customerStatus)){
            return OrderStatusEnum.REFUND_FAILED;
        }
        if (UserOrderStatusEnum.CANCELED.equals(customerStatus)){
            return OrderStatusEnum.CANCELED;
        }
        if (UserOrderStatusEnum.COMPLETED.equals(customerStatus)){
            return OrderStatusEnum.COMPLETED;
        }

        return OrderStatusEnum.NULL;
    }

}
