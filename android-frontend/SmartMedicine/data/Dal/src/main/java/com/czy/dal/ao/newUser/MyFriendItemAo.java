package com.czy.dal.ao.newUser;


import com.czy.dal.constant.ListAddOrDeleteStatusEnum;
import com.czy.dal.entity.UserViewEntity;

public class MyFriendItemAo {

    // 用户View
    public UserViewEntity userViewEntity;

    // 此条状态：
    public Integer checkIsFriendStatus = ListAddOrDeleteStatusEnum.ADD.code;
}
