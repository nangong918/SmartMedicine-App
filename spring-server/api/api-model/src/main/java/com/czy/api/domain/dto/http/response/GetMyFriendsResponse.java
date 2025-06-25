package com.czy.api.domain.dto.http.response;



import com.czy.api.domain.ao.relationship.MyFriendItemAo;
import com.czy.api.domain.dto.http.base.BaseHttpResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetMyFriendsResponse extends BaseHttpResponse {

    /**
     * 请求添加我的列表
     */
    public List<MyFriendItemAo> addMeRequestList;

}
