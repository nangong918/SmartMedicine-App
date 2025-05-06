package com.czy.api.api.post;


import java.util.List;

/**
 * @author 13225
 * @date 2025/5/6 11:45
 */
public interface PostSearchService {

    // 0~1级搜索：完全匹配~mysql like匹配（放一起是因为like能一起做了）
    List<Long> searchPostIdsByLikeTitle(String likeTitle);

}
