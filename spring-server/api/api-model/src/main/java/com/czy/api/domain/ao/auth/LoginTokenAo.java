package com.czy.api.domain.ao.auth;

import lombok.Data;

/**
 * @author 13225
 * @date 2025/1/18 11:21
 */
@Data
public class LoginTokenAo {
    private String accessToken;
    private String refreshToken;

    public LoginTokenAo() {
    }

    public LoginTokenAo(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
