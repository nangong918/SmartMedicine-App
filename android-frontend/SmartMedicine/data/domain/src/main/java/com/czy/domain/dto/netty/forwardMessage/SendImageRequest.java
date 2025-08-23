package com.czy.domain.dto.netty.forwardMessage;



import com.czy.baseUtilLib.json.BaseBean;
import com.czy.domain.dto.netty.base.BaseRequestData;

/**
 * @author 13225
 * @date 2025/2/8 18:18
 * 思考文件的相通性检查，相同的文件发送了两次就不要存储了
 */

public class SendImageRequest extends BaseRequestData implements BaseBean {
    public String fileName;
    // 由前端自己生成的消息id，用于通知Android端哪个消息被处理了
    public String androidMessageId;
    // 文本消息内容
    public String content;
}
