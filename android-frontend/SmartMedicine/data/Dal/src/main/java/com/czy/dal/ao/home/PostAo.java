package com.czy.dal.ao.home;

import com.czy.dal.vo.entity.home.PostVo;

public class PostAo {

    public final static int VIEW_TYPE_PLUS = 1, VIEW_TYPE_USER = 2;

    // view
    // 单个post的信息
    public PostVo[] postVos;

    // data
    private Long postId;
    private Long fileId;
    public int viewType = VIEW_TYPE_USER;

    private PostAo(){
        this.postVos = new PostVo[this.viewType];
    }

    private PostAo(int postType){
        this.viewType = postType;
        this.postVos = new PostVo[this.viewType];
    }
}
