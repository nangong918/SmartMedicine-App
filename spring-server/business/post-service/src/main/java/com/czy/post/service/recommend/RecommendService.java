package com.czy.post.service.recommend;

import lombok.NonNull;

import java.util.List;

/**
 * @author 13225
 * @date 2025/5/16 15:59
 * RecommendService：传入上下文，userId，postId
 * 上下文
 */
public interface RecommendService {

    /**
     * 获取推荐帖子
     * @param userId    用户ID
     * @return          推荐帖子id
     */
    List<Long> getRecommendPosts(@NonNull Long userId);

    /**
     * 获取热门帖子
     * @return  热门帖子id
     */
    List<Long> getHeatPosts();

    /**
     * 获取随机帖子 (过滤用户看过的) 不能保证list的数量和随机性
     * @param userId    用户ID
     * @return  随机帖子id
     */
    List<Long> getRandomPosts(Long userId, int randomNum);

    /**
     * 获取随机帖子 (不过滤)
     * @return  随机帖子id
     */
    List<Long> getRandomPosts(int randomNum);
}
