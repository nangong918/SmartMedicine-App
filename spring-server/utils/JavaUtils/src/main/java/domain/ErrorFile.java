package domain;

import json.BaseBean;
import lombok.Data;

/**
 * @author 13225
 * @date 2025/4/18 11:51
 */
@Data
public class ErrorFile implements BaseBean {
    private String fileName;
    private Long fileId = null;
    private String errorMessage;

    public ErrorFile() {
    }

    public ErrorFile(String fileName, String errorMessage) {
        this.fileName = fileName;
        this.errorMessage = errorMessage;
    }

    public ErrorFile(String fileName, Long fileId, String errorMessage) {
        this.fileName = fileName;
        this.fileId = fileId;
        this.errorMessage = errorMessage;
    }
}
