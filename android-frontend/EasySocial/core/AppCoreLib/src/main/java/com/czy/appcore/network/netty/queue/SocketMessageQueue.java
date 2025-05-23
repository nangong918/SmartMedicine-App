package com.czy.appcore.network.netty.queue;

import android.util.Log;

import com.czy.appcore.BaseConfig;
import com.czy.dal.netty.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Socket消息队列
 * 发送：送将消息存入消息队列
 * 接收：将接收的消息存入消息队列
 *      Activity从消息队列中取出属于自己订阅的消息
 * 持久化：消息队列中超过20条消息/30秒就持久化一次
 * <p>
 * 数据结构：
 *      1.存储Activity能获取订阅消息：ConcurrentHashMap<MessageType,List<Message>>
 *      2.存储能存如SQLite的消息：将ConcurrentHashMap转为MessageDo；存入SQLite
 */
public class SocketMessageQueue {

    private static final String TAG = SocketMessageQueue.class.getSimpleName();


    // ConcurrentHashMap<MessageType,List<Message>>
    private final Map<String, List<Message>> currentMap = new ConcurrentHashMap<>();
    // 定时器用于定期持久化
    private final Timer timer = new Timer();

    public SocketMessageQueue(){
        // 启动定时持久化任务
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (getMessageCount() > 0){
                    persistMessages();
                }
            }
        }, BaseConfig.SOCKET_QUEUE_PERSISTENCE_INTERVAL, BaseConfig.SOCKET_QUEUE_PERSISTENCE_INTERVAL);
    }

    /**
     * 发送消息，将消息存入消息队列
     * @param message 要发送的消息
     * @param messageType 消息类型
     */
    public void sendMessage(Message message, String messageType) {
        currentMap.computeIfAbsent(messageType, k -> new ArrayList<>()).add(message);
        // 存储到消息缓存
        chatListMessagesMap.put(message.receiverId, message);
        checkAndPersist(messageType);
    }

    /**
     * 接收消息，将接收的消息存入消息队列
     * @param message 接收到的消息
     * @param messageType 消息类型
     */
    public void receiveMessage(Message message, String messageType) {
        currentMap.computeIfAbsent(messageType, k -> new ArrayList<>()).add(message);
        // 存储到消息缓存
        chatListMessagesMap.put(message.type, message);
        Log.d(TAG, "receiveMessage::message.type: " + message.type);
        checkAndPersist(messageType);
    }

    /**
     * 从消息队列中取出属于指定用户的消息
     * @param userId 用户 ID
     * @param messageType 消息类型
     * @return 属于用户的消息列表
     */
    public List<Message> getMessagesForUser(String userId, String messageType) {
        List<Message> userMessages = new ArrayList<>();
        List<Message> messages = currentMap.get(messageType);
        if (messages != null) {
            for (Message message : messages) {
                if (message.receiverId.equals(userId)) {
                    userMessages.add(message);
                }
            }
        }
        return userMessages;
    }

    /**
     * 检查队列大小并决定是否进行持久化
     */
    private void checkAndPersist(String messageType) {
        if (getMessageCount() >= BaseConfig.SOCKET_QUEUE_MAX_QUEUE_SIZE) {
            persistMessages();
        }
    }

    /**
     * 持久化消息队列中的消息
     */
    private synchronized void persistMessages() {
        for (Map.Entry<String, List<Message>> entry : currentMap.entrySet()) {
            List<Message> messagesToPersist = new ArrayList<>(entry.getValue());
            // 清空当前消息列表
            entry.getValue().clear();
            // 持久化到数据库
            saveToDatabase(messagesToPersist);
        }
    }

    /**
     * 获取消息数量
     * @return  消息数量
     */
    public synchronized int getMessageCount() {
        int count = 0;
        for (List<Message> messages : currentMap.values()) {
            count += messages.size();
        }
        return count;
    }

    /**
     * 模拟持久化到数据库的方法
     * @param messages 要持久化的消息列表
     */
    private void saveToDatabase(List<Message> messages) {
        // TODO: 实现数据库操作，将 Message 转换为 MessageDo 存入 SQLite
    }

    // 关闭定时器
    public void stop() {
        timer.cancel();
    }

    // ChatActivity的消息；MessageListActivity的消息
    // ConcurrentHashMap<userId,Message> 专门存储ChatList界面的消息
    private final Map<String, Message> chatListMessagesMap = new ConcurrentHashMap<>();

    // 发送的时候存储，消息接收的时候存储，然后ChatListActivity启动的时候调用获取消息
    public synchronized Map<String, Message> getChatListMessagesMap() {
        return chatListMessagesMap;
    }
}
