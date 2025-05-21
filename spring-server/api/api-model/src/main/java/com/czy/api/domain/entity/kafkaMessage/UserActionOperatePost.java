package com.czy.api.domain.entity.kafkaMessage;

import com.czy.api.domain.entity.kafkaMessage.base.UserActionMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 13225
 * @date 2025/5/21 15:46
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserActionOperatePost extends UserActionMessage {

    public static final String TOPIC = "USER_ACTION_OPERATE_POST";

    private Long postId;
    private Integer operateType;
}
