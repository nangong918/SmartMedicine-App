package com.czy.api.api.user_relationship;

import com.czy.api.domain.Do.user.UserDo;

import java.util.List;

/**
 * @author 13225
 * @date 2025/4/15 18:25
 */
public interface UserSearchService {

    /**
     * 用户的 account 用 mysql的like模糊匹配
     * @param account   用户的 account
     * @return          用户List的 Do
     */
    List<UserDo> searchUserByLikeAccount(String account);

    /**
     * 用户的 name 用 elastic-search的分词模糊匹配
     * @param userName  用户的 name
     * @return          用户List的 Do
     */
    List<UserDo> searchUserByIkName(String userName);
}
