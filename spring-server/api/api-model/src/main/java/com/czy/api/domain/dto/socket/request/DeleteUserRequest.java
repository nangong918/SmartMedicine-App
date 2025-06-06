package com.czy.api.domain.dto.socket.request;


import com.czy.api.constant.user_relationship.newUserGroup.ApplyStatusEnum;
import com.czy.api.domain.dto.base.BaseRequestData;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 13225
 * @date 2025/3/10 17:26
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DeleteUserRequest extends BaseRequestData {
    public final Integer applyType = ApplyStatusEnum.DELETED.code;
}
