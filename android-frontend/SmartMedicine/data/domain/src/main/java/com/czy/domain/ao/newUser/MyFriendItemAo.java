package com.czy.domain.ao.newUser;


import com.czy.domain.constant.ListAddOrDeleteStatusEnum;
import com.czy.domain.entity.UserViewEntity;

public class MyFriendItemAo {

    // 用户View
    public UserViewEntity userViewEntity;

    // 此条状态：
    public Integer checkIsFriendStatus = ListAddOrDeleteStatusEnum.ADD.code;
}
