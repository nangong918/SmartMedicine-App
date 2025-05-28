package com.czy.dal.dto.netty.response;

import com.czy.dal.ao.newUser.AddUserStatusAo;
import com.czy.dal.constant.newUserGroup.ApplyStatusEnum;
import com.czy.dal.dto.netty.base.BaseResponseData;

public class CancelAddMeResponse extends BaseResponseData {
    // 添加状态
    public AddUserStatusAo addUserStatusAo = new AddUserStatusAo();
    // 附加消息
    public String additionalContent;
    // user账号
    public String userAccount;
    // user名称
    public String userName;
    // 用户头像
    public String avatarUrl;

    public CancelAddMeResponse() {
        addUserStatusAo.applyStatus = ApplyStatusEnum.NOT_APPLY.code;
    }
}
