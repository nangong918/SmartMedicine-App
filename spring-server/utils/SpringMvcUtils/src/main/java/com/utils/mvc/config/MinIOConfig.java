package com.utils.mvc.config;

import io.minio.MinioClient;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

/**
 *@author 13225
 *@date 2025/7/21 11:30
 */
@Setter
@Getter
@Slf4j
@Component
@ConfigurationProperties(prefix = "minio")
public class MinIOConfig {
    /**
     * minio的endpoint (网段)
     */
    private String endpoint;
    /**
     * minio的accessKey
     */
    private String accessKey;
    /**
     * minio的secretKey
     */
    private String secretKey;

    /**
     * 是否使用gateway代理；如果不适用则需要使用nginx反向代理。否则会出现前端无法访问后端网域而导致生成的url无法呗访问
     */
    private boolean isUseGateway;
    /**
     * 如果使用gateway代理，则gateway的地址需要配置;eg: 8888则默认就是 -> http://localhost:8888
     */
    private String gatewayPort;
    /**
     * 用于替换minio的endpoint的地址。eg：/127.0.0.1:9000 -> /oss-minio
     */
    private String minioUrl;

    @Bean
    public MinioClient minioClient() {
        // 创建 MinioClient 客户端
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    /**
     * 获取gateway代理的url
     * @return  gateway代理的url
     * @throws Exception 获取本机IP异常/配置异常
     */
    @Bean
    public String minioGatewayAgentUrl() throws Exception {
        // 获取本机IP
        InetAddress inetAddress = InetAddress.getLocalHost();
        String ip = inetAddress.getHostAddress();

        // http可能是http或者https
        return ip + ":" + gatewayPort + minioUrl;
    }

    @Bean
    public String globalOssBucket(){
        return "global-oss";
    }

//    @Bean
//    public MinioClient minioClient() {
//        // Minio 配置。实际项目中，定义到 application.yml 配置文件中
//        String endpoint = "http://127.0.0.1:9000";
//        String accessKey = "minioadmin";
//        String secretKey = "minioadmin";
//
//        // 创建 MinioClient 客户端
//        return MinioClient.builder()
//                .endpoint(endpoint)
//                .credentials(accessKey, secretKey)
//                .build();
//    }

}
