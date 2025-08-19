package com.czy.api.domain.vo.medicine;

import domain.FileResAo;
import lombok.Data;

/**
 * @author 13225
 * @date 2025/8/18 14:44
 */
@Data
public class DoctorVo {
    // 头像
    public FileResAo doctorAvatarFileAo = new FileResAo();
    // 姓名
    public String doctorName;
    // 职称
    public String doctorTitle;
}
