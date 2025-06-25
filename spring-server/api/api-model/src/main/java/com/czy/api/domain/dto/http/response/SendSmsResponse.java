package com.czy.api.domain.dto.http.response;

import lombok.Data;

/**
 * @author 13225
 * @date 2025/6/24 16:42
 */
@Data
public class SendSmsResponse {
    public String phone;
    public String smsType;
}
