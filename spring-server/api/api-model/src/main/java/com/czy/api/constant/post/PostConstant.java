package com.czy.api.constant.post;

/**
 * @author 13225
 * @date 2025/4/18 18:20
 */
public class PostConstant {
    // 缓存
    public static final String POST_PUBLISH_KEY = "post_publish_key:";
    // 默认你oss上传最大时间是1天，超过1天就删掉了
    public static final Long POST_PUBLISH_KEY_EXPIRE_TIME = 60 * 60 * 24L;
}
