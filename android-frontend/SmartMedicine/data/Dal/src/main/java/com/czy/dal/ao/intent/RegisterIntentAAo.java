package com.czy.dal.ao.intent;

import com.czy.dal.constant.intent.RegisterActivityType;

import java.io.Serializable;

public class RegisterIntentAAo implements Serializable {

    public static final String INTENT_KEY = RegisterIntentAAo.class.getName();

    public int activityType = RegisterActivityType.REGISTER.getType();
    public String phone = "";

}
