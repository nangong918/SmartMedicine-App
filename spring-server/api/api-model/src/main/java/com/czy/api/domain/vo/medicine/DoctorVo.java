package com.czy.api.domain.vo.medicine;

import domain.FileResAo;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 13225
 * @date 2025/8/18 14:44
 */
@Data
public class DoctorVo implements Cloneable , Serializable {
    // 头像
    public FileResAo doctorAvatarFileAo = new FileResAo();
    // 姓名
    public String doctorName;
    // 职称
    public String doctorTitle;

    @Override
    public DoctorVo clone() throws CloneNotSupportedException {
        DoctorVo cloned = (DoctorVo) super.clone();
        // 深克隆 doctorAvatarFileAo
        if (this.doctorAvatarFileAo != null){
            cloned.doctorAvatarFileAo = this.doctorAvatarFileAo.clone();
        }
        return cloned;
    }
}
