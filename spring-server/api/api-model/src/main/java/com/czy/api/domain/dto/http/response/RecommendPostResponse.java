package com.czy.api.domain.dto.http.response;

import com.czy.api.domain.vo.post.toFront.PostPreviewFVo;
import lombok.Data;

import java.util.List;

/**
 * @author 13225
 * @date 2025/5/24 11:35
 */
@Data
public class RecommendPostResponse {
    private List<PostPreviewFVo> postPreviewVos;
}
