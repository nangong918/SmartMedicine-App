package com.czy.post;


import com.utils.common.debug.DebugConfig;
import com.utils.common.start.PortApplicationContextInitializer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author 13225
 * @date 2025/1/10 18:25 todo 合并post-search-recommend
 */
// mybatis-plus
@MapperScan({"com.czy.post.mapper", "com.utils.minio.mapper", "com.api.mapper"})
// mongodb
//@EnableMongoRepositories(basePackages = "com.api.mapper")
@EnableAspectJAutoProxy // 启用aop
@EnableConfigurationProperties(DebugConfig.class)
@SpringBootApplication(
        // 扫描指定包下的类
        scanBasePackages = {
                // 扫描api模块
                "com.czy.api",
                "com.api.mapper",
                // 扫描本模块
                "com.czy.post",
//                // 扫描工具类 Webflux的异常处理
//                "com.utils.webflux.handler",
                // 扫描工具类springMvcUtils
                "com.utils.common",
                "com.utils.redisson",
                "com.utils.minio",
                "com.utils.redis",
                "com.utils.rabbitmq",
        },
        // 排除
        exclude = {}
)
public class PostServiceApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(PostServiceApplication.class)
                .initializers(new PortApplicationContextInitializer())
                .run(args);
    }
}
