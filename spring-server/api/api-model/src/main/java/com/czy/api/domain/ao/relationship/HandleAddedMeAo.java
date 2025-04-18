package com.czy.api.domain.ao.relationship;



import com.czy.api.domain.dto.socket.request.HandleAddedUserRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 13225
 * @date 2025/3/3 18:06
 */
@Data
public class HandleAddedMeAo implements Serializable {
    public Integer handleType;
    public String applyAccount;
    public String handlerAccount;
    public Long handleTime;
    public String additionalContent;

    public void setByRequest(HandleAddedUserRequest request) {
        this.handleType = request.getHandleType();
        // 接收方是申请人
        this.applyAccount = request.getReceiverId();
        // 请求方是处理人
        this.handlerAccount = request.getSenderId();
        this.handleTime = Long.valueOf(request.getTimestamp());
        this.additionalContent = request.getAdditionalContent();
    }
}
