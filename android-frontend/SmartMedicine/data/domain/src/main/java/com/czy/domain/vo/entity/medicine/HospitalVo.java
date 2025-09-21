package com.czy.domain.vo.entity.medicine;


import androidx.annotation.NonNull;

import java.io.Serializable;

/**
 * @author 13225
 * @date 2025/8/18 14:48
 */
public class HospitalVo implements Cloneable, Serializable {
    // name
    public String name;
    // 等级
    public String level;

    @NonNull
    @Override
    public HospitalVo clone() throws CloneNotSupportedException {
        return (HospitalVo) super.clone();
    }
}
