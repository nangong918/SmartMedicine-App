package com.czy.domain.entity;

public class UserViewEntity {
    // userId
    public Long userId;

    // user账号
    public String userAccount;

    // user名称
    public String userName;

    // 用户头像
    public Long avatarFileId;

    // 用户头像url (无法查询出来，需要去ossService用minio或者redis获取)
    public String avatarUrl;
}
