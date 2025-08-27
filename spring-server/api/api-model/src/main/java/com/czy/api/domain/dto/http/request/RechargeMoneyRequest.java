package com.czy.api.domain.dto.http.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author 13225
 * @date 2025/8/27 18:25
 */
@Data
public class RechargeMoneyRequest {
    /**
     * 充值类型
     * @see com.czy.api.constant.purchase.RechargeEnum
     */
    @NotNull(message = "充值类型不能为空")
    public Integer type;
    @NotNull(message = "用户id不能为空")
    public Long userId;
}
