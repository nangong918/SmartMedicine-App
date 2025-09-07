package com.czy.domain.dto.netty.request;


import com.czy.domain.constant.newUserGroup.ApplyStatusEnum;
import com.czy.domain.dto.netty.base.BaseRequestData;

/**
 * @author 13225
 * @date 2025/3/10 17:26
 */

public class DeleteUserRequest extends BaseRequestData {
    public final Integer applyType = ApplyStatusEnum.DELETED.code;
}
