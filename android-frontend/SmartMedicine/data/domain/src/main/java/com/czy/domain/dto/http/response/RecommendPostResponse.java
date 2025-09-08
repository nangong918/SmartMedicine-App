package com.czy.domain.dto.http.response;


import com.czy.domain.vo.entity.home.PostPreviewVo;

import java.util.List;

public class RecommendPostResponse {
    public List<PostPreviewVo> postPreviewVos;

    public List<PostPreviewVo> getPostVos() {
        return postPreviewVos;
    }

    public void setPostVos(List<PostPreviewVo> postVos) {
        this.postPreviewVos = postVos;
    }
}
