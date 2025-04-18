package com.czy.api.domain.dto.http.request;

import lombok.Data;

/**
 * @author 13225
 * @date 2025/4/16 11:01
 */

@Data
public class ResetUserInfoRequest {
    public String account;
    public String userName;
    public String avatarUrl;
}
