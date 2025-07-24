package com.czy.api.domain.dto.http.response;

import com.czy.api.domain.entity.UserViewEntity;
import com.czy.api.domain.vo.post.PostPreviewVo;
import lombok.Data;

import java.util.List;

/**
 * @author 13225
 * @date 2025/7/24 10:11
 */
@Data
public class UserBriefResponse {
    // 用户视图
    public UserViewEntity userView;
    // 用户的备注 (暂时不实现)
    public String userRemark;
    // userPosts的视图
    public List<PostPreviewVo> userPosts;
}
