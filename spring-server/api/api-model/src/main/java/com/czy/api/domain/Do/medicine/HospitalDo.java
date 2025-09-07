package com.czy.api.domain.Do.medicine;

import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * @author 13225
 * @date 2025/8/18 15:20
 */
@Data
public class HospitalDo {
    @Id
    // 记录id
    private Long id;
    // name
    public String name;
    // 等级 （三甲，三丙）
    public String level;
    // 地区
    public String province;
    public String city;
    public String region;
    // 经纬度
    public Double longitude;
    public Double latitude;
}
