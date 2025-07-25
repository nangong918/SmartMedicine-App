package com.czy.api.domain.entity.kafkaMessage;

import com.czy.api.domain.entity.kafkaMessage.base.UserActionMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 13225
 * @date 2025/5/21 15:47
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserActionCommentPost extends UserActionMessage {

    private Long postId;
    private String comment;
}
