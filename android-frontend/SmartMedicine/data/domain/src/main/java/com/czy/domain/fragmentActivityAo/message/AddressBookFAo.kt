package com.czy.domain.fragmentActivityAo.message

import androidx.lifecycle.MutableLiveData
import com.czy.domain.vo.entity.contact.ContactListVo

open class AddressBookFAo {

    var friendsMoments: MutableLiveData<Int> = MutableLiveData(0)

    var newFriends: MutableLiveData<Int> = MutableLiveData(0)

    var contactListVo: ContactListVo = ContactListVo()

}