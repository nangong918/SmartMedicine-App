package com.czy.domain.ao.home;

import com.czy.baseUtilLib.algorithm.SortItem;
import com.czy.domain.constant.home.RecommendCardType;
import com.czy.domain.vo.entity.home.PostVo;

public class PostAo extends SortItem {

    // view
    // 单个post的信息
    public PostVo[] postVos;

    public int viewType = RecommendCardType.TWO_SMALL_CARD.value;

    public PostAo(){
        this.postVos = new PostVo[this.viewType];
    }

    public PostAo(int postType){
        this.viewType = postType;
        this.postVos = new PostVo[this.viewType];
    }
}
