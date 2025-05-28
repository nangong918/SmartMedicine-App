package com.czy.dal.dto.netty.forwardMessage;



import com.czy.baseUtilsLib.json.BaseBean;
import com.czy.dal.dto.netty.base.BaseResponseData;

/**
 * @author 13225
 * @date 2025/2/8 19:03
 */

public class GroupTextDataResponse extends BaseResponseData implements BaseBean {
    private String title;
    private String content;
    private String groupId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
