package com.czy.api.domain.dto.http.response;



import com.czy.api.domain.ao.relationship.NewUserItemAo;
import com.czy.api.domain.dto.http.base.BaseHttpResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetHandleMyAddUserResponseListResponse extends BaseHttpResponse {

    /**
     * 我请求添加好友的结果响应列表
     */
    public List<NewUserItemAo> handleMyAddUserResponseList;

}
