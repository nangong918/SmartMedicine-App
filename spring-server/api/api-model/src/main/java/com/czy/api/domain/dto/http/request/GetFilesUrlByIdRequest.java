package com.czy.api.domain.dto.http.request;

import json.BaseBean;
import lombok.Data;

import java.util.List;

/**
 * @author 13225
 * @date 2025/6/5 17:33
 */
@Data
public class GetFilesUrlByIdRequest implements BaseBean {
    private List<Long> fileIds;
}
