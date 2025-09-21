package com.czy.domain.fragmentActivityAo.home

import androidx.lifecycle.MutableLiveData
import com.czy.domain.ao.home.PostPreviewAo

class RecommendFAo {

    var recommendPosts: List<PostPreviewAo> = ArrayList()

    val recommendPostCount: MutableLiveData<Int> = MutableLiveData(0)

}