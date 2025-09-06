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

    open val isFinishedLdMap : Map<String, MutableLiveData<Boolean>> = mapOf(
        "社区动态" to MutableLiveData(true),
        "关注" to MutableLiveData(false),
        "粉丝" to MutableLiveData(false),
        "我的动态" to MutableLiveData(true),
        "我的收藏" to MutableLiveData(true),
        "我的运动" to MutableLiveData(false),
        "我的饮食" to MutableLiveData(false),
        "我的健康提醒" to MutableLiveData(true),
        "我的购物" to MutableLiveData(true),
        "我的健康" to MutableLiveData(false),
        "我的订单" to MutableLiveData(true),
        "充值" to MutableLiveData(true),
        "流水记录" to MutableLiveData(false),
        "设置" to MutableLiveData(true),
    )

}

