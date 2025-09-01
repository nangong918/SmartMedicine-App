package com.api.mapper.purchase.redis;

import com.czy.api.domain.ao.purchase.OrderStatusAo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 *@author 13225
 *@date 2025/8/26 17:22
 */
public interface PayRedisMapper {

    void saveOrderStatus(@NotNull Long userId, @NotNull Long orderId,
                         @NotNull Integer customerStatus, @Nullable Integer merchantStatus,
                         @Nullable LocalDateTime merchantEndTime, @NotNull BigDecimal totalPrice);

    void deleteOrderStatus(@NotNull Long userId, @NotNull Long orderId);

    /**
     * 修改订单状态 (全部, 会删除原先全部状态)
     * @param userId            用户id
     * @param orderId           订单id
     * @param customerStatus    用户订单状态
     * @param merchantStatus    商户订单状态
     * @param merchantEndTime   订单结束时间
     * @param totalPrice        订单总价
     */
    void updateOrderStatus(@NotNull Long userId, @NotNull Long orderId,
                           @NotNull Integer customerStatus, @Nullable Integer merchantStatus,
                           @Nullable LocalDateTime merchantEndTime, @NotNull BigDecimal totalPrice);

    /**
     * 更新订单状态 (保留价商户信息: 商户结束状态和总价)
     *
     * @param userId            用户id
     * @param orderId           订单id
     * @param customerStatus    用户订单状态
     * @param merchantStatus    商户订单状态
     */
    void updateOrderStatus(@NotNull Long userId, @NotNull Long orderId,
                           @NotNull Integer customerStatus, @Nullable Integer merchantStatus);

    OrderStatusAo getOrderStatus(@NotNull Long userId, @NotNull Long orderId);
}
