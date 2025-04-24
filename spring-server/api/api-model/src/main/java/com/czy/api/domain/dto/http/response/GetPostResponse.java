package com.czy.api.domain.dto.http.response;


import com.czy.api.domain.Do.post.comment.PostCommentDo;
import com.czy.api.domain.ao.post.PostAo;
import lombok.Data;

import java.util.List;


/**
 * @author 13225
 * @date 2025/4/21 17:25
 */
@Data
public class GetPostResponse {
    public PostAo postAo;
    // 一般来说第一次默认是第1（0）页面 + 20条
    public List<PostCommentDo> postCommentList;
}
