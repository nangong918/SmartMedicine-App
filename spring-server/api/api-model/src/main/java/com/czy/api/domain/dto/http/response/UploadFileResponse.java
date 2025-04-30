package com.czy.api.domain.dto.http.response;

import com.czy.api.domain.dto.base.BaseResponseData;
import json.BaseBean;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 13225
 * @date 2025/4/29 18:19
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UploadFileResponse extends BaseResponseData implements BaseBean {
    public Long fileId;
    public Long messageId;
    public String receiverAccount;
}
