package com.czy.api.domain.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FriendViewEntity extends UserViewEntity{
    // 备注
    public String remark;
}
