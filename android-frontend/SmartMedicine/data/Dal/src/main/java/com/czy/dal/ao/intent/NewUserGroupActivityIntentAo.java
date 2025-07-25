package com.czy.dal.ao.intent;

import com.czy.dal.constant.newUserGroup.UserGroupEnum;

import java.io.Serializable;

public class NewUserGroupActivityIntentAo implements Serializable {

    public static final String INTENT_KEY = NewUserGroupActivityIntentAo.class.getName();

    public UserGroupEnum userGroupEnum = UserGroupEnum.USER;

}
