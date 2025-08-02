package com.czy.appcore.service.chat;


import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.czy.baseUtilsLib.algorithm.SortUtils;
import com.czy.dal.ao.chat.ChatContactItemAo;
import com.czy.dal.constant.NettyConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

/**
 * 用于管理chat消息管理者
 * 此管理者只创建在MainApplication，以单例形式存在。
 * 缓存内容: 1. messageFragment的联系人最近消息（RecentContactMessage）
 *          2. chatActivity的联系人全部消息（ChatMessage）
 * 数据来源: 1. http统一查询
 *          2. socket推送
 *          3. 本地sqlite存储
 */
public class ChatMessageManager {

    // 常量数据
    private static final String TAG = ChatMessageManager.class.getName();
    public static final long WAIT_TIME = 1000L;

    /// 缓存对象
    // RecentContactMessage: List<ChatContactItemAo>
    private final List<ChatContactItemAo> chatContactItemAoList = new ArrayList<>();
    // ChatMessage: Map<UserId, List<MessageItem>>
    private final Map<Long, List<MessageItem>> chatMessageMap = new HashMap<>();

    /// 工具
    // main线程消息队列（任务队列）
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    // 循环检查合并 线程
    private Thread messageProcessor;

    /// 消息队列
    // RecentContactMessage: Queue<List<ChatContactItemAo>>
    private final Queue<List<ChatContactItemAo>> chatContactItemAoMessageQueue = new LinkedList<>();
    // ChatMessage: Queue<List<MessageItem>>
    private final Queue<Map<Long, List<MessageItem>>> chatMessageMapQueue = new LinkedList<>();

    /// 消息变化回调
    private CurrentChatMessageContext currentChatMessageContext;
    private OnRecentContactMessageChange onRecentContactMessageChange;

    /**
     * 设置最近联系人消息监听回调
     * ChatMessageManager是单例，每次MessageFragment初始化的时候需要传递自己的UI监听回调给chatMessageManager
     * @param onRecentContactMessageChange  监听回调
     */
    public void setOnRecentContactMessageChange(@NonNull OnRecentContactMessageChange onRecentContactMessageChange){
        this.onRecentContactMessageChange = onRecentContactMessageChange;
    }

    /**
     * 设置聊天消息监听回调
     * ChatMessageManager是单例，每次ChatActivity初始化的时候需要传递自己的UI监听回调给chatMessageManager
     * @param currentChatMessageContext  ChatActivity的当前聊天消息上下文
     */
    public void setCurrentChatMessageContext(@NonNull CurrentChatMessageContext currentChatMessageContext){
        this.currentChatMessageContext = currentChatMessageContext;
    }

    /// 添加数据

    /**
     * ChatActivity立刻添加消息并回调自行更新UI
     * 此方法是提供给本地发送给对方的，由于不需要等待网络，所以立刻合并
     * @param item                   message item
     * @param contactId              对方id
     * @param onChatMessageChange    回调
     */
    public void immediatelyAddChatMessage(MessageItem item, Long contactId, OnChatMessageChange onChatMessageChange){
        // key不存在就创建
        if (!chatMessageMap.containsKey(contactId)){
            chatMessageMap.put(contactId, new ArrayList<>());
        }

        // 获取list
        List<MessageItem> list = Optional.ofNullable(chatMessageMap.get(contactId))
                .orElseGet(() -> {
                    List<MessageItem> l = new ArrayList<>();
                    // 添加到map中
                    chatMessageMap.put(contactId, l);
                    return l;
                });

        // 添加消息到list中
        list.add(item);

        // 用消息队列回调，避免线程并发冲突
        mainHandler.post(() -> {
            if (onChatMessageChange != null){
                onChatMessageChange.onChange(list);
            }
            else {
                Log.w(TAG, "onChatMessageChange is null");
            }
        });
    }

    /**
     * 缓存ChatMessage
     * @param list      缓存的ChatMessages
     * @param contactId 联系人Id
     */
    public synchronized void cacheMessage(List<MessageItem> list, Long contactId){
        // 校验
        if (list == null || list.isEmpty()) {
            return;
        }
        if (contactId == null) {
            Log.w(TAG, "cacheMessage contactId is null");
            return;
        }

        // 创建
        Map<Long, List<MessageItem>> messagesMap = new HashMap<>();
        messagesMap.put(contactId, list);

        // 添加缓存
        mainHandler.post(() -> {
            this.chatMessageMapQueue.add(messagesMap);
        });
    }

    /**
     * 立刻添加联系人的最新消息
     * @param itemAo                联系人最新消息
     * @param contactMessageChange  联系人消息改变监听器
     */
    public void immediatelyAddContactMessage(ChatContactItemAo itemAo, OnRecentContactMessageChange contactMessageChange){
        // 添加消息到list中
        chatContactItemAoList.add(itemAo);

        // 用消息队列回调，避免线程并发冲突
        mainHandler.post(() -> {
            if (contactMessageChange != null){
                contactMessageChange.onChange(chatContactItemAoList);
            }
            else {
                Log.w(TAG, "contactMessageChange is null");
            }
        });
    }

    /**
     * 缓存RecentMessage
     * @param list  RecentMessages
     */
    public synchronized void cacheMessage(List<ChatContactItemAo> list){
        // 校验
        if (list == null || list.isEmpty()){
            return;
        }

        // 添加缓存
        mainHandler.post(() -> {
            chatContactItemAoMessageQueue.add(list);
        });
    }

    // 线程控制标志
    private boolean running = false;

    // 启动消息处理线程
    private void startMessageProcessor() {
        messageProcessor = new Thread(() -> {
            while (running) {

                // 不断检查消息队列
                processRecentContactMessage();
                processChatMessage();

                // 如果没有消息，短暂休眠，避免忙等待
                if (!checkIsNoMessageNeedProcess()) {
                    try {
                        Thread.sleep(WAIT_TIME); // 可以调整休眠时间
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        });
        messageProcessor.start();
    }

    private void processRecentContactMessage(){
        while (!chatContactItemAoMessageQueue.isEmpty()){
            List<ChatContactItemAo> list = chatContactItemAoMessageQueue.poll();
            if (list != null){
                mergeRecentContactMessage(list);
            }
        }
    }

    private void processChatMessage(){
        while (!chatMessageMapQueue.isEmpty()) {
            Map<Long, List<MessageItem>> messagesMap = chatMessageMapQueue.poll();
            if (messagesMap != null) {
                mergeChatMessage(messagesMap);
            }
        }
    }

    /**
     * 合并ChatMessage消息
     * 二分算法
     * @param messagesMap  消息列表Map
     */
    public synchronized void mergeChatMessage(Map<Long, List<MessageItem>> messagesMap){
        if (messagesMap == null || messagesMap.isEmpty()){
            Log.i(TAG, "mergeChatMessage: list is empty");
            return;
        }

        Long currentContactId = Optional.ofNullable(this.currentChatMessageContext)
                .map(context -> context.contactId)
                .orElse(null);

        // key : value 拆解 Map
        for (Map.Entry<Long, List<MessageItem>> entry : messagesMap.entrySet()) {
            Long contactId = entry.getKey();
            List<MessageItem> list = entry.getValue();

            // 获取list
            List<MessageItem> chatMessageList = Optional.ofNullable(chatMessageMap.get(contactId))
                    .orElseGet(() -> {
                        List<MessageItem> l = new ArrayList<>();
                        // 添加到map中
                        chatMessageMap.put(this.currentChatMessageContext.contactId, l);
                        return l;
                    });

            // 调用二分查找算法
            for (MessageItem item : list) {
                // 使用二分查找找到插入位置
                int insertPosition = SortUtils.findInsertPosition(item.index, chatMessageList);

                // 在合适位置插入新消息
                chatMessageList.add(insertPosition, item);
            }

            // 检查ui是否需要更新
            if (currentContactId != null && currentContactId.equals(contactId)){
                // 消息队列避免并发
                mainHandler.post(() -> {
                    // 尝试回调更新
                    if (!currentChatMessageContext.isEmpty()){
                        try {
                            currentChatMessageContext.onChatMessageChange.onChange(chatMessageList);
                        } catch (Exception e){
                            Log.e(TAG, "调用更新chatMessage异常" + e);
                        }
                    }
                    else {
                        Log.e(TAG, "currentChatMessageContext.onChatMessageChange == null");
                    }
                });
            }
         }
    }

    /**
     * 合并RecentContactMessage消息
     * 二分算法
     * @param list   待合并的RecentContactMessage消息
     */
    public synchronized void mergeRecentContactMessage(List<ChatContactItemAo> list){
        if (list == null || list.isEmpty()){
            Log.i(TAG, "mergeChatMessage: list is empty");
            return;
        }

        // 调用二分查找算法
        for (ChatContactItemAo item : list) {
            // 使用二分查找找到插入位置
            int insertPosition = SortUtils.findInsertPosition(item.index, chatContactItemAoList);

            // 在合适位置插入新消息
            chatContactItemAoList.add(insertPosition, item);
        }

        // 消息队列避免并发
        mainHandler.post(() -> {
            // 尝试回调更新
            if (onRecentContactMessageChange != null){
                try {
                    onRecentContactMessageChange.onChange(chatContactItemAoList);
                } catch (Exception e){
                    Log.e(TAG, "调用更新RecentContactMessage异常" + e);
                }
            }
            else {
                Log.e(TAG, "没有设置OnRecentContactMessageChange");
            }
        });
    }

    /**
     * 检查是否有消息需要处理
     * @return  true 表示没有消息需要处理
     */
    private boolean checkIsNoMessageNeedProcess(){
        boolean chatContactIsEmpty = chatContactItemAoList.isEmpty();
        boolean chatMessageMapIsEmpty = chatMessageMap.isEmpty();
        boolean chatMessageListIsAllEmpty = true;
        if (!chatMessageMapIsEmpty){
            for (Map.Entry<Long, List<MessageItem>> entry : chatMessageMap.entrySet()) {
                if (!entry.getValue().isEmpty()){
                    chatMessageListIsAllEmpty = false;
                }
            }
        }
        return !chatContactIsEmpty || (!chatMessageMapIsEmpty && !chatMessageListIsAllEmpty);
    }

    /// 数据获取
    @NonNull
    public List<ChatContactItemAo> getRecentContactMessages(){
        return this.chatContactItemAoList;
    }

    @NonNull
    public List<MessageItem> getChatMessages(@NonNull Long contactId){
        if (contactId.equals(NettyConstants.ERROR_ID)){
            return new ArrayList<>();
        }
        List<MessageItem> messageList = chatMessageMap.get(contactId);
        if (messageList == null){
            messageList = new ArrayList<>();
        }
        return messageList;
    }

    /**
     * 停止消息处理线程
     */
    public void stop() {
        running = false; // 设置为停止状态
        if (messageProcessor != null) {
            try {
                messageProcessor.join(); // 等待线程结束
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 停止并启动消息处理线程
     */
    public void start(){
        stop();
        running = true;
        startMessageProcessor();
    }

    public void cleanChatActivityParam(){
        this.currentChatMessageContext = null;
    }
}
