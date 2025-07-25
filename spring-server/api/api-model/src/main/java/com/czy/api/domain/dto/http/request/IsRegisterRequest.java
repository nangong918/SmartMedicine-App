package com.czy.api.domain.dto.http.request;

import lombok.Data;

/**
 * @author 13225
 * @date 2025/6/7 17:18
 */
@Data
public class IsRegisterRequest {
    public String phone;
    public String account;
}
