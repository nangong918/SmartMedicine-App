package com.czy.api.domain.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class FriendViewEntity extends UserViewEntity implements Serializable {
    // 备注
    public String remark;

    /**
     * 他妈的，傻逼MyBatis，不支持多级继承，还要老子亲自把父类字段搬过来，
     * 真傻逼，早点淘汰傻逼框架
     */
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
