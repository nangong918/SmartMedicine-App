package com.czy.dal.dto.http.request;

import java.util.List;

public class GetMyFriendsRequest extends BaseNettyRequest{

    /**
     * 前端已经有的accountList
     */
    public List<String> accountList;

}
