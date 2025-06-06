package com.czy.api.domain.ao.relationship;

import com.czy.api.constant.user_relationship.newUserGroup.AddSourceEnum;
import com.czy.api.domain.entity.ChatEntity;
import com.czy.api.domain.entity.UserViewEntity;
import json.BaseBean;
import lombok.Data;


import java.util.LinkedList;
import java.util.List;

@Data
public class NewUserItemAo implements BaseBean {

    // 是否是添加我的请求 / 是否对我添加别人请求的响应
    public boolean isAddMeNotResponse = true;

    // 添加记录对话信息
    public List<ChatEntity> chatList = new LinkedList<>();

    // 用户View
    public UserViewEntity userViewEntity;

    // 申请时间
    public Long applyTime;

    // 处理时间
    public Long handleTime;

    // 添加来源：手机，账号，扫码，群
    public Integer addSource = AddSourceEnum.PHONE.code;

    // 这个user是否是被添加的
    public boolean isBeAdd = true;

    // 添加状态
    public AddUserStatusAo addUserStatusAo = new AddUserStatusAo();

}
