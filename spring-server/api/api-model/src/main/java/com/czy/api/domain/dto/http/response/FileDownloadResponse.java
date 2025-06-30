package com.czy.api.domain.dto.http.response;

import com.czy.api.domain.ao.oss.FileResAo;
import lombok.Data;

import java.io.Serializable;

@Data
public class FileDownloadResponse implements Serializable {
    private FileResAo fileResAo;
}
