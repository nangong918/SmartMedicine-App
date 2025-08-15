package com.czy.dal.fragmentActivityAo.search;

import androidx.lifecycle.MutableLiveData;

import com.czy.dal.vo.entity.addContact.AddContactListVo;


public class SearchUserVo {
    // 输入框内容
    public final MutableLiveData<String> edtvInputData = new MutableLiveData<>();

    // userList
    public AddContactListVo addContactListVo = new AddContactListVo();
}
