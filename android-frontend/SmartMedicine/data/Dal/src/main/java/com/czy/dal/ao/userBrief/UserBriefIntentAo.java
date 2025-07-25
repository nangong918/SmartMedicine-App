package com.czy.dal.ao.userBrief;

import com.czy.baseUtilsLib.json.BaseBean;

import java.io.Serializable;

public class UserBriefIntentAo implements Serializable, BaseBean {

    // view
    public String userName;
    public String avatarUrl;

    // data
    public String userAccount;
    // 必填
    public Long userId;
}
