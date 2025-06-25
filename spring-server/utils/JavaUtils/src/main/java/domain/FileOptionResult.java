package domain;

import json.BaseBean;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 13225
 * @date 2025/4/22 16:38
 */
@Data
public class FileOptionResult implements BaseBean , Serializable {
    private List<ErrorFile> ErrorFiles = new ArrayList<>();
    private List<SuccessFile> successFiles = new ArrayList<>();

    public FileOptionResult() {
    }

    public FileOptionResult(List<ErrorFile> errorFiles, List<SuccessFile> successFiles) {
        ErrorFiles = errorFiles;
        this.successFiles = successFiles;
    }
}
