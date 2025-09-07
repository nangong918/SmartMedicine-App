package com.czy.domain.fragmentActivityAo.message

import androidx.lifecycle.MutableLiveData

open class MessageMainFAo {

    open val currentPosition : MutableLiveData<Int> = MutableLiveData(0)

}