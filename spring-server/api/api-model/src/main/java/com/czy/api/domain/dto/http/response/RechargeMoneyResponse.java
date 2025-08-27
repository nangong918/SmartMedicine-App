package com.czy.api.domain.dto.http.response;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 13225
 * @date 2025/8/27 18:26
 */
@Data
public class RechargeMoneyResponse {
    public BigDecimal rechargeAmount;
    public BigDecimal balance;
    public Long userId;
}
