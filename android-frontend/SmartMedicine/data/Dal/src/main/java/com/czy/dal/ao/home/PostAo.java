package com.czy.dal.ao.home;

import com.czy.dal.constant.home.RecommendCardType;
import com.czy.dal.vo.entity.home.PostVo;

public class PostAo {

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
