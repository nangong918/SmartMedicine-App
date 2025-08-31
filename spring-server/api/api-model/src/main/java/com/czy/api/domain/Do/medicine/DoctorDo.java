package com.czy.api.domain.Do.medicine;

import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * @author 13225
 * @date 2025/8/18 15:15
 */
@Data
public class DoctorDo {
    @Id
    // 记录id
    private Long id;
    // name
    private String name;
    // title 职称
    private String title;
    // avatar 头像 fileId
    private Long avatarFileId;
}
