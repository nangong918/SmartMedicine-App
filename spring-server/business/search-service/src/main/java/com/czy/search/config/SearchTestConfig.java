package com.czy.search.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author 13225
 * @date 2025/6/7 10:47
 */
@Setter
@Getter
@Slf4j
@Component
@Configuration
public class SearchTestConfig {
    @Value("${search-debug.isDebug}")
    public boolean isDebug = false;

}
