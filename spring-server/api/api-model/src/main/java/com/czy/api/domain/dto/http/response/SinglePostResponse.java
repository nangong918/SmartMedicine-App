package com.czy.api.domain.dto.http.response;

import com.czy.api.domain.ao.post.CommentAo;
import com.czy.api.domain.vo.post.toFront.PostFVo;
import lombok.Data;

import java.util.List;

@Data
public class SinglePostResponse {
    public PostFVo postVo;
    public List<CommentAo> commentAos;
}
