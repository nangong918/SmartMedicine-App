package com.czy.domain.fragmentActivityAo.medicine

import androidx.lifecycle.MutableLiveData
import com.czy.domain.vo.entity.chat.ChatMessageItemVo

class AiQuestionFAo {

    val isLoading = MutableLiveData(true)
    val inputText = MutableLiveData("")

    val chatList : MutableList<ChatMessageItemVo> = mutableListOf()
    val chatCountLd = MutableLiveData(0)
}