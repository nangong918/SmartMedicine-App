package com.czy.api.domain.dto.http.response;

import com.czy.api.domain.vo.post.CommentOldVo;
import com.czy.api.domain.vo.post.PostOldVo;
import lombok.Data;

import java.util.List;

@Data
public class SinglePostResponse {
    public PostOldVo postVo;
    public List<CommentOldVo> commentVos;
}
