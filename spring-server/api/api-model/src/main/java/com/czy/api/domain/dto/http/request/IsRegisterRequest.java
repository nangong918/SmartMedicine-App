package com.czy.api.domain.dto.http.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author 13225
 * @date 2025/6/7 17:18
 */
@Data
public class IsRegisterRequest {
    @NotEmpty(message = "手机号不能为空")
    public String phone;
}
