package com.czy.dal.vo.entity.addContact;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AddContactListVo {

    public List<AddContactItemVo> contactItemList = new ArrayList<>();

    public AddContactItemVo getByVoId(int id) {
        return Optional.of(contactItemList)
                .map(list -> {
                    for (AddContactItemVo itemVo : list){
                        if (itemVo.id == id) {
                            return itemVo;
                        }
                    }
                    return null;
                })
                .orElse(null);
    }

    public AddContactItemVo getByAccount(String account) {
        if (TextUtils.isEmpty(account)){
            return null;
        }
        return Optional.of(contactItemList)
                .map(list -> {
                    for (AddContactItemVo itemVo : list){
                        if (account.equals(itemVo.account)) {
                            return itemVo;
                        }
                    }
                    return null;
                })
                .orElse(null);
    }
}
