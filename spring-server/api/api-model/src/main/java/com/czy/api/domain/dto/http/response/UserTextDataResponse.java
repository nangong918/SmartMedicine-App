package com.czy.api.domain.dto.http.response;



import com.czy.api.domain.dto.base.BaseResponseData;
import json.BaseBean;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 13225
 * @date 2025/2/8 19:03
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserTextDataResponse extends BaseResponseData implements BaseBean {
    private String title;
    private String content;
    public String senderName;
    // 当值不为空才更新
    public String avatarUrl = "";
}
