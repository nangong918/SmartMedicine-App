package com.czy.domain.fragmentActivityAo.medicine

import androidx.lifecycle.MutableLiveData

open class MedicineFAo {

    open val currentPosition : MutableLiveData<Int> = MutableLiveData(0)

}