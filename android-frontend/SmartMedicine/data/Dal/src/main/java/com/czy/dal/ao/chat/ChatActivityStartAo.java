package com.czy.dal.ao.chat;

import com.czy.baseUtilsLib.json.BaseBean;
import com.czy.dal.vo.entity.message.ChatMessageItemVo;

import java.io.Serializable;
import java.util.List;

/**
 * @author 13225
 */
public class ChatActivityStartAo implements Serializable, BaseBean {

    // View
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

    // 初始化的输入框
    public String inputText;
}
