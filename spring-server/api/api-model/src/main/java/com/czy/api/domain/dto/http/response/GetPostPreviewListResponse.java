package com.czy.api.domain.dto.http.response;


import com.czy.api.domain.vo.post.PostPreviewVo;
import lombok.Data;

import java.util.List;

/**
 * @author 13225
 * @date 2025/4/21 17:25
 */
@Data
public class GetPostPreviewListResponse {
    public List<PostPreviewVo> postPreviewVos;
}
