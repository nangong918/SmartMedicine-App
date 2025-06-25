package com.czy.api.domain.dto.http.response;



import com.czy.api.domain.ao.auth.LoginTokenAo;
import com.czy.api.domain.vo.user.UserVo;
import lombok.Data;


@Data
public class LoginSignResponse {
    private UserVo userVo;
    private LoginTokenAo loginTokenAo;
    private boolean comeConnectWebsocket;
}
