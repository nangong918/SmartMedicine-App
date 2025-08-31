package com.czy.domain.ao.intent;

import com.czy.domain.constant.intent.RegisterActivityType;

import java.io.Serializable;

public class RegisterIntentAAo implements Serializable {

    public static final String INTENT_KEY = RegisterIntentAAo.class.getName();

    public int activityType = RegisterActivityType.REGISTER.getType();
    public String phone = "";

}
