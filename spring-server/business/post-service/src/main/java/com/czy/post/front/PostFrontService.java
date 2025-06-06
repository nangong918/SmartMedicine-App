package com.czy.post.front;

import com.czy.api.domain.ao.post.PostInfoAo;
import com.czy.api.domain.vo.PostPreviewVo;

import java.util.List;

/**
 * @author 13225
 * @date 2025/6/6 16:38
 * 转换成前端需要的类型
 */
public interface PostFrontService {
    List<PostPreviewVo> toPostPreviewVoList(List<PostInfoAo> postAoList);
}
