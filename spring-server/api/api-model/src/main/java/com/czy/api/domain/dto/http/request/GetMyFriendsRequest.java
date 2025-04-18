package com.czy.api.domain.dto.http.request;


import com.czy.api.domain.dto.http.base.BaseNettyRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetMyFriendsRequest extends BaseNettyRequest {

    /**
     * 前端已经有的accountList
     */
    public List<String> accountList;

}
