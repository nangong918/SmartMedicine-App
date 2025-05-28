package com.czy.api.domain.dto.http.response;

import com.czy.api.domain.vo.CommentVo;
import com.czy.api.domain.vo.PostVo;
import lombok.Data;

import java.util.List;

@Data
public class SinglePostResponse {
    public PostVo postVo;
    public List<CommentVo> commentVos;
}
