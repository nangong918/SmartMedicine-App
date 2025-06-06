package com.czy.api.domain.dto.http.request;

import json.BaseBean;
import lombok.Data;

import java.util.List;

/**
 * @author 13225
 * @date 2025/6/5 17:33
 */
@Data
public class GetFilesUrlByNameRequest implements BaseBean {
    private List<String> fileNames;
    private String bucketName;
}
