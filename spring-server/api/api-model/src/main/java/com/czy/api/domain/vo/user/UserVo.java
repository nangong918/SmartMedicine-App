package com.czy.api.domain.vo.user;

import lombok.Data;

/**
 * @author 13225
 * @date 2025/6/10 16:21
 */
@Data
public class UserVo {
    // view
    public String userName;
    public String account;
    public String avatarUrl;

    // data
    public Long userId;
    public String phone;
}
