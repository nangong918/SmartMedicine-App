package domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 13225
 * @date 2025/4/22 16:38
 */
@Data
public class FileOptionResult {
    private List<ErrorFile> ErrorFiles = new ArrayList<>();
    private List<SuccessFile> successFiles = new ArrayList<>();
}
