package com.czy.springUtils.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.util.List;

public final class JSONByteUtils {
    /**
     * 创建一个用于JSON处理的ObjectMapper实例
     * 该实例配置了特定的读取特征和反序列化行为，以适应特定的JSON处理需求
     */
    private static final JsonMapper OBJECT_MAPPER = JsonMapper.builder()
            // 允许JSON字符串中存在未转义的控制字符
            .enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS)
            // 允许任何字符使用反斜杠进行转义
            .enable(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER)
            // 忽略JSON对象中不存在的属性，防止反序列化失败
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            // 仅序列化非空对象，提高生成的JSON字符串的效率和可读性
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .build();

    /**
     * 将给定的对象转换为JSON字符串
     * 此方法使用全局的ObjectMapper实例将Java对象序列化为JSON格式的字符串
     * 如果序列化过程中遇到无法处理的属性或结构，将抛出IllegalArgumentException
     *
     * @param data 要转换为JSON字符串的Java对象
     * @return Java对象对应的JSON字符串
     * @throws IllegalArgumentException 如果序列化过程中发生错误，例如对象结构不支持序列化
     */
    public static String toJSONString(Object data) {

        try {
            // 使用ObjectMapper将对象转换为JSON字符串
            return OBJECT_MAPPER.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            // 如果转换过程中发生错误，则抛出IllegalArgumentException
            throw new IllegalArgumentException(e);
        }

    }

    /**
     * 将给定的JSON字符串转换为指定类型的对象
     *
     * @param str JSON字符串，表示要转换的数据
     * @param clazz 目标类型，表示要转换成的对象类型
     * @param <T> 泛型参数，表示可以转换成的任意类型
     * @return 转换后的对象，类型为T
     *
     * 此方法使用了Jackson库中的ObjectMapper来解析JSON字符串
     * 如果字符串无法解析或转换过程中出现错误，将抛出IllegalArgumentException
     */
    public static <T> T fromJson(String str, Class<T> clazz) {
        try {
            // 使用ObjectMapper的readValue方法将JSON字符串转换为指定类型T的对象
            return OBJECT_MAPPER.readValue(str, clazz);
        } catch (IOException e) {
            // 如果转换过程中出现IOException，将其转换为IllegalArgumentException并抛出
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 将JSON格式的数据转换为指定类型的对象
     *
     * @param data 包含JSON格式数据的字节数组
     * @param clazz 目标对象类型，用于指定转换后的对象类型
     * @param <T> 泛型参数，表示可以转换为任何指定的类型
     * @return 转换后的对象，类型为指定的T类型
     * <p>
     * 此方法使用了Jackson库中的ObjectMapper来执行实际的转换操作
     * 如果转换过程中出现IOException，将抛出IllegalArgumentException异常
     */
    public static <T> T fromJson(byte[] data, Class<T> clazz) {
        try {
            // 使用ObjectMapper读取字节数组中的数据并将其转换为指定类型的对象
            return OBJECT_MAPPER.readValue(data, clazz);
        } catch (IOException e) {
            // 如果转换过程中出现IOException，将其包装在IllegalArgumentException中并抛出
            throw new IllegalArgumentException(e);
        }
    }


    /**
     * 解析JSON字符串为指定类型的列表
     * 该方法使用了Jackson库中的ObjectMapper来解析JSON字符串，并将结果封装为一个泛型列表
     *
     * @param str JSON字符串，表示一个数组
     * @param clazz 列表中元素的类型
     * @param <T> 泛型参数，表示列表中元素的类型
     * @return 解析后的列表，列表中的元素类型为T
     * @throws IllegalArgumentException 如果解析过程中发生IO错误，则抛出该异常
     */
    public static <T> List<T> parseList(String str, Class<T> clazz) {
        // 创建一个JavaType对象，用于表示一个集合类型，这里指定集合元素的类型为clazz
        JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructCollectionLikeType(List.class,clazz);
        try {
            // 使用ObjectMapper读取字符串内容，并解析为指定类型的列表
            return OBJECT_MAPPER.readValue(str, javaType);
        } catch (IOException e) {
            // 如果解析过程中发生IO错误，则抛出IllegalArgumentException
            throw new IllegalArgumentException(e);
        }
    }
}
