package com.czy.netty;

import com.czy.springUtils.start.PortApplicationContextInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author 13225
 * @date 2025/1/10 18:25
 * 因为我需要启动netty跟前端进行长连接，
 * 但是长连接的netty本身是会占用端口号的，
 * 比如说我的服务是20000，
 * 但是netty的长连接还要启动占用端口号60000；
 * 这个时候如果我给每个微服务都单独创建netty长连接的话就会造成：
 * 前端链接的长连接管理混乱，不知道具体连接哪个微服务，
 * 按理来说这对前端应该是无感知的，而不是要去判断后端的微服务有哪些。
 * 第二是资源占用，每个需要跟前端交互的微服务都启动netty长连接会启用大量进程，
 * 因为netty长连接是一个单独的进程，
 * 所以我单独创建netty-socket微服务，
 * 它专门跟前端交互，只有它持有netty，它收到消息就交给RabbitMq，
 * 然后其他微服务通过RabbitMq和它交互.
 */
@SpringBootApplication(
        // 扫描指定包下的类
        scanBasePackages = {"com.czy.netty",
                "com.czy.springUtils", "com.czy.api"},
        // 排除
        exclude = {}
)
public class NettySocketApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(NettySocketApplication.class)
                .initializers(new PortApplicationContextInitializer())
                .run(args);
    }
}
