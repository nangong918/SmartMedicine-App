package com.czy.api.domain.dto.http.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

@EqualsAndHashCode(callSuper = true)
@Data
public class SendSmsRequest extends DeviceInfoRequest {

    @NotEmpty(message = "手机号不能为空")
    public String phone;

    public SendSmsRequest() {
    }

}
