package com.czy.api.domain.dto.http.request;



import com.czy.api.domain.dto.base.BaseRequestData;
import json.BaseBean;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 13225
 * @date 2025/2/7 18:18
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RegisterRequest extends BaseRequestData implements BaseBean {
    private String uuid;
    private String deviceId;
    private String deviceName;
    private String appVersion;
    private String osVersion;
    private String packageName;
    private String language;
}
