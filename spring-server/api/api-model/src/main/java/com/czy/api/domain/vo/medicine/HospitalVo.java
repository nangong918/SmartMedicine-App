package com.czy.api.domain.vo.medicine;

import lombok.Data;

/**
 * @author 13225
 * @date 2025/8/18 14:48
 */
@Data
public class HospitalVo {
    // name
    public String name;
    // 等级
    public String level;

    @Override
    public HospitalVo clone() throws CloneNotSupportedException {
        return (HospitalVo) super.clone();
    }
}
