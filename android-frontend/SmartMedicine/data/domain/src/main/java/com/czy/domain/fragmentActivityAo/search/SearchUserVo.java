package com.czy.domain.fragmentActivityAo.search;

import androidx.lifecycle.MutableLiveData;

import com.czy.domain.vo.entity.addContact.AddContactListVo;


public class SearchUserVo {
    // 输入框内容
    public final MutableLiveData<String> edtvInputData = new MutableLiveData<>();

    // userList
    public AddContactListVo addContactListVo = new AddContactListVo();
}
