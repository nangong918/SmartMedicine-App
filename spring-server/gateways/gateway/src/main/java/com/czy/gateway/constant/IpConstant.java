package com.czy.gateway.constant;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author 13225
 * @date 2025/4/4 20:22
 */
@ConfigurationProperties(prefix = "gateway.ip")
@Component
public class IpConstant {
    // 白名单key
    public final static String WHITE_LIST_KEY = "FlowLimit:White:";
    // 黑名单key
    public final static String BLACK_LIST_KEY = "FlowLimit:Black:";
    // 白名单限制次数前缀
    public final static String WHITE_LIMIT_NUM = "FlowLimit:WhiteLimitNum:";
    // 黑名单限时间数前缀
    public final static String BLACK_LIMIT_Time = "FlowLimit:BlackLimitTime:";

    // 访问次数上限
    @Getter
    @Setter
    public static int MAX_ACCESS_ATTEMPTS = 15;
    // 黑名单过期时间（分钟）
    @Getter
    @Setter
    public static long BLACKLIST_EXPIRY = 60L * 30;
}
