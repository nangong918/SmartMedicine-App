package com.czy.api.domain.dto.socket.response;


import com.czy.api.constant.relationship.newUserGroup.ApplyStatusEnum;
import com.czy.api.domain.dto.base.BaseResponseData;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 13225
 * @date 2025/3/10 17:26
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DeleteUserResponse extends BaseResponseData {
    public final Integer applyStatus = ApplyStatusEnum.DELETED.code;
}
