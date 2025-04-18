package com.czy.api.domain.Do.message;

import cn.hutool.core.util.IdUtil;
import com.czy.api.constant.es.FieldAnalyzer;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

/**
 * @author 13225
 * @date 2025/2/26 16:55
 * Table:user_chat_message
 * 组合索引（senderId，receiverId）
 */
@Data
@org.springframework.data.mongodb.core.mapping.Document("user_chat_message")
public class UserChatMessageDo implements Serializable {
    @Id
    // bigInt/not null/key
    public Long id;
    // text
    @Field(analyzer = FieldAnalyzer.IK_MAX_WORD, type = FieldType.Text)
    public String msgContent;
    // not null
    public Integer msgType;
    // not null;索引
    public Long senderId;
    // not null;索引
    public Long receiverId;
    // not null;时间索引；用于找到某个时间节点前后的另一条消息
    // eg：time < timestamp limit 1
    public Long timestamp = System.currentTimeMillis();

    public UserChatMessageDo() {
        // 设置雪花id
        this.id = IdUtil.getSnowflake().nextId();
    }
}
