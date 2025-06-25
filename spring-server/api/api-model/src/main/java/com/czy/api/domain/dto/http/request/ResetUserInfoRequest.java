package com.czy.api.domain.dto.http.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author 13225
 * @date 2025/4/16 11:01
 */

@Data
public class ResetUserInfoRequest {
    public String account;
    @NotEmpty(message = "用户名不能为空")
    public String newUserName;
}
