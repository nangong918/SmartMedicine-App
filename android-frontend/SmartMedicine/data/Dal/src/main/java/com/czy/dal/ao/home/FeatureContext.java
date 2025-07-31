package com.czy.dal.ao.home;


import java.util.ArrayList;
import java.util.List;

/**
 * @author 13225
 * @date 2025/5/16 17:01
 */
public class FeatureContext {
    public Long userId = null;
    // 当前环境感兴趣的帖子
    public List<Long> postIds;
    // timestamp
    public Long timestamp = System.currentTimeMillis();

    public void clear() {
        postIds.clear();
        timestamp = System.currentTimeMillis();
    }

    public FeatureContext copy() {
        FeatureContext featureContext = new FeatureContext();
        // 拷贝内容而不是地址
        featureContext.postIds = new ArrayList<>(postIds);
        featureContext.timestamp = timestamp;
        return featureContext;
    }
}
