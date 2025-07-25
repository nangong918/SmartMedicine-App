package com.czy.api.domain.dto.http.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

@EqualsAndHashCode(callSuper = true)
@Data
public class SendSmsRequest extends DeviceInfoRequest {

    @NotEmpty(message = "手机号不能为空")
    public String phone;
    // 类型：1.用于用于登录，2.用于注册，3.用于重置密码
    public String smsType;

    public SendSmsRequest() {
    }

}
