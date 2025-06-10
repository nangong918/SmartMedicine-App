package com.czy.api.domain.ao.user;

import lombok.Builder;
import lombok.Data;

/**
 * @author 13225
 * @date 2025/6/10 17:27
 */
@Data
@Builder
public class UserInfoAo {
    private Long userId;
    private String username;
    private String account;
}
