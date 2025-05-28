package com.czy.dal.dto.netty.response;

import android.util.Base64;

import java.io.Serializable;

public class FileDownloadBytesResponse implements Serializable {
    private String fileBytes;
    private String fileName;

    /**
     * Spring Boot网络传输中，基本数据类型就是基本数据类型进行传输
     * 如果采用的是DTO这种类型进行传输，byte，Map这种非基本数据结构会转化为Json；
     * 其中byte[] 将会经过Base64编码变为String 方便数据进行传输。
     * 在需要byte[]类型的地方可以将其通过Base64.decode转化为需要的字节数组
     */
    public byte[] getFileBytes() {
        return Base64.decode(fileBytes, Base64.DEFAULT);
    }

    public void setFileBytes(String fileBytes) {
        this.fileBytes = fileBytes;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
