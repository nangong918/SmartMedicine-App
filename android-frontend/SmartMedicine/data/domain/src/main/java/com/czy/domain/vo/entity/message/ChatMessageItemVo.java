package com.czy.domain.vo.entity.message;


import android.util.Log;

import com.czy.baseUtilsLib.date.DateUtils;
import com.czy.domain.constant.MessageTypeEnum;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * @author 13225
 * 聊天消息的RecyclerView的VO
 */
public class ChatMessageItemVo implements Serializable {
    private static final String TAG = ChatMessageItemVo.class.getSimpleName();


    private String itemId;
    private final long createdTimestamp;

    public ChatMessageItemVo(){
        itemId = UUID.randomUUID().toString();
        createdTimestamp = System.currentTimeMillis();
    }

    public String getItemId(){
        return itemId;
    }

    public void setItemId(String itemId){
        this.itemId = itemId;
    }

    public long getCreatedTimestamp(){
        return createdTimestamp;
    }

    public static final int VIEW_TYPE_SENDER = 0;
    public static final int VIEW_TYPE_RECEIVER = 1;
    // 头像（支持网络 URL 或本地 URI）
    public String avatarUrlOrUri = "";

    // 消息概览
    public String content;

    // 时间
    public String time;

    // 是否已读
    public Boolean isRead;

    // 发送消息类型
    public int viewType;

    // 消息类型
    public int messageType = MessageTypeEnum.text.code;

    public long timestamp = System.currentTimeMillis();

    public void setTimeByStringTimeStamp(String timeStamp){
        try{
            long timeStampLong = Long.parseLong(timeStamp);
            Date date = new Date(timeStampLong);
            time = DateUtils.getTime(date);
        } catch (Exception e){
            Log.e(TAG, "setTimeByStringTimeStamp Error: ", e);
            Date date = new Date(System.currentTimeMillis());
            time = DateUtils.getTime(date);
        }
    }

    public void setTimeByStringTimeStamp(long timeStamp){
        this.timestamp = timeStamp;
        try{
            Date date = new Date(timeStamp);
            time = DateUtils.getTime(date);
        } catch (Exception e){
            Log.e(TAG, "setTimeByStringTimeStamp Error: ", e);
            Date date = new Date(System.currentTimeMillis());
            time = DateUtils.getTime(date);
        }
    }

    // 用于判断两个对象是否属于一个对象（用唯一标识符判断）
    public boolean isItemEquals(Object o){
        if (o instanceof ChatMessageItemVo that){
            return this.timestamp == (that.timestamp) &&
                    Objects.equals(this.itemId, that.itemId) &&
                    Objects.equals(this.content, that.content) &&
                    Objects.equals(this.messageType, that.messageType)
                    ;
        }
        return false;
    }

    public boolean isContentEquals(Object o){
        return equals(o);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessageItemVo vo = (ChatMessageItemVo) o;
        return createdTimestamp == vo.createdTimestamp &&
                viewType == vo.viewType &&
                messageType == vo.messageType &&
                timestamp == vo.timestamp &&
                Objects.equals(itemId, vo.itemId) &&
                Objects.equals(avatarUrlOrUri, vo.avatarUrlOrUri) &&
                Objects.equals(content, vo.content) &&
                Objects.equals(time, vo.time) &&
                Objects.equals(isRead, vo.isRead)
                ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, createdTimestamp, avatarUrlOrUri, content,
                time, isRead, viewType, messageType, timestamp);
    }
}
