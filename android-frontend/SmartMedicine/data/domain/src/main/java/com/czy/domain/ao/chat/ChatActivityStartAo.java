package com.czy.domain.ao.chat;

import com.czy.baseUtilsLib.json.BaseBean;
import com.czy.domain.vo.entity.message.ChatMessageItemVo;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * @author 13225
 */
public class ChatActivityStartAo implements Serializable, BaseBean {

    // View
    @Deprecated(since = "2025/8/2; chatActivity自己会拿，不需要传递，多此一举")
    public List<ChatMessageItemVo> chatMessageListItemVo;

    /**
     * 联系人名称/群组名称
     */
    public String contactName;

    /**
     * 头像
     */
    public String avatarUrl;

    // Data
    /**
     * 联系人id/群组id
     */
    public String contactAccount;
    public Long contactId;

    // 初始化的输入框
    public String inputText;

    public static ChatActivityStartAo getStartAoByChatContactItemAo(ChatContactItemAo ao, String inputText){
        ChatActivityStartAo startAo = new ChatActivityStartAo();
        startAo.contactName = ao.chatContactItemVo.name;
        startAo.avatarUrl = ao.chatContactItemVo.avatarUrlOrUri;
        startAo.contactAccount = ao.contactAccount;
        startAo.contactId = ao.userId;
        startAo.inputText = Optional.ofNullable(inputText).orElse("");
        return startAo;
    }

}
