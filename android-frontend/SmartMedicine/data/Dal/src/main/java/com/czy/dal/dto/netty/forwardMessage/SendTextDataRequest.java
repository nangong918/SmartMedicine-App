package com.czy.dal.dto.netty.forwardMessage;



import com.czy.baseUtilsLib.json.BaseBean;
import com.czy.dal.dto.netty.base.BaseRequestData;

/**
 * @author 13225
 * @date 2025/2/8 18:18
 */

public class SendTextDataRequest extends BaseRequestData implements BaseBean {
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
