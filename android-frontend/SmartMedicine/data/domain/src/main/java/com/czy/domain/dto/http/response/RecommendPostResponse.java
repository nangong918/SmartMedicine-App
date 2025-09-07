package com.czy.domain.dto.http.response;


import com.czy.domain.vo.entity.home.PostVo;

import java.util.List;

public class RecommendPostResponse {
    public List<PostVo> postVos;

    public List<PostVo> getPostVos() {
        return postVos;
    }

    public void setPostVos(List<PostVo> postVos) {
        this.postVos = postVos;
    }
}
