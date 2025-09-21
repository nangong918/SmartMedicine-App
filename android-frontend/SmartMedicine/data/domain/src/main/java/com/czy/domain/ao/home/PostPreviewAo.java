package com.czy.domain.ao.home;

import com.czy.baseutil.algorithm.SortItem;
import com.czy.domain.constant.home.RecommendCardType;
import com.czy.domain.vo.entity.home.PostPreviewVo;

public class PostPreviewAo extends SortItem {

    // view
    // 单个post的信息
    public PostPreviewVo[] postPreviewVos;

    public int viewType = RecommendCardType.TWO_SMALL_CARD.value;

    public PostPreviewAo(){
        this.postPreviewVos = new PostPreviewVo[this.viewType];
    }

    public PostPreviewAo(int postType){
        this.viewType = postType;
        this.postPreviewVos = new PostPreviewVo[this.viewType];
    }
}
