package com.czy.datalib.databaseRepository;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.czy.dal.Do.MessageDo;

import java.util.List;

@Dao
public interface MessageDao {

    /**
     * 插入多条消息
     * @param message
     */
    @Insert
    List<Long> insert(MessageDo... message);


    /**
     * 查询指定用户之间的消息 ID 列表，根据 mid 向前查询
     * @param senderId 发送者 ID
     * @param receiverId 接收者 ID
     * @param lastMessageId 最后一条消息的 ID
     * @param limit 查询的条数
     * @return 消息 ID 列表
     */
    @Query("SELECT mid FROM message " +
            "WHERE sender_id IN (:senderId, :receiverId) " +
            "AND receiver_id IN (:senderId, :receiverId) " +
            "AND mid < :lastMessageId " +
            "ORDER BY timestamp DESC LIMIT :limit")
    List<Integer> loadMessageIdsById(Long senderId, Long receiverId, int lastMessageId, int limit);

    /**
     * 查询指定用户之间的消息 ID 列表，根据 timestamp 向前查询
     * @param senderId 发送者 ID
     * @param receiverId 接收者 ID
     * @param lastTimestamp 最后一条消息的时间戳
     * @param limit 查询的条数
     * @return 消息 ID 列表
     */
    @Query("SELECT mid FROM message " +
            "WHERE sender_id IN (:senderId, :receiverId) " +
            "AND receiver_id IN (:senderId, :receiverId) " +
            "AND timestamp < :lastTimestamp " +
            "ORDER BY timestamp DESC LIMIT :limit")
    List<Integer> loadMessageIdsByTimestamp(Long senderId, Long receiverId, long lastTimestamp, int limit);

    /**
     * 根据消息 ID 列表查询具体消息
     * @param ids 消息 ID 列表
     * @return 消息列表
     */
    @Query("SELECT * FROM message " +
            "WHERE mid IN (:ids)")
    List<MessageDo> loadMessagesByIds(List<Integer> ids);

    /**
     * 插入消息列表
     * @param messages 消息对象列表
     * @return 插入的消息 ID 列表
     */
    @Insert
    List<Long> insertMessages(List<MessageDo> messages);

    /**
     * 根据消息 ID 列表批量删除消息
     * @param ids 消息 ID 列表
     */
    @Query("DELETE FROM message " +
            "WHERE mid IN (:ids)")
    void deleteMessagesByIds(List<Integer> ids);

    /**
     * 删除所有消息
     */
    @Query("DELETE FROM message")
    void deleteAllMessages();

    /**
     * 根据消息列表批量更新消息
     * @param messages 消息对象列表
     */
    @Update
    void updateMessages(List<MessageDo> messages);
}
