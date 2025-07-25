package com.czy.api.domain.ao.oss;


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
