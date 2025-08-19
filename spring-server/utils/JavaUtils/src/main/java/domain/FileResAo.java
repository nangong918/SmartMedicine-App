package domain;

import lombok.Data;

/**
 * @author 13225
 * @date 2025/8/12 17:43
 */
@Data
public class FileResAo {
    public Long fileId;
    // bo无法获得，需要通过minio获取
    public String fileUrl;
    // 暂未使用
    public Long uploadUserId;
}
