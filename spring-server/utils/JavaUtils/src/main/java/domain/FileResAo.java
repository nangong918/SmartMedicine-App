package domain;

import lombok.Data;

/**
 * @author 13225
 * @date 2025/8/12 17:43
 */
@Data
public class FileResAo {
    public String fileName;
    public Long fileId;
    public String fileUrl;
    public Long uploadUserId;
}
