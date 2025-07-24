package com.czy.api.domain.dto.http.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

/**
 * @author 13225
 * @date 2025/1/2 13:48
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LoginUserRequest extends DeviceInfoRequest {
    private Integer id;
    private String userName;
    private String account;
    @NotEmpty(message = "用户密码不能为空")
    private String password;
    @NotEmpty(message = "用户手机号不能为空")
    private String phone;
    private Integer permission;
}
