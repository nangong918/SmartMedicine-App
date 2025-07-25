package com.czy.api.domain.dto.http.request;

import com.czy.api.domain.dto.http.base.BaseHttpRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SearchUserRequest extends BaseHttpRequest {
    // userData是因为不知道是账号，还是名称，还是手机号
    private String userData;
}
