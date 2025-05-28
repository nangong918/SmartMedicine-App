package com.czy.dal.Do;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "conversation")
public class ConversationDo {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "conversation_id")
    public long conversationId; // 自增的对话 ID

    @ColumnInfo(name = "sender_id")
    public String senderId; // 用户 ID
    @ColumnInfo(name = "receiver_id", index = true)
    public String receiverId;// 收信人 ID
    @ColumnInfo(name = "last_message_id")
    public int lastMessageId; // 消息 ID，关联到 MessageDo 表
    @ColumnInfo(name = "timestamp")
    public long timestamp; // 对话时间戳

}
