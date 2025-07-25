package com.czy.dal.dto.http.request;



/**
 * @author 13225
 * @date 2025/7/24 15:09
 */
public class UserBriefRequest {
    public Long senderId;
    public Long receiverId;
    // 从0开始：从第一个开始
    public Integer postNum = 0;
    public Integer postSize = 4;
}
