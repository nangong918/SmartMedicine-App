package com.czy.api.domain.dto.http.response;





import com.czy.api.domain.ao.relationship.NewUserItemAo;
import com.czy.api.domain.dto.http.base.BaseHttpResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
public class GetAddMeRequestListResponse extends BaseHttpResponse {

    /**
     * 请求添加我的列表
     */
    public List<NewUserItemAo> addMeRequestList;

}
