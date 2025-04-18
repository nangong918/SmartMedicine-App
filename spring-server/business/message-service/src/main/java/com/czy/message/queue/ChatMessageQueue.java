package com.czy.message.queue;


import com.czy.api.api.message.ChatService;
import com.czy.api.domain.Do.message.UserChatMessageDo;
import com.czy.springUtils.debug.DebugConfig;
import com.czy.springUtils.service.ScheduledTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author 13225
 * @date 2025/2/26 21:38
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ChatMessageQueue {

    // 使用线程安全的 List; 服务挂掉了，此消息队列也挂掉了。所以可能需要RabbitMq
    private final List<UserChatMessageDo> messageQueue = Collections.synchronizedList(new LinkedList<>());
    private final ScheduledTaskService scheduledTaskService;
    private final DebugConfig debugConfig;
    private final ChatService chatService;

    @PostConstruct
    public void init() {
        // 默认15s
        long period = 15L;
        TimeUnit timeUnit = TimeUnit.SECONDS;
        if (debugConfig.isDebug()) {
            // debug 模式下，5秒存储
            period = 5L;
        }
        // 注册一个定时任务，每隔两小时读取消息并存储到数据库
        scheduledTaskService.submitRepeatingTask(
                this::storeAllMessagesToDatabase,
                0,
                period,
                timeUnit
        );
        log.info("debug模式?:[{}], 消息队列持久化到MySQL周期：[{}]", debugConfig.isDebug(), period + " " + timeUnit);
    }

    // 添加消息到队列：没必要使用 RabbitMQ + 定时任务；因为内存就可以直接实现；要重构生产者消费者结构以后再说
    public synchronized void addMessage(UserChatMessageDo message) {
        log.info("消息队列添加消息::消息内容: [{}]", message);
        messageQueue.add(message);
    }

    // 获取一条消息
    public synchronized UserChatMessageDo getMessage() {
        if (!messageQueue.isEmpty()) {
            // 移除并返回第一个消息
            return messageQueue.remove(0);
        }
        // 如果队列为空，返回 null
        return null;
    }

    // 获取全部消息
    public synchronized List<UserChatMessageDo> getAllMessages() {
        // 返回一个副本以避免外部修改
        return new LinkedList<>(messageQueue);
    }

    // 清空消息队列
    public synchronized void clearMessages() {
        messageQueue.clear();
    }

    // 获取全部消息并清空消息队列
    public synchronized List<UserChatMessageDo> getAllMessagesAndClear() {
        List<UserChatMessageDo> messages = new ArrayList<>(messageQueue);
        messageQueue.clear();
        return messages;
    }



    // 存储消息到数据库的方法 TODO 内部事务失效的时候，外部的list消息是否找回？
    private synchronized void storeAllMessagesToDatabase() {
        List<UserChatMessageDo> messages = getAllMessagesAndClear();
        if (!messages.isEmpty()) {
            // 取消之前存入mysql的逻辑
//            userChatMessageMapper.batchInsert(messages);
            chatService.saveUserChatMessagesToDatabase(messages);
            log.info("消息队列数据存储到数据库::存储条数: [{}]", messages.size());
        }
    }
}
