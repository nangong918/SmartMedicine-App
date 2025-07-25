package com.czy.dal.ao.intent;

import com.czy.baseUtilsLib.json.BaseBean;
import com.czy.dal.constant.SearchEnum;

import java.io.Serializable;

public class SearchActivityIntentAo implements Serializable, BaseBean {

    public static final String INTENT_KEY = SearchActivityIntentAo.class.getName();

    public SearchEnum searchType = SearchEnum.USER;
}
