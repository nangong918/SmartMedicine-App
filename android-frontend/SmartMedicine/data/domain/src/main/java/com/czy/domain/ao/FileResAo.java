package com.czy.domain.ao;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class FileResAo implements Cloneable, Serializable {
    public Long fileId;
    // bo无法获得，需要通过minio获取
    public String fileUrl;
    // 暂未使用
    public Long uploadUserId;

    @NonNull
    @Override
    public FileResAo clone() throws CloneNotSupportedException {
        return (FileResAo) super.clone();
    }
}
