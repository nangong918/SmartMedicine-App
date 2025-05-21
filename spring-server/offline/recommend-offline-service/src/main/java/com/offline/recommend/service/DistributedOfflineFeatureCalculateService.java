package com.offline.recommend.service;

/**
 * @author 13225
 * @date 2025/5/15 16:39
 * 分布式离线特征计算service
 * redisson存储user的特赠，并且存储最后更新时间。
 * 然后根据活跃用户顺序进行离线特征计算。
 * 需要对用户的user_id上分布式锁，实现分布式计算。
 */
public interface DistributedOfflineFeatureCalculateService {

    /**
     * 计算用户活跃度
     */
    void calculateUserHeat();

    /**
     * 计算帖子热度
     */
    void calculatePostHeats();

    /**
     * 计算用户特征
     */
    void calculateUserFeatures();
}
