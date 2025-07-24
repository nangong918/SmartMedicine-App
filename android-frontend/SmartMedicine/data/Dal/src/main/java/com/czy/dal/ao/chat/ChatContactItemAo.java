package com.czy.dal.ao.chat;

import android.util.Log;

import com.czy.baseUtilsLib.json.BaseBean;
import com.czy.dal.constant.Constants;
import com.czy.dal.vo.entity.contact.ChatContactItemVo;

import java.util.Objects;

/**
 * @author 13225
 */
public class ChatContactItemAo implements BaseBean {

    // view
    // 单个联系人Vo信息
    public ChatContactItemVo chatContactItemVo = new ChatContactItemVo();

    // data
    // 联系人账号信息，用于搜索
    public String contactAccount;
    public Long userId;

    public ChatContactItemAo() {

    }

    public ChatContactItemAo(ChatContactItemAo ao){
        this.contactAccount = ao.contactAccount;
        this.userId = ao.userId;
        this.chatContactItemVo = new ChatContactItemVo(ao.chatContactItemVo);
    }

    // 用于判断两个对象是否属于一个对象（用唯一标识符判断）
    public boolean isItemEquals(Object o){
        if (o instanceof ChatContactItemAo that){
            return this.contactAccount.equals(that.contactAccount);
        }
        return false;
    }

    public boolean isContentEquals(Object o){
        if (this == o) return true;
        if (o instanceof ChatContactItemAo that){

            String thisName = chatContactItemVo.name == null ? "" : chatContactItemVo.name;
            String thatName = that.chatContactItemVo.name == null ? "" : that.chatContactItemVo.name;
            String thisMessagePreview = chatContactItemVo.messagePreview == null ? "" : chatContactItemVo.messagePreview;
            Long thisUserId = userId == null ? Constants.ERROR_ID : userId;
            String thatMessagePreview = that.chatContactItemVo.messagePreview == null ? "" : that.chatContactItemVo.messagePreview;
            String thisTime = chatContactItemVo.time == null ? "" : chatContactItemVo.time;
            String thatTime = that.chatContactItemVo.time == null ? "" : that.chatContactItemVo.time;
            Long thatUserId = that.userId == null ? Constants.ERROR_ID : userId;
            int thisUnreadCount = chatContactItemVo.unreadCount;
            int thatUnreadCount = that.chatContactItemVo.unreadCount;
            String thisContactAccount = contactAccount == null ? "" : contactAccount;
            String thatContactAccount = that.contactAccount == null ? "" : that.contactAccount;

            return thisName.equals(thatName) &&
                    thisMessagePreview.equals(thatMessagePreview) &&
                    thisTime.equals(thatTime) &&
                    thisUserId.equals(thatUserId) &&
                    thisUnreadCount == thatUnreadCount &&
                    thisContactAccount.equals(thatContactAccount);
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
//        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatContactItemAo that = (ChatContactItemAo) o;

        String thisName = chatContactItemVo.name == null ? "" : chatContactItemVo.name;
        String thatName = that.chatContactItemVo.name == null ? "" : that.chatContactItemVo.name;
        String thisMessagePreview = chatContactItemVo.messagePreview == null ? "" : chatContactItemVo.messagePreview;
        Long thisUserId = userId == null ? Constants.ERROR_ID : userId;
        String thatMessagePreview = that.chatContactItemVo.messagePreview == null ? "" : that.chatContactItemVo.messagePreview;
        String thisTime = chatContactItemVo.time == null ? "" : chatContactItemVo.time;
        String thatTime = that.chatContactItemVo.time == null ? "" : that.chatContactItemVo.time;
        Long thatUserId = that.userId == null ? Constants.ERROR_ID : userId;
        int thisUnreadCount = chatContactItemVo.unreadCount;
        int thatUnreadCount = that.chatContactItemVo.unreadCount;
        String thisContactAccount = contactAccount == null ? "" : contactAccount;
        String thatContactAccount = that.contactAccount == null ? "" : that.contactAccount;

        Log.i("ChatContactItemAo", "equals::thisName: " + thisName + ", thatName: " + thatName);
        Log.i("ChatContactItemAo", "equals::thisMessagePreview: " + thisMessagePreview + ", thatMessagePreview: " + thatMessagePreview);
        Log.i("ChatContactItemAo", "equals::thisTime: " + thisTime + ", thatTime: " + thatTime);
        Log.i("ChatContactItemAo", "equals::thisUnreadCount: " + thisUnreadCount + ", thatUnreadCount: " + thatUnreadCount);
        Log.i("ChatContactItemAo", "equals::thisContactAccount: " + thisContactAccount + ", thatContactAccount: " + thatContactAccount);
        Log.i("ChatContactItemAo", "equals::equals: " + (thisName.equals(thatName) &&
                thisMessagePreview.equals(thatMessagePreview) &&
                thisTime.equals(thatTime) &&
                thisUserId.equals(thatUserId) &&
                thisUnreadCount == thatUnreadCount &&
                thisContactAccount.equals(thatContactAccount)));

        return thisName.equals(thatName) &&
                thisMessagePreview.equals(thatMessagePreview) &&
                thisUserId.equals(thatUserId) &&
                thisTime.equals(thatTime) &&
                thisUnreadCount == thatUnreadCount &&
                thisContactAccount.equals(thatContactAccount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatContactItemVo, contactAccount);
    }
}
