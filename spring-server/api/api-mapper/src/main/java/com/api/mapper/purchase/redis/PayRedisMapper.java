package com.api.mapper.purchase.redis;

import com.czy.api.domain.ao.purchase.OrderStatusAo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *@author 13225
 *@date 2025/8/26 17:22
 */
public interface PayRedisMapper {

    void saveOrderStatus(@NotNull Long userId, @NotNull Long orderId,
                         @NotNull Integer customerStatus, @Nullable Integer merchantStatus);

    void deleteOrderStatus(@NotNull Long userId, @NotNull Long orderId);

    void updateOrderStatus(@NotNull Long userId, @NotNull Long orderId,
                           @NotNull Integer customerStatus, @Nullable Integer merchantStatus);

    OrderStatusAo getOrderStatus(@NotNull Long userId, @NotNull Long orderId);
}
