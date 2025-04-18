package com.czy.api.domain.dto.http.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class FileUploadResponse implements Serializable {
    private String uploadState;

    public FileUploadResponse() {
    }

    public FileUploadResponse(String uploadState) {
        this.uploadState = uploadState;
    }
}
