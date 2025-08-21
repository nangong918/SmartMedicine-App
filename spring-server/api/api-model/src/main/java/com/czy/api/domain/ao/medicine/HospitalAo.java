package com.czy.api.domain.ao.medicine;

import com.czy.api.domain.ao.LocationAo;
import com.czy.api.domain.vo.medicine.HospitalVo;
import lombok.Data;

/**
 * @author 13225
 * @date 2025/8/18 14:47
 */
@Data
public class HospitalAo implements Cloneable {
    // vo
    public HospitalVo hospitalVo;

    // data
    // position
    public LocationAo locationAo;
    // 经纬度
    public Double longitude;
    public Double latitude;

    @Override
    public HospitalAo clone() throws CloneNotSupportedException {
        HospitalAo cloned = (HospitalAo) super.clone();
        cloned.hospitalVo = hospitalVo.clone();
        cloned.locationAo = locationAo.clone();
        return cloned;
    }
}
