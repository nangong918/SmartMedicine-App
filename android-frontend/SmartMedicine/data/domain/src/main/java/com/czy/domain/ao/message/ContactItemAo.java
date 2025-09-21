package com.czy.domain.ao.message;

import com.czy.baseutil.json.BaseBean;
import com.czy.domain.vo.entity.message.message.ContactItemVo;

public class ContactItemAo implements BaseBean {
    // view
    public ContactItemVo contactItemVo = new ContactItemVo();

    // data
    public String contactAccount;
    public Long contactId;

    public ContactItemAo() {
    }

    public ContactItemAo(ContactItemAo ao) {
        this.contactItemVo = new ContactItemVo(ao.contactItemVo);
        this.contactAccount = ao.contactAccount;
        this.contactId = ao.contactId;
    }

    // 用于判断两个对象是否属于一个对象（用唯一标识符判断）
    public boolean isItemEquals(Object o){
        if (o instanceof ContactItemAo that){
            return this.contactId.equals(that.contactId);
        }
        return false;
    }
}
