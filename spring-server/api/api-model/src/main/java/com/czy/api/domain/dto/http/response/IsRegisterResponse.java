package com.czy.api.domain.dto.http.response;

import lombok.Data;

/**
 * @author 13225
 * @date 2025/6/7 17:18
 */
@Data
public class IsRegisterResponse {
    public String phone;
    public boolean isRegister;
}
