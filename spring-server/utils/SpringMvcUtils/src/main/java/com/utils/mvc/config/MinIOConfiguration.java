package com.utils.mvc.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;

@Configuration
public class MinIOConfiguration {

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Value("${minio.gateway-port}")
    private String gatewayPort;

    @Value("${minio.minio-url}")
    private String minioUrl;

    @Value("${minio.is-use-gateway}")
    private boolean isUseGateway;

    @Bean
    public MinioClient minioClient() {
        // 创建 MinioClient 客户端
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    @Bean
    public String getGatewayAgentUrl() throws Exception {
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
