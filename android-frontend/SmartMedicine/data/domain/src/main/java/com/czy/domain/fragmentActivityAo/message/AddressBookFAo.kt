package com.czy.domain.fragmentActivityAo.message

import androidx.lifecycle.MutableLiveData

open class AddressBookFAo {

    var friendsMoments: MutableLiveData<Int> = MutableLiveData(0)

    var newFriends: MutableLiveData<Int> = MutableLiveData(0)

}