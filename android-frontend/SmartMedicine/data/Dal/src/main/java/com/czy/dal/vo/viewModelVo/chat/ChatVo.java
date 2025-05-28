package com.czy.dal.vo.viewModelVo.chat;

import androidx.lifecycle.MutableLiveData;

public class ChatVo {

    // view
    // 名字
    public final MutableLiveData<String> name = new MutableLiveData<>("");
    // list
    public ChatListVo chatListVo = new ChatListVo();
    // 输入框
    public final MutableLiveData<String> inputText = new MutableLiveData<>("");
    // 头像
    public MutableLiveData<String> avatarUrlOrUri = new MutableLiveData<>("");
    // 加载中
    public final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(true);

    // data
    // account
    public String contactAccount;
}
