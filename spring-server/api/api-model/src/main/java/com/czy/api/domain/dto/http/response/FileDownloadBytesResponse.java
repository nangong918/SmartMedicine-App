package com.czy.api.domain.dto.http.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class FileDownloadBytesResponse implements Serializable {
    private byte[] fileBytes;
    private String fileName;

    public FileDownloadBytesResponse(byte[] fileBytes, String fileName) {
        this.fileBytes = fileBytes;
        this.fileName = fileName;
    }

    public FileDownloadBytesResponse(){

    }

}
