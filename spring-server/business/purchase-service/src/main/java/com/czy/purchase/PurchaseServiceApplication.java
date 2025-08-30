package com.czy.purchase;


import com.utils.common.start.PortApplicationContextInitializer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;


// mybatis (注意mybatis一定要特别限定区间，因为这个傻逼会把其他的接口也视为它的接口，明明都没用@Mapper但是这个傻逼还是会绝对接口是它的)
@MapperScan({
        // this
        "com.czy.purchase.mapper",
        // minio
        "com.utils.minio.mapper",
        // api.mapper
        "com.api.mapper.user.mybatis",
        "com.api.mapper.purchase.mybatis",
})
@SpringBootApplication(scanBasePackages = {
        // 扫描api模块
        "com.czy.api",
        // 扫描本模块
        "com.czy.purchase",
        // 扫描工具类
        "com.czy.spring",
        "com.utils.common",
        "com.utils.redisson",
        "com.utils.minio",
        "com.utils.redis",
        "com.utils.rabbitmq",
}) // 扫描bean
@EnableElasticsearchRepositories(basePackages = {
        "com.api.mapper.purchase.es"
})
@EnableRabbit
public class PurchaseServiceApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(PurchaseServiceApplication.class)
                .initializers(new PortApplicationContextInitializer())
                .run(args);
    }
}
