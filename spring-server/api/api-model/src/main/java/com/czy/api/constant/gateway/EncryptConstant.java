package com.czy.api.constant.gateway;

/**
 * @author 13225
 * @date 2025/1/15 13:58
 */
public class EncryptConstant {
    // Key统一前缀
    // 存储Key的redisHash的前缀，所用的Key又不多，就用Hash，不用Set
    public static final String REDIS_KEY_KEY_PREFIX = "KeyGenerator:";
    public static final String REDIS_TOKEN_KEY_PREFIX = "TokenGenerator:";
    public static class Key {
        // RsaKey
        public static final String REDIS_RSA_KEY_PREFIX = "RsaKey:";
        // InternalKey
        public static final String REDIS_INTERNAL_KEY_PREFIX = "InternalKey:";
        // AccessTokenKey
        public static final String REDIS_ACCESS_TOKEN_KEY_PREFIX = "AccessTokenKey:";
        // RandomTokenKey
        public static final String REDIS_RANDOM_TOKEN_KEY_PREFIX = "RandomTokenKey:";

        // Key前缀列表
        public static String[] REDIS_HASH_KEYS_PREFIX = new String[]{
                REDIS_RSA_KEY_PREFIX,
                REDIS_INTERNAL_KEY_PREFIX,
                REDIS_ACCESS_TOKEN_KEY_PREFIX,
                REDIS_RANDOM_TOKEN_KEY_PREFIX
        };

        // 默认密钥长度
        public static final int defaultKeyLength = 64;
        // 默认Rsa密钥长度
        public static final int defaultRsaKeyLength = 2048;
        // 默认内部密钥长度
        public static final int defaultInternalKeyLength = 128;
    }

    public static class Token {
        public static final String REDIS_RANDOM_TOKEN_PREFIX = "Token:";
        public static final String REDIS_ACCESS_TOKEN_PREFIX = "AccessToken:";
        public static final String REDIS_REFRESH_TOKEN_PREFIX = "Refresh:";
        public static final int defaultJweKeyLength = 256;
        // 3小时更换一次AccessToken
        public static final long AccessToken_EXPIRATION_TIME = 3 * 60 * 60L;
        // 3倍时间更新所有Token的Key
        public static final long SECRET_KEY_EXPIRATION_TIME = 3 * AccessToken_EXPIRATION_TIME;
        // 7天更新一次RefreshToken
        public static final long RefreshToken_EXPIRATION_TIME = 60L * 60 * 24 * 7L;
    }
}
