package com.czy.api.domain.Do.relationship;

import json.BaseBean;
import lombok.Data;

/**
 * @author 13225
 * @date 2025/2/19 11:56
 */
@Data
public class UserFriendDo implements BaseBean {
    private Integer id;
    private Integer userId;
    private Integer friendId;
    private Long addTime = System.currentTimeMillis();
    private Long lastChatTime;
}
