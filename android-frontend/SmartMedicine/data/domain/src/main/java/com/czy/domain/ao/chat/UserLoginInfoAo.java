package com.czy.domain.ao.chat;

//import android.content.SharedPreferences;
//import android.text.TextUtils;

import com.czy.baseutil.json.BaseBean;
import com.czy.baseutil.object.BaseSharedPreferencesBean;

public class UserLoginInfoAo extends BaseSharedPreferencesBean implements BaseBean {
    public Long userId = -1L;
    public String account;
    public String phone;
    public String userName;
    public Boolean isLogin = false;
    public String avatarUrl;

    public UserLoginInfoAo() {}

    public UserLoginInfoAo(Long userId, String account, String phone, String userName) {
        this.userId = userId;
        this.account = account;
        this.phone = phone;
        this.userName = userName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Boolean getLogin() {
        return isLogin;
    }

    public void setLogin(Boolean login) {
        isLogin = login;
    }

}
