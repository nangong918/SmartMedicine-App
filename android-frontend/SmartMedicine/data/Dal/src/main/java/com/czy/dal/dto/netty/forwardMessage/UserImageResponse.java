package com.czy.dal.dto.netty.forwardMessage;



import com.czy.baseUtilsLib.json.BaseBean;
import com.czy.dal.dto.netty.base.BaseResponseData;

/**
 * @author 13225
 * @date 2025/2/8 19:03
 */

public class UserImageResponse extends BaseResponseData implements BaseBean {
    public String title;
    public String account;
    public String imageUrl;
    public String senderName;
    // 当值不为空才更新
    public String avatarUrl = "";
}
