package com.czy.api.domain.dto.http.response;



import com.czy.api.domain.ao.relationship.MyFriendItemAo;
import lombok.Data;

import java.util.List;

@Data
public class GetMyFriendsResponse {

    /**
     * 请求添加我的列表
     */
    public List<MyFriendItemAo> addMeRequestList;

}
