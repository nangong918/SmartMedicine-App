package com.czy.api.domain.dto.http.request;


import com.czy.api.domain.dto.http.base.BaseHttpRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.NotNull;

/**
 * @author 13225
 * @date 2025/2/26 14:51
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FetchUserMessageRequest extends BaseHttpRequest {
    // 用于查询消息记录的起始索引
    @NotNull(message = "时间戳索引不能为空")
    public Long timestampIndex;
    // 消息条数 当其大于200的时候设置为200
    @NotNull(message = "消息条数不能为空")
    public Integer messageCount;
}
