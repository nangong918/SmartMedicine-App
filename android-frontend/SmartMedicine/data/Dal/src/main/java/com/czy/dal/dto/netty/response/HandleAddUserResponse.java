package com.czy.dal.dto.netty.response;

import com.czy.dal.dto.netty.base.BaseResponseData;

public class HandleAddUserResponse extends BaseResponseData {
    // 添加状态 AddUserStatusAo
    // 申请状态
    public int applyStatus;
    // 处理状态
    public int handleStatus;
    // 是否拉黑
    public boolean isBlack = false;
    // applyAccount + handlerAccount -> applyAccount是否是本号主 -> 判断此View是否是被添加
    // applyAccount
    public String applyAccount;
    // handlerAccount
    public String handlerAccount;

    // 附加消息
    public String additionalContent;
    // user账号
    public String userAccount;
    // user名称
    public String userName;
    // 用户头像
    public String avatarUrl;
}
