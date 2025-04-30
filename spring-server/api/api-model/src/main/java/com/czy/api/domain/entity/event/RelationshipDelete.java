package com.czy.api.domain.entity.event;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author 13225
 * @date 2025/4/30 17:41
 */
@Data
public class RelationshipDelete {
    @NotEmpty(message = "发送者 ID 不能为空")
    public Long senderId;
    @NotEmpty(message = "接收者 ID 不能为空")
    public Long receiverId;
}
