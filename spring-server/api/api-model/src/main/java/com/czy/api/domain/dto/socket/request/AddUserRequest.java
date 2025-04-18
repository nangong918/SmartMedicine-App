package com.czy.api.domain.dto.socket.request;


import com.czy.api.constant.relationship.newUserGroup.ApplyStatusEnum;
import com.czy.api.domain.dto.base.BaseRequestData;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;


@EqualsAndHashCode(callSuper = true)
@Data
public class AddUserRequest extends BaseRequestData {
    @NotEmpty(message = "需要添加的用户账号不能为空")
    private String addUserAccount;
    @NotEmpty(message = "我的账号不能为空")
    private String myAccount;
    @NotEmpty(message = "我的名字不能为空")
    private String myName;
    public String addContent;
    @NotEmpty(message = "添加来源不能为空")
    public Integer source;
    public Integer applyType = ApplyStatusEnum.NOT_APPLY.code;
}
