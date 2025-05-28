package com.czy.dal.dto.http.request;



/**
 * @author 13225
 * @date 2025/4/21 11:29
 */

public class PostPublishRequest extends BaseNettyRequest {
    // title；not null
    public String title;
    // content；not null
    public String content;
    public Boolean isHaveFiles;
}
