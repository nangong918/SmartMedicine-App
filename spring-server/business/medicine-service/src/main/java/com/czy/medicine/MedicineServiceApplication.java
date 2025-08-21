package com.czy.medicine;


import com.utils.common.start.PortApplicationContextInitializer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;


// 扫描bean
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
})
// mybatis (注意mybatis一定要特别限定区间，因为这个傻逼会把其他的接口也视为它的接口，明明都没用@Mapper但是这个傻逼还是会绝对接口是它的)
@MapperScan({
        // this
        "com.czy.medicine.mapper",
        // minio
        "com.utils.minio.mapper",
        // api.mapper
        "com.api.mapper.user.mybatis",
        "com.api.mapper.medicine.mybatis",
})
// es
@EnableElasticsearchRepositories(basePackages = "com.api.mapper")
public class MedicineServiceApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(MedicineServiceApplication.class)
                .initializers(new PortApplicationContextInitializer())
                .run(args);
    }
}
