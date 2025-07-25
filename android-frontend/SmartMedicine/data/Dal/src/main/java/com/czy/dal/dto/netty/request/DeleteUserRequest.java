package com.czy.dal.dto.netty.request;


import com.czy.dal.constant.newUserGroup.ApplyStatusEnum;
import com.czy.dal.dto.netty.base.BaseRequestData;

/**
 * @author 13225
 * @date 2025/3/10 17:26
 */

public class DeleteUserRequest extends BaseRequestData {
    public final Integer applyType = ApplyStatusEnum.DELETED.code;
}
