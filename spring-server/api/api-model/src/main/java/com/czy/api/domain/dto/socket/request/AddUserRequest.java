package com.czy.api.domain.dto.socket.request;


import com.czy.api.constant.user_relationship.newUserGroup.ApplyStatusEnum;
import com.czy.api.domain.dto.base.BaseRequestData;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;


@EqualsAndHashCode(callSuper = true)
@Data
public class AddUserRequest extends BaseRequestData {
    @NotEmpty(message = "我的名字不能为空")
    private String myName;
    public String addContent;
    @NotEmpty(message = "添加来源不能为空")
    public Integer source;
    // 申请类型
    public Integer applyType = ApplyStatusEnum.NOT_APPLY.code;
}
