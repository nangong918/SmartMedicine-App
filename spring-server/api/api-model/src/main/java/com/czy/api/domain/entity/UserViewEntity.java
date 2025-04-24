package com.czy.api.domain.entity;


import com.czy.api.domain.Do.user.UserDo;
import json.BaseBean;

public class UserViewEntity implements BaseBean {

    // user账号
    public String userAccount;

    // user名称
    public String userName;

    // 用户头像
    public String avatarUrl;

    public void setByLoginUserDo(UserDo userDo) {
        if (userDo == null){
            return;
        }
        this.userAccount = userDo.getAccount();
        this.userName = userDo.getUserName();
        this.avatarUrl = userDo.getAvatarFileId();
    }
}
