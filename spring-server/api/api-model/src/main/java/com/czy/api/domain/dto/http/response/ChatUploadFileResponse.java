package com.czy.api.domain.dto.http.response;


import lombok.Data;

@Data
public class ChatUploadFileResponse {
    // 上传文件id
    public Long uploadFileId;
    // 上传文件url
    public String uploadFileUrl;
}
