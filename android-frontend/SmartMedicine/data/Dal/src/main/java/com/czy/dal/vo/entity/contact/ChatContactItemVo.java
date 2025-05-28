package com.czy.dal.vo.entity.contact;



/**
 * @author 13225
 * 所有ListItem取消使用LiveData，观察交给DiffUtil而不是一个观察者观察无穷多的数据
 */
public class ChatContactItemVo {

    // 头像（支持网络 URL 或本地 URI）
    public String avatarUrlOrUri = "";

    // 名称
    public String name = "";

    // 消息概览
    public String messagePreview = "";

    // 时间
    public String time = "";

    // 未读消息条数 （注意0条的时候隐藏view）
    public Integer unreadCount = 0;

    public ChatContactItemVo(){

    }
    public ChatContactItemVo(ChatContactItemVo vo){
        this.avatarUrlOrUri = vo.avatarUrlOrUri;
        this.name = vo.name;
        this.messagePreview = vo.messagePreview;
        this.time = vo.time;
        this.unreadCount = vo.unreadCount;
    }

    public void setMessagePreview(String content){
        // 长度小于20就全部，大于20就裁剪20 + ...
        if (content.length() <= 20){
            messagePreview = content;
        }
        else {
            messagePreview = content.substring(0, 20) + "...";
        }
    }
}
