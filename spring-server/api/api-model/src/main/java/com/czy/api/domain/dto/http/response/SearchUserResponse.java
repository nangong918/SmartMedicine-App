package com.czy.api.domain.dto.http.response;


import com.czy.api.domain.ao.relationship.SearchFriendApplyAo;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SearchUserResponse {

    public List<SearchFriendApplyAo> userList;

    public SearchUserResponse(){
        userList = new ArrayList<>();
    }
}
