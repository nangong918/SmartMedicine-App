package com.czy.api.domain.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class FriendViewEntity extends UserViewEntity implements Serializable {
    // 备注
    public String remark;
}
