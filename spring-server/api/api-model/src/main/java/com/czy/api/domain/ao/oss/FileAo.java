package com.czy.api.domain.ao.oss;

import json.BaseBean;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 13225
 * @date 2025/6/5 18:03
 */
@Data
public class FileAo implements BaseBean, Serializable {
    private String fileName;
    private String filePath;
    private Long fileSize;
}
