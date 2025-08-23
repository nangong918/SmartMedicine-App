package com.czy.domain.ao.intent;

import com.czy.domain.constant.newUserGroup.UserGroupEnum;

import java.io.Serializable;

public class NewUserActivityIntentAo implements Serializable {

    public static final String INTENT_KEY = NewUserActivityIntentAo.class.getName();

    public UserGroupEnum userGroupEnum = UserGroupEnum.USER;

}
