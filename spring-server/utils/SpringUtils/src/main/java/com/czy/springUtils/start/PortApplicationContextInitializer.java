package com.czy.springUtils.start;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Collections;

/**
 * @author 13225
 * @date 2025/4/9 14:29
 */
@Slf4j
@Component
public class PortApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment env = applicationContext.getEnvironment();

        // 直接从环境获取配置（此时配置已加载但容器未初始化）
        int minPort = env.getProperty("port.min", Integer.class, 1000);
        int maxPort = env.getProperty("port.max", Integer.class, 8070);

        int availablePort = findAvailablePort(minPort, maxPort);
        env.getPropertySources().addFirst(
                new MapPropertySource("custom-port",
                        Collections.singletonMap("server.port", availablePort))
        );
    }

    private int findAvailablePort(int minPort, int maxPort) {
        for (int port = minPort; port <= maxPort; port++) {
            try (ServerSocket socket = new ServerSocket(port)) {
                socket.setReuseAddress(true);
                log.info("Port {} is available", port);
                return port;
            } catch (IOException ignored) {
            }
        }
        throw new IllegalStateException("No available port in range " + minPort + "-" + maxPort);
    }
}
