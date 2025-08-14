package com.czy.dal.ao.intent;

import com.czy.dal.constant.newUserGroup.UserGroupEnum;

import java.io.Serializable;

public class NewUserActivityIntentAo implements Serializable {

    public static final String INTENT_KEY = NewUserActivityIntentAo.class.getName();

    public UserGroupEnum userGroupEnum = UserGroupEnum.USER;

}
