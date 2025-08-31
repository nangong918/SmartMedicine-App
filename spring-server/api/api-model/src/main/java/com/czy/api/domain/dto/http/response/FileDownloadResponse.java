package com.czy.api.domain.dto.http.response;


import domain.FileResAo;
import lombok.Data;

import java.io.Serializable;

@Data
public class FileDownloadResponse implements Serializable {
    private FileResAo fileResAo;
}
