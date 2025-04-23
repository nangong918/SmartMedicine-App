package com.czy.message.component;


import com.czy.api.converter.base.BaseRequestConverter;
import com.czy.api.domain.dto.base.BaseRequestData;
import com.czy.api.domain.entity.event.Message;
import com.czy.message.annotation.HandlerType;
import com.czy.message.annotation.MessageType;
import com.czy.springUtils.debug.DebugConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 13225
 * @date 2025/2/12 16:37
 * Message服务的EventMananger
 */

@Slf4j
@Component
public class MessageEventManager {

    @Autowired
    private DebugConfig debugConfig;

    @Autowired
    private BaseRequestConverter baseRequestConverter;


    // 存储所有 Handler
    private final Map<String, Object> handlers = new ConcurrentHashMap<>();
    // 存储消息类型与方法的映射
    private final Map<String, Method> methodCache = new ConcurrentHashMap<>();

    @Getter
    private List<String> messageHandlers = new ArrayList<>();
//    @Lazy 用lazy/PostConstruct避免循环依赖
//    @Lazy
    @Autowired
    private List<Object> handlerBeans;

    @PostConstruct
    public void init() {
        for (Object handler : handlerBeans) {
            HandlerType handlerType = handler.getClass().getAnnotation(HandlerType.class);
            if (handlerType != null) {
                messageHandlers.add(handlerType.value());
                handlers.put(handlerType.value(), handler);
                cacheAnnotatedMethods(handler.getClass());
            }
        }
    }

    /**
     * 缓存标注了 @MessageType 的方法
     */
    private void cacheAnnotatedMethods(Class<?> clazz) {
        // 处理接口的方法
        for (Class<?> iface : clazz.getInterfaces()) {
            for (Method method : iface.getDeclaredMethods()) {
                MessageType annotation = method.getAnnotation(MessageType.class);
                if (annotation != null) {
                    methodCache.put(annotation.value(), method);
                }
            }
        }
    }

    // 此处需要优化性能，频繁调用的方法禁止使用反射。可以先将反射内容存储起来
    // 我已经用BaseRequestData.class.isAssignableFrom(parameterType)检查了
    @SuppressWarnings("unchecked")
    public void process(Message msg) {
        if (msg == null){
            return;
        }
        String msgType = msg.getType();
        if (debugConfig.isAllLog()) {
            String msgStr = msg.toJsonString();
            log.info("收到消息：[message: {}][messageType: {}]", msgStr, msgType);
        }

        // 反射匹配
        Method targetMethod = methodCache.get(msgType);
        if (targetMethod == null) {
            log.warn("未找到匹配的消息处理方法: [messageType: {}]", msgType);
            return;
        }
        // 找到对应的 Handler
        Object handler = findHandlerByMessageType(msgType);
        if (handler == null){
            log.warn("未找到对应的 Handler: [messageType: {}]", msgType);
            return;
        }
        try {
            // 获取方法的第二个参数类型（即请求参数类型）
            // 获取目标方法的参数类型
            Class<?> parameterType = targetMethod.getParameterTypes()[1];
            // 检查 parameterType 是否是 BaseRequestData 或其子类
            if (BaseRequestData.class.isAssignableFrom(parameterType)) {
                // 进行安全转换
                Class<? extends BaseRequestData> safeParameterType = (Class<? extends BaseRequestData>) parameterType;

                // 现在可以使用 safeParameterType 来调用 getBaseTransferData
                Object request = baseRequestConverter.getBaseRequestData(msg, safeParameterType);

                if (request == null) {
                    log.warn("请求参数解析失败: [messageType: {}]", msgType);
                    return;
                }

                // 调用目标方法
                targetMethod.invoke(handler, request);
            } else {
                // 处理不符合类型的情况
                log.warn("参数类型不匹配: {}", parameterType.getName());
            }
        } catch (Exception e) {
            log.error("调用方法失败: [messageType: {}]", msgType, e);
        }
    }

    /**
     * 根据消息类型找到对应的 Handler
     */
    private Object findHandlerByMessageType(String messageType) {
        for (Map.Entry<String, Object> entry : handlers.entrySet()) {
            if (messageType.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }
}
/**
 * 将消息转为需要的类型
 */
