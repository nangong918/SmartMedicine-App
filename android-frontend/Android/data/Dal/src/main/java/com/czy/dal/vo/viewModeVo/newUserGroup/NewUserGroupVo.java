package com.czy.dal.vo.viewModeVo.newUserGroup;

import androidx.lifecycle.MutableLiveData;

import com.czy.dal.ao.newUser.NewUserItemAo;
import com.czy.dal.vo.entity.addContact.AddContactListVo;

import java.util.LinkedList;
import java.util.List;

// TODO 类图
public class NewUserGroupVo {

    // 是否是用户、群
    public boolean isUserNotGroup = true;

    // 新用户列表 Data：数据内容：点击交互之后展示
    public MutableLiveData<List<NewUserItemAo>> newUserItemListLd = new MutableLiveData<>(new LinkedList<>());

    // 新用户列表 View：RecyclerView的展示内容
    public AddContactListVo addContactListVo = new AddContactListVo();

    // 新群列表
}
