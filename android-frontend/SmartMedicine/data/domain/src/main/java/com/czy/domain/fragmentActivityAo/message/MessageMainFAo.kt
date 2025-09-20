package com.czy.domain.fragmentActivityAo.message

import androidx.lifecycle.MutableLiveData

open class MessageMainFAo {

    open val currentPositionLd : MutableLiveData<Int> = MutableLiveData(0)

}