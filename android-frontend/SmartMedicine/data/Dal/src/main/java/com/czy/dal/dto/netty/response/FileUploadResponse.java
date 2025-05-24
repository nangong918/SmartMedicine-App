package com.czy.dal.dto.netty.response;

import java.io.Serializable;

public class FileUploadResponse implements Serializable {
    private String uploadState;

    public String getUploadState() {
        return uploadState;
    }

    public void setUploadState(String uploadState) {
        this.uploadState = uploadState;
    }

    public FileUploadResponse() {
    }
}
