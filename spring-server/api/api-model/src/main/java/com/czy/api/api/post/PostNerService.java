package com.czy.api.api.post;

import com.czy.api.domain.ao.post.PostNerResult;

import java.util.List;

/**
 * @author 13225
 * @date 2025/5/5 17:21
 */
public interface PostNerService {

    /**
     * 根据帖子标题获取AcTree实体识别结果
     * @param postTitle     帖子标题
     * @return              实体识别结果
     */
    List<PostNerResult> getPostNerResults(String postTitle);
}
