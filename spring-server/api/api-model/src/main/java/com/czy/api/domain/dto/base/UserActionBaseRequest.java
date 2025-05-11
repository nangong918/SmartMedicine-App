package com.czy.api.domain.dto.base;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author 13225
 * @date 2025/5/9 15:53
 */
@Data
public class UserActionBaseRequest {
    @NotEmpty(message = "用户账号不能为空")
    private String userAccount;
    @NotEmpty(message = "行为时间戳不能为空")
    private Long timestamp;
}
