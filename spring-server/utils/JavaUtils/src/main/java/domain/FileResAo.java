package domain;

import lombok.Data;

/**
 * @author 13225
 * @date 2025/8/12 17:43
 */
@Data
public class FileResAo {
    public Long fileId;
    public String fileUrl;
    // 暂未使用
    public Long uploadUserId;
}
