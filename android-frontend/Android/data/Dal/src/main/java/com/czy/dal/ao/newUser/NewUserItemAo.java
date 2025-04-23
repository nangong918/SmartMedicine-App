package com.czy.dal.ao.newUser;

import com.czy.dal.constant.newUserGroup.AddSourceEnum;
import com.czy.dal.entity.ChatEntity;
import com.czy.dal.entity.UserViewEntity;

import java.util.LinkedList;
import java.util.List;

/**
 * 使用场景：点开详情之后展示
 */
public class NewUserItemAo {

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

    // 添加状态
    public AddUserStatusAo addUserStatusAo = new AddUserStatusAo();

    // 这个user是否是被添加的
    public boolean isBeAdd = true;
}
