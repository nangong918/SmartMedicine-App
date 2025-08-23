package com.czy.domain.dto.http.response;


import com.czy.domain.vo.entity.home.CommentVo;
import com.czy.domain.vo.entity.home.PostVo;

import java.util.List;

public class SinglePostResponse {
    public PostVo postVo;
    public List<CommentVo> commentVos;
}
