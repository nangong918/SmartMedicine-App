package domain;

import json.BaseBean;
import lombok.Data;

/**
 * @author 13225
 * @date 2025/4/22 16:37
 */
@Data
public class SuccessFile implements BaseBean {
    private String fileName;
    private String fileStorageName;
    private Long fileSize;
    private Long fileId = null;
    public SuccessFile(){

    }
    public SuccessFile(String fileName, String fileStorageName, Long fileSize) {
        this.fileName = fileName;
        this.fileStorageName = fileStorageName;
        this.fileSize = fileSize;
    }
}
