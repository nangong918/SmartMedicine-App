package com.czy.dal.vo.entity.message;


import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.Nullable;

import com.czy.baseUtilsLib.date.DateUtils;
import com.czy.dal.ao.chat.ChatContactItemAo;
import com.czy.dal.constant.MessageTypeEnum;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * @author 13225
 * 聊天消息的RecyclerView的VO
 */
public class ChatMessageItemVo implements Serializable {
    private static final String TAG = ChatMessageItemVo.class.getSimpleName();

    public static long totalNumber = 0;

    private final long id;
    private final long createdTimestamp;

    public ChatMessageItemVo(){
        id = totalNumber;
        totalNumber++;
        createdTimestamp = System.currentTimeMillis();
    }

    public long getId(){
        return id;
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

    // 图片资源
    public Bitmap bitmap = null;

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
            return this.timestamp == (that.timestamp);
        }
        return false;
    }

    public boolean isContentEquals(Object o){
        if (this == o) return true;
        if (!(o instanceof ChatMessageItemVo that)) return false;

        String thisAvatarUrl = avatarUrlOrUri == null ? "" : avatarUrlOrUri;
        String thatAvatarUrl = that.avatarUrlOrUri == null ? "" : that.avatarUrlOrUri;
        String thisContent = content == null ? "" : content;
        String thatContent = that.content == null ? "" : that.content;
        String thisTime = time == null ? "" : time;
        String thatTime = that.time == null ? "" : that.time;
        Boolean thisIsRead = isRead != null && isRead;
        Boolean thatIsRead = that.isRead != null && that.isRead;
        int thisViewType = viewType == 0 ? VIEW_TYPE_SENDER : VIEW_TYPE_RECEIVER;
        int thatViewType = that.viewType == 0 ? VIEW_TYPE_SENDER : VIEW_TYPE_RECEIVER;
        int thisMessageType = messageType == MessageTypeEnum.text.code ? MessageTypeEnum.text.code : MessageTypeEnum.image.code;
        int thatMessageType = that.messageType == MessageTypeEnum.text.code ? MessageTypeEnum.text.code : MessageTypeEnum.image.code;
        long thisTimestamp = timestamp;
        long thatTimestamp = that.timestamp;
        Bitmap thisBitmap = bitmap == null ? null : bitmap;
        Bitmap thatBitmap = that.bitmap == null ? null : that.bitmap;

        return thisAvatarUrl.equals(thatAvatarUrl) &&
                thisContent.equals(thatContent) &&
                thisTime.equals(thatTime) &&
                thisIsRead.equals(thatIsRead) &&
                thisViewType == thatViewType &&
                thisMessageType == thatMessageType &&
                thisTimestamp == thatTimestamp &&
                Objects.equals(thisBitmap, thatBitmap);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatMessageItemVo)) return false;

        String thisAvatarUrl = avatarUrlOrUri == null ? "" : avatarUrlOrUri;
        String thatAvatarUrl = ((ChatMessageItemVo) o).avatarUrlOrUri == null ? "" : ((ChatMessageItemVo) o).avatarUrlOrUri;
        String thisContent = content == null ? "" : content;
        String thatContent = ((ChatMessageItemVo) o).content == null ? "" : ((ChatMessageItemVo) o).content;
        String thisTime = time == null ? "" : time;
        String thatTime = ((ChatMessageItemVo) o).time == null ? "" : ((ChatMessageItemVo) o).time;
        Boolean thisIsRead = isRead != null && isRead;
        Boolean thatIsRead = ((ChatMessageItemVo) o).isRead != null && ((ChatMessageItemVo) o).isRead;
        int thisViewType = viewType == 0 ? VIEW_TYPE_SENDER : VIEW_TYPE_RECEIVER;
        int thatViewType = ((ChatMessageItemVo) o).viewType == 0 ? VIEW_TYPE_SENDER : VIEW_TYPE_RECEIVER;

        return thisAvatarUrl.equals(thatAvatarUrl) &&
                thisContent.equals(thatContent) &&
                thisTime.equals(thatTime) &&
                thisIsRead.equals(thatIsRead) &&
                thisViewType == thatViewType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(avatarUrlOrUri, content, time, viewType);
    }
}
