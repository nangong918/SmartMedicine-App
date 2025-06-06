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
 * 多实例解决方案：
 * Nacos虽然可以直接注册Dubbo端口，但是无法注册Http端口。所以用此自动管理Http端口，避免增加消费者需要配置信的配置文件
 */
@Slf4j
@Component
public class PortApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String PORT_RANGE_KEY = "port.range";
    private static final String DEFAULT_PORT_RANGE = "1000-8070";

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment env = applicationContext.getEnvironment();
        // 解析端口范围配置（格式：min-max）
        String range = env.getProperty(PORT_RANGE_KEY, DEFAULT_PORT_RANGE);
        String[] parts = range.split("-");
        int minPort = Integer.parseInt(parts[0]);
        int maxPort = Integer.parseInt(parts[1]);

        int availablePort = findAvailablePort(minPort, maxPort);
        env.getPropertySources().addFirst(
                new MapPropertySource("custom-port",
                        Collections.singletonMap("server.port", availablePort))
        );

        log.info("Auto-assigned server.port={} (range: {})", availablePort, range);
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
