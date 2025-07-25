package com.czy.springUtils.debug;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author 13225
 * @date 2025/1/4 16:40
 */
@Setter
@Getter
@Slf4j
@Component
@ConfigurationProperties(prefix = "debug")
public class DebugConfig {
    private boolean debug = false;
    private boolean allLog = false;
    private boolean vcodeCheck = true;
    private boolean accessTokenCheck = true;
    private boolean recordUserAccount = true;

    @Override
    public String toString() {
        return "DebugConfig{" +
                "debug=" + debug +
                ", allLog=" + allLog +
                ", vcodeCheck=" + vcodeCheck +
                ", accessTokenCheck=" + accessTokenCheck +
                ", recordUserAccount=" + recordUserAccount +
                '}';
    }
}
