package com.czy.domain.dto.http.response;


import com.czy.domain.ao.home.PostInfoUrlAo;

import java.util.List;

public class RecommendPostResponse {
    public List<PostInfoUrlAo> postInfoUrlAos;

    public List<PostInfoUrlAo> getPostInfoUrlAos() {
        return postInfoUrlAos;
    }

    public void setPostInfoUrlAos(List<PostInfoUrlAo> postInfoAos) {
        this.postInfoUrlAos = postInfoAos;
    }
}
