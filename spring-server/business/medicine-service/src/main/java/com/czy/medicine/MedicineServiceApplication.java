package com.czy.medicine;


import com.utils.common.start.PortApplicationContextInitializer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;


@MapperScan({"com.czy.medicine.mapper",
        "com.utils.minio.mapper",
        "com.api.mapper"})    // 扫描mapper
@SpringBootApplication(scanBasePackages = {
        // 扫描api模块
        "com.czy.api",
        "com.api.mapper",
        // 扫描本模块
        "com.czy.medicine",
        // 扫描工具类
        "com.czy.spring",
        "com.utils.common",
        "com.utils.redisson",
        "com.utils.minio",
        "com.utils.redis",
        "com.utils.rabbitmq",
}) // 扫描bean
@EnableElasticsearchRepositories(basePackages = "com.api.mapper")
public class MedicineServiceApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(MedicineServiceApplication.class)
                .initializers(new PortApplicationContextInitializer())
                .run(args);
    }
}
