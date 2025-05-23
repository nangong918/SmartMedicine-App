package com.czy.appcore.network.netty.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author 13225
 * @date 2025/2/14 11:07
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface MsgTranslator {
    /**
     * 请求类型 -> 响应类型：
     * <p>
     * Connect/ToServer：sender -> sender
     * <p>
     * 其他的：sender -> receiver
     * @return 请求类型
     */
    String responseType();
}
