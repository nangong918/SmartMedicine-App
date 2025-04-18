package com.czy.api.domain.ao.relationship;


import com.czy.api.constant.relationship.ListAddOrDeleteStatusEnum;
import com.czy.api.domain.entity.UserViewEntity;
import json.BaseBean;


public class MyFriendItemAo implements BaseBean {

    // 用户View
    public UserViewEntity userViewEntity;

    // 此条状态：
    public Integer checkIsFriendStatus = ListAddOrDeleteStatusEnum.ADD.code;
}
