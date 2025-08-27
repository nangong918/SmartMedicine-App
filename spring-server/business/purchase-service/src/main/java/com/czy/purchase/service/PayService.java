package com.czy.purchase.service;

import com.czy.api.constant.purchase.RechargeEnum;
import com.czy.api.domain.dto.http.response.RechargeMoneyResponse;
import org.jetbrains.annotations.NotNull;

/**
 * @author 13225
 * @date 2025/8/27 11:33
 */
public interface PayService {
    @NotNull RechargeMoneyResponse testRecharge(@NotNull Long userId, @NotNull RechargeEnum rechargeEnum);
}
