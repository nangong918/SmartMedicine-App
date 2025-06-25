package com.czy.dal.ao.login;


import com.czy.baseUtilsLib.json.BaseBean;
import com.czy.baseUtilsLib.object.BaseSharedPreferencesBean;

/**
 * @author 13225
 * @date 2025/1/18 11:21
 */
public class LoginTokenAo extends BaseSharedPreferencesBean implements BaseBean {
    public String accessToken;
    public String refreshToken;

    public LoginTokenAo() {
    }

    public LoginTokenAo(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
