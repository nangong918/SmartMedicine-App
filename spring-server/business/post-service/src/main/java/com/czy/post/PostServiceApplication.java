package com.czy.post;


import com.utils.common.debug.DebugConfig;
import com.utils.common.start.PortApplicationContextInitializer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * @author 13225
 * @date 2025/1/10 18:25
 */
// mybatis (注意mybatis一定要特别限定区间，因为这个傻逼会把其他的接口也视为它的接口，明明都没用@Mapper但是这个傻逼还是会绝对接口是它的)
@MapperScan({
        // this
        "com.czy.post.mapper",
        // minio
        "com.utils.minio.mapper",
        // api.mapper
        "com.api.mapper.user.mybatis",
        "com.api.mapper.post.mybatis",
})
// mongodb
@EnableMongoRepositories(basePackages = {
        "com.api.mapper.post.mongo"
})
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
                "com.czy.spring",
                "com.utils.common",
                "com.utils.redisson",
                "com.utils.minio",
                "com.utils.redis",
                "com.utils.rabbitmq",
        },
        // 排除
        exclude = {}
)
// es
@EnableElasticsearchRepositories(basePackages = {
        "com.api.mapper.post.es"
})
public class PostServiceApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(PostServiceApplication.class)
                .initializers(new PortApplicationContextInitializer())
                .run(args);
    }
}
