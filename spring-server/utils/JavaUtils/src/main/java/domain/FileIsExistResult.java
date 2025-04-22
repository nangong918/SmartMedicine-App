package domain;

import lombok.Data;

/**
 * @author 13225
 * @date 2025/4/22 16:56
 */
@Data
public class FileIsExistResult {
    // 是否存在？
    public Boolean isExist;
    // 如果存再找到id
    public Long fileId;
}
