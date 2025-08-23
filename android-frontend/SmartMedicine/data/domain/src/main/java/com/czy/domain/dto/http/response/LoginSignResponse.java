package com.czy.domain.dto.http.response;

import com.czy.domain.ao.login.LoginTokenAo;
import com.czy.domain.vo.entity.UserEntityVo;

public class LoginSignResponse {
    public UserEntityVo userEntityVo;
    public LoginTokenAo loginTokenAo;
    public boolean comeConnectWebsocket;
}
