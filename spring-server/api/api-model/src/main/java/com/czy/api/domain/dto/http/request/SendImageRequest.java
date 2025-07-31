package com.czy.api.domain.dto.http.request;



import com.czy.api.domain.dto.base.BaseRequestData;
import json.BaseBean;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 13225
 * @date 2025/2/8 18:18
 * 思考文件的相通性检查，相同的文件发送了两次就不要存储了
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SendImageRequest extends BaseRequestData implements BaseBean {
    public String fileName;
    // 由前端自己生成的消息id，用于通知Android端哪个消息被处理了
    public String androidMessageId;
    // 文本消息内容
    public String content;
}
