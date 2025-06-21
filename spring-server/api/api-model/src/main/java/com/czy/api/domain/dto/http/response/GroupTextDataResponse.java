package com.czy.api.domain.dto.http.response;


import com.czy.api.domain.dto.http.base.BaseHttpResponse;
import json.BaseBean;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 13225
 * @date 2025/2/8 19:03
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GroupTextDataResponse extends BaseHttpResponse implements BaseBean {
    private String title;
    private String content;
    private String groupId;

}
