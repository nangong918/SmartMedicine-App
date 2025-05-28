package com.czy.dal.vo.entity.contact;

import androidx.lifecycle.MutableLiveData;

import com.czy.dal.ao.chat.ChatContactItemAo;

import java.util.LinkedList;
import java.util.List;

public class ContactListVo {

    public final MutableLiveData<List<ChatContactItemAo>> contactItemList = new MutableLiveData<>(new LinkedList<>());

}
