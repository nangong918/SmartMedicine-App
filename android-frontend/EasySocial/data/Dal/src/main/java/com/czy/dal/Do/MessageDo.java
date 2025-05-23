package com.czy.dal.Do;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.czy.baseUtilsLib.json.BaseBean;

@Entity(tableName = "message")
public class MessageDo implements BaseBean {
    @PrimaryKey
    @ColumnInfo(name = "mid")
    public int mid;
    @ColumnInfo(name = "sender_id")
    public String senderId;
    @ColumnInfo(name = "receiver_id")
    public String receiverId;
    @ColumnInfo(name = "message_type")
    public String messageType;
    @ColumnInfo(name = "timestamp", index = true) // 添加索引
    public long timestamp;
}
