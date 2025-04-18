package com.czy.api.domain.dto.http.response;



import com.czy.api.domain.ao.auth.LoginTokenAo;
import lombok.Data;


@Data
public class LoginSignResponse {
    private String userId;
    private String userName;
    private String account;
    private String phone;
    private LoginTokenAo loginTokenAo;
    private boolean comeConnectWebsocket;
}
