package com.czy.springUtils.component;


import com.czy.springUtils.annotation.HandlerType;
import com.czy.springUtils.annotation.MessageType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

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
//@Component
public abstract class BaseEventManager <T> {

    // 存储所有 Handler
    protected final Map<String, Object> handlers = new ConcurrentHashMap<>();
    // 存储消息类型与方法的映射
    protected final Map<String, Method> methodCache = new ConcurrentHashMap<>();

    @Getter
    protected List<String> messageHandlers = new ArrayList<>();

//    @Autowired
//    protected List<Object> handlerBeans;

//    @PostConstruct
//    public void init(List<Object> handlerBeans) {
//        for (Object handler : handlerBeans) {
//            HandlerType handlerType = handler.getClass().getAnnotation(HandlerType.class);
//            if (handlerType != null) {
//                messageHandlers.add(handlerType.value());
//                handlers.put(handlerType.value(), handler);
//                cacheAnnotatedMethods(handler.getClass());
//            }
//        }
//    }

    /**
     *  postConstruct
     * @param handlerBeans  待处理的handler
     */
    protected void initEventManager(List<Object> handlerBeans){
        for (Object handler : handlerBeans) {
            HandlerType handlerType = handler.getClass().getAnnotation(HandlerType.class);
            if (handlerType != null) {
                messageHandlers.add(handlerType.value());
                handlers.put(handlerType.value(), handler);
                cacheAnnotatedMethods(handler.getClass());
                log.info("初始化Message处理类: [handlerType: {}, handlerClass: {}]", handlerType.value(), handler.getClass());
            }
        }
    }

//    protected BaseEventManager(List<Object> handlerBeans){
//        for (Object handler : handlerBeans) {
//            HandlerType handlerType = handler.getClass().getAnnotation(HandlerType.class);
//            if (handlerType != null) {
//                messageHandlers.add(handlerType.value());
//                handlers.put(handlerType.value(), handler);
//                cacheAnnotatedMethods(handler.getClass());
//            }
//        }
//    }

    /**
     * 缓存标注了 @MessageType 的方法
     */
    protected void cacheAnnotatedMethods(Class<?> clazz) {
        // 处理接口的方法
        for (Class<?> iface : clazz.getInterfaces()) {
            for (Method method : iface.getDeclaredMethods()) {
                MessageType annotation = method.getAnnotation(MessageType.class);
                if (annotation != null) {
                    methodCache.put(annotation.value(), method);
                    log.info("缓存标注了 @MessageType 的方法: [messageType: {}, method: {}, desc: {}]", annotation.value(), method, annotation.desc());
                }
            }
        }
    }

    public abstract void process(T msg);

    /**
     * 根据消息类型找到对应的 Handler
     * 反射的invoke方法需要handler和request，也就是接口和入参
     */
    protected Object findHandlerByMessageType(String messageType) {
        for (Map.Entry<String, Object> entry : handlers.entrySet()) {
            if (messageType.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }
}
