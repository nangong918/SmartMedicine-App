package com.czy.domain.dto.netty.forwardMessage;



import com.czy.baseutil.json.BaseBean;
import com.czy.domain.dto.netty.base.BaseResponseData;

/**
 * @author 13225
 * @date 2025/2/8 19:03
 */

public class UserImageResponse extends BaseResponseData implements BaseBean {
    public String title;
    public String account;
    public String content;
    public String imageUrl;
    public Long imageFileId;
    public String senderName;
}
