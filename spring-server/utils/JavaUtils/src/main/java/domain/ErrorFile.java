package domain;

import lombok.Data;

/**
 * @author 13225
 * @date 2025/4/18 11:51
 */
@Data
public class ErrorFile {
    private String fileName;
    private Long fileId;
    private String errorMessage;

    public ErrorFile() {
    }

    public ErrorFile(String fileName, String errorMessage) {
        this.fileName = fileName;
        this.errorMessage = errorMessage;
    }
}
