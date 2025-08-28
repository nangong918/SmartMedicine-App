package com.czy.purchase.service;

import com.czy.api.constant.purchase.RechargeEnum;
import com.czy.api.domain.dto.http.response.RechargeMoneyResponse;
import org.jetbrains.annotations.NotNull;

/**
 * @author 13225
 * @date 2025/8/27 11:33
 */
public interface PayService {
    // 事务处理 + 回调medicine + 测试
    int payAppointmentOrder(Long userId, Long orderId);

    @NotNull RechargeMoneyResponse testRecharge(@NotNull Long userId, @NotNull RechargeEnum rechargeEnum);
}
