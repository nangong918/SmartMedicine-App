package com.czy.api.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author 13225
 * @date 2025/3/1 11:44
 */
@Slf4j
public class JsonUtil {

    public static  <T> List<T> getListEntity(String jsonString, Class<T> clazz) {
        if (!StringUtils.hasText(jsonString)) {
            return null;
        }
        try {
            // 使用 FastJSON 解析为指定类型的 List
            return JSON.parseArray(jsonString, clazz);
        } catch (Exception e) {
            // 处理解析异常
            log.error("Error parsing JSON string: {}", jsonString, e);
            return null;
        }
    }

}
