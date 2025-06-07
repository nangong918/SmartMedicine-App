package com.czy.auth.constant;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author 13225
 * @date 2025/4/11 14:49
 */
@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "sms.constant")
public class SmsConstant {

    public static final String SMS_CODE_KEY = "Sms_Code:";

    private long smsCodeExpireTime = 60L * 5;
    private String phonePrefix = "1";
    private int phoneLength = 11;
    private int codeLength = 6;
}
