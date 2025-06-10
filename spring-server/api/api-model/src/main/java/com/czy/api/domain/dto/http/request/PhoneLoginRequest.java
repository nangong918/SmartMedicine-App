package com.czy.api.domain.dto.http.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

/**
 * @author 13225
 * @date 2025/1/2 15:32
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PhoneLoginRequest extends DeviceInfoRequest {
    @NotEmpty(message = "手机号不能为空")
    private String phone;

    @NotEmpty(message = "验证码不能为空")
    private String vcode;

    private String userName;
    private String password;
}
