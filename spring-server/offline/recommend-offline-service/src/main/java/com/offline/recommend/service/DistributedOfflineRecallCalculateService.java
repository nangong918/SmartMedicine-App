package com.offline.recommend.service;

import java.util.Map;

/**
 * @author 13225
 * @date 2025/5/16 15:42
 */
public interface DistributedOfflineRecallCalculateService {

    /// python

    /// Java
    Map<Long, Double> getOfflineRecommend(Long userId);

    void allHeatUserOfflineRecommend();
}

/**
 * 离线分布式计算召回信息
 *  Python 的召回
 *      协同召回：
 *          user-cf
 *          item-cf
 *          swing
 *          mf
 *      向量召回：
 *          fm
 *          deepFM
 *          word2vec
 *          item2vec
 *          tf-idf
 *          bert
 *          neo4j图嵌入
 *  Java 的召回
 *      图召回：
 *          共同邻居
 *          图路径
 *
 */
