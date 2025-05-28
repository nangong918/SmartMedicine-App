package com.czy.dal.dto.netty.response;

import com.czy.dal.dto.netty.base.BaseResponseData;

public class AddUserToTargetUserResponse extends BaseResponseData {

    // 请求方的account
    public String account;

    // 请求方的name
    public String name;

    // 请求方的附加content
    public String additionalContent;

    // 请求方的头像
    public String avatarUrl;

    // 请求方的请求添加时间
    public String requestTime;

}
