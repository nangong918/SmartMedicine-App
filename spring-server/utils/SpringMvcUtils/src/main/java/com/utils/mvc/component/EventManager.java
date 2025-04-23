package com.utils.mvc.component;


import com.czy.api.converter.base.BaseRequestConverter;
import com.czy.api.domain.dto.base.BaseRequestData;
import com.czy.api.domain.entity.event.Message;
import com.czy.springUtils.annotation.HandlerType;
import com.czy.springUtils.annotation.MessageType;
import com.czy.springUtils.component.BaseEventManager;
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
public abstract class EventManager<T> extends BaseEventManager<Message> {

    @Autowired
    private DebugConfig debugConfig;

    @Autowired
    private BaseRequestConverter baseRequestConverter;

    @PostConstruct
    public void init() {
        super.init();
    }

    // 此处需要优化性能，频繁调用的方法禁止使用反射。可以先将反射内容存储起来
    // 我已经用BaseRequestData.class.isAssignableFrom(parameterType)检查了
    // 因为我使用了网关，所以不能使用webmvc，所以要单独为webmvc和webflux拆分，然后又因为swagger是基于webmvc的，所以utils不能继承任何的api，但是springMvcUtils可以继承
    @SuppressWarnings("unchecked")
    @Override
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
                // 按理来说应该IM系统应该禁用动态反射的，但是代价是开发成本。这是一个折中的选择。
                // 就像ProtoBuf比JSON快但是难以映射为对象。
                // NoSQL比ORM数据库快但是也难以映射为对象
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

}
/**
 * 将消息转为需要的类型
 */
