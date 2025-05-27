package com.czy.dal.vo.viewModelVo.search;

import androidx.lifecycle.MutableLiveData;

import com.czy.dal.vo.entity.addContact.AddContactListVo;


public class SearchActivityUserVo {
    // 输入框内容
    public final MutableLiveData<String> edtvInputData = new MutableLiveData<>();

    // userList
    public AddContactListVo addContactListVo = new AddContactListVo();
}
