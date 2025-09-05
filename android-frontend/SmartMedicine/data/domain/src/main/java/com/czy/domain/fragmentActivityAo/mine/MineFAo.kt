package com.czy.domain.fragmentActivityAo.mine

import androidx.lifecycle.MutableLiveData

open class MineFAo{
    open val avatarUrlLd: MutableLiveData<String> = MutableLiveData("")
    open val userNameLd: MutableLiveData<String> = MutableLiveData("")
    open val userAccountLd: MutableLiveData<String> = MutableLiveData("")

    open val myDynamicLd: MutableLiveData<String> = MutableLiveData("0")
    open val myFollowLd: MutableLiveData<String> = MutableLiveData("0")
    open val myFansLd: MutableLiveData<String> = MutableLiveData("0")

    open val moneyLd: MutableLiveData<String> = MutableLiveData("-")
}
