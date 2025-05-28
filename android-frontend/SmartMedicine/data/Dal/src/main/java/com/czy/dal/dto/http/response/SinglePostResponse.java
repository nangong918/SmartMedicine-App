package com.czy.dal.dto.http.response;


import com.czy.dal.vo.entity.home.CommentVo;
import com.czy.dal.vo.entity.home.PostVo;

import java.util.List;

public class SinglePostResponse {
    public PostVo postVo;
    public List<CommentVo> commentVos;
}
