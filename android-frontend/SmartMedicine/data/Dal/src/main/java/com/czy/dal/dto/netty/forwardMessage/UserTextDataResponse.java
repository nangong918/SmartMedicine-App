package com.czy.dal.dto.netty.forwardMessage;



import com.czy.baseUtilsLib.json.BaseBean;
import com.czy.dal.dto.netty.base.BaseResponseData;

/**
 * @author 13225
 * @date 2025/2/8 19:03
 */

public class UserTextDataResponse extends BaseResponseData implements BaseBean {
    public String title;
    public String account;
    public String content;
    public String senderName;
    // 当值不为空才更新
    public String avatarUrl = "";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String message) {
        this.content = message;
    }
}
