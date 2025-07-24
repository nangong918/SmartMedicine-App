package com.czy.dal.dto.http.response;


import com.czy.dal.entity.UserViewEntity;
import com.czy.dal.vo.entity.home.PostVo;

import java.util.List;


/**
 * @author 13225
 * @date 2025/7/24 10:11
 */
public class UserBriefResponse {
    // 用户视图
    public UserViewEntity userView;
    // 用户的备注 (暂时不实现)
    public String userRemark = null;
    // userPosts的视图
    public List<PostVo> userPosts;
}
