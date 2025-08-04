package com.czy.dal.dto.http.response;

import com.czy.dal.ao.login.LoginTokenAo;
import com.czy.dal.vo.entity.UserEntityVo;

public class LoginSignResponse {
    public UserEntityVo userEntityVo;
    public LoginTokenAo loginTokenAo;
    public boolean comeConnectWebsocket;
}
