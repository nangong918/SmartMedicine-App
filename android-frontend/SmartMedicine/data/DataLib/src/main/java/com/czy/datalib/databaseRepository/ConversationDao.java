package com.czy.datalib.databaseRepository;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.czy.dal.Do.ConversationDo;

import java.util.List;

@Dao
public interface ConversationDao {

    /**
     * 插入一条对话记录
     * @param conversations 对话对象s
     * @return 插入的对话 ID
     */
    @Insert
    List<Long> insert(ConversationDo... conversations);

    /**
     * 查询特定用户的对话记录
     * @param userId 用户 ID
     * @return 对话记录列表
     */
    @Query("SELECT * FROM conversation " +
            "WHERE sender_id = :userId " +
            "OR receiver_id = :userId ORDER BY timestamp DESC")
    List<ConversationDo> loadConversationsByUserId(String userId);


    /**
     * 查询特定用户的对话记录，限制数量
     * @param userId 用户 ID
     * @param limit 限制数量
     * @return 对话记录列表
     */
    @Query("SELECT * FROM conversation " +
            "WHERE sender_id = :userId " +
            "OR receiver_id = :userId " +
            "ORDER BY timestamp DESC " +
            "LIMIT :limit")
    List<ConversationDo> loadConversationsByUserIdWithLimit(String userId, int limit);

    /**
     * 根据对话 ID 删除对话记录
     * @param conversationId 对话 ID
     */
    @Query("DELETE FROM conversation WHERE " +
            "conversation_id = :conversationId")
    void deleteById(long conversationId);

    /**
     * 删除特定用户的所有对话记录
     * @param userId 用户 ID
     */
    @Query("DELETE FROM conversation WHERE " +
            "sender_id = :userId " +
            "OR receiver_id = :userId")
    void deleteAllByUserId(String userId);

    /**
     * 更新对话记录
     * @param conversations 对话对象
     */
    @Update
    Integer update(ConversationDo... conversations);

}
