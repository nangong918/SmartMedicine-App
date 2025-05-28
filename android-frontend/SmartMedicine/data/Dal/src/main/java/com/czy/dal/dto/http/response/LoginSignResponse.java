package com.czy.dal.dto.http.response;

import com.czy.dal.ao.login.LoginTokenAo;

public class LoginSignResponse {
    public String userId;
    public String userName;
    public String account;
    public String phone;
    public LoginTokenAo loginTokenAo;
    public boolean comeConnectWebsocket;
    
}
