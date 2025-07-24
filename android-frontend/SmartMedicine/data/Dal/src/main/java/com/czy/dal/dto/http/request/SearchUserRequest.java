package com.czy.dal.dto.http.request;

public class SearchUserRequest extends BaseHttpRequest {
    // userData是因为不知道是账号，还是名称，还是手机号
    public String userData;
}
