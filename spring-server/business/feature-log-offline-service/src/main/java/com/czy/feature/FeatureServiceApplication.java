package com.czy.feature;


import com.utils.common.debug.DebugConfig;
import com.utils.common.start.PortApplicationContextInitializer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author 13225
 * @date 2025/1/10 18:25
 */
// mybatis (注意mybatis一定要特别限定区间，因为这个傻逼会把其他的接口也视为它的接口，明明都没用@Mapper但是这个傻逼还是会绝对接口是它的)
@MapperScan({
        // this
        "com.czy.feature.mapper",
        // minio
        "com.utils.minio.mapper",
        // api.mapper
        "com.api.mapper.user.mybatis",
        "com.api.mapper.feature.mybatis",
})
@EnableConfigurationProperties(DebugConfig.class)
@SpringBootApplication(
        // 扫描指定包下的类
        scanBasePackages = {
                // 扫描api模块
                "com.czy.api",
                "com.api.mapper",
                // 扫描本模块
                "com.czy.feature",
//                // 扫描工具类 Webflux的异常处理
//                "com.utils.webflux.handler",
                "com.utils.redis",
                "com.utils.redisson",
        },
        // 排除
        exclude = {}
)
public class FeatureServiceApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(FeatureServiceApplication.class)
                .initializers(new PortApplicationContextInitializer())
                .run(args);
    }
}
