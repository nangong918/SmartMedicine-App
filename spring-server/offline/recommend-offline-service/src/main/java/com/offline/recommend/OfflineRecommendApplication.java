package com.offline.recommend;

import com.czy.springUtils.start.PortApplicationContextInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author 13225
 * @date 2025/1/10 18:25
 */
@SpringBootApplication(
        scanBasePackages = {
                "com.czy.springUtils",
                // 扫描api模块
                "com.czy.api",
                // 扫描本模块
                "com.offline.recommend",
//                // 扫描工具类 Webflux的异常处理
//                "com.utils.webflux.handler",
                // 扫描工具类springMvcUtils
                "com.utils.mvc"
        }
)
public class OfflineRecommendApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(OfflineRecommendApplication.class)
                .initializers(new PortApplicationContextInitializer())
                .run(args);
    }
}

/**
 * 特征工程：
 *  1.离线特征工程：
 *  2.近线特征工程：
 *  3.在线特征工程：
 */
