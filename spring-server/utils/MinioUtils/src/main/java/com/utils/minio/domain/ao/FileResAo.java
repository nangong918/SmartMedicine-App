package com.utils.minio.domain.ao;


import lombok.Data;

/**
 * 用于相应前端的FIleAo
 */
@Data
public class FileResAo {
    public String fileName;
    public Long fileId;
    public String fileUrl;
    public Long uploadUserId;
}
