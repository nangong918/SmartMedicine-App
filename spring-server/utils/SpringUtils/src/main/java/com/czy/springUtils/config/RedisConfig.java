package com.czy.springUtils.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 不进行序列化配置是默认的二进制存储，很难排查问题；
 * 配置之后使用的是JSON存储，便于排查问题。
 * 通过配置能直接存储Java对象
 * @author 13225
 * @date 2024/12/26 17:00
 */
@Configuration
public class RedisConfig {

    // TODO 1.思考二进制是否需要 redisTemplateByte 2.尝试使用redisTemplate
    @Bean(name = "redisTemplate")// 标记一个方法为 Bean 的定义，Spring 会将该方法返回的对象作为一个 Bean 注册到应用上下文中，命名为 redisTemplate
    public RedisTemplate<String, Object> getRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // 设置 RedisTemplate 的连接工厂，传入的 factory 参数将提供与 Redis 的连接。
        redisTemplate.setConnectionFactory(factory);

        // 创建一个 StringRedisSerializer 实例，用于将 Redis 中的键序列化为字符串。
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        // 设置 RedisTemplate 的键序列化方式为 StringRedisSerializer，确保键以字符串形式存储。
        redisTemplate.setKeySerializer(stringRedisSerializer); // key的序列化类型

        // 创建一个 Jackson2JsonRedisSerializer 实例，用于将对象序列化为 JSON 格式。
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);// 2024.12.26 手动添加泛型为<Object>
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        // 使用新方法启用默认类型处理，允许在序列化和反序列化时包含类型信息，以支持多态。
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance ,
                ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        // 将配置好的 ObjectMapper 设置到 Jackson2JsonRedisSerializer 中，以便进行 JSON 序列化和反序列化。
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        // 设置 RedisTemplate 的值序列化方式为 Jackson2JsonRedisSerializer，确保值以 JSON 格式存储。
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer); // value的序列化类型
        // 设置 RedisTemplate 的哈希Key序列化方式为 StringRedisSerializer。
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        // 设置 RedisTemplate 的哈希Value序列化方式为 StringRedisSerializer。
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);

        // 调用 afterPropertiesSet 方法，以便完成 RedisTemplate 的初始化和配置。
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
