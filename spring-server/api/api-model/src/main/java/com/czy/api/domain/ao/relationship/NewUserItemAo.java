package com.czy.api.domain.ao.relationship;

import com.czy.api.constant.relationship.newUserGroup.AddSourceEnum;
import com.czy.api.domain.bo.relationship.NewUserItemBo;
import com.czy.api.domain.entity.ChatEntity;
import com.czy.api.domain.entity.UserViewEntity;
import com.czy.api.utils.JsonUtil;
import json.BaseBean;


import java.util.LinkedList;
import java.util.List;

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

    public void setByNewUserItemBo(NewUserItemBo bo){
        this.isAddMeNotResponse = bo.isAddMeNotResponse;
        // string -> list
        this.chatList = JsonUtil.getListEntity(bo.chatList, ChatEntity.class);

        this.userViewEntity = new UserViewEntity();
        this.userViewEntity.userName = bo.userName;
        this.userViewEntity.userAccount = bo.userAccount;
        this.userViewEntity.avatarFileId = bo.avatarFileId;
        this.applyTime = bo.applyTime;
        this.handleTime = bo.handleTime;
        this.addSource = bo.addSource;
        this.addUserStatusAo.applyStatus = bo.applyStatus;
        this.addUserStatusAo.handleStatus = bo.handleStatus;
        this.addUserStatusAo.isBlack = bo.isBlack != 0;
        this.addUserStatusAo.applyAccount = bo.applyAccount;
        this.addUserStatusAo.handlerAccount = bo.handlerAccount;
        this.isBeAdd = this.addUserStatusAo.isBeAdd(bo.applyAccount);
    }
}
