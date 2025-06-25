package com.czy.dal.dto.http.response;

import com.czy.dal.ao.login.LoginTokenAo;
import com.czy.dal.vo.entity.UserVo;

public class LoginSignResponse {
    public UserVo userVo;
    public LoginTokenAo loginTokenAo;
    public boolean comeConnectWebsocket;
}
