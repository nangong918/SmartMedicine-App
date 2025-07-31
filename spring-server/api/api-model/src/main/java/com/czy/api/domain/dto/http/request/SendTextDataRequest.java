package com.czy.api.domain.dto.http.request;



import com.czy.api.domain.dto.base.BaseRequestData;
import json.BaseBean;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 13225
 * @date 2025/2/8 18:18
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SendTextDataRequest extends BaseRequestData implements BaseBean {
    private String content;
    // 由前端自己生成的消息id，用于通知Android端哪个消息被处理了
    private String androidMessageId;
}
