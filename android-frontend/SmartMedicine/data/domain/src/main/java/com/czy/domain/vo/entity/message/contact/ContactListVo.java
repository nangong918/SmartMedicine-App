package com.czy.domain.vo.entity.message.contact;

import androidx.lifecycle.MutableLiveData;

import com.czy.domain.ao.message.ContactItemAo;

import java.util.LinkedList;
import java.util.List;

public class ContactListVo {

    public final MutableLiveData<List<ContactItemAo>> contactItemList = new MutableLiveData<>(new LinkedList<>());

}
