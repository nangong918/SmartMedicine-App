package com.czy.domain.ao.medicine;


import androidx.annotation.NonNull;

import com.czy.domain.ao.LocationAo;
import com.czy.domain.vo.entity.medicine.HospitalVo;

import java.io.Serializable;

/**
 * @author 13225
 * @date 2025/8/18 14:47
 */
public class HospitalAo implements Cloneable , Serializable {
    // vo
    public HospitalVo hospitalVo;

    // data
    // position
    public LocationAo locationAo;
    // 经纬度
    public Double longitude;
    public Double latitude;

    @NonNull
    @Override
    public HospitalAo clone() throws CloneNotSupportedException {
        HospitalAo cloned = (HospitalAo) super.clone();
        if (hospitalVo != null){
            cloned.hospitalVo = hospitalVo.clone();
        }
        if (locationAo != null){
            cloned.locationAo = locationAo.clone();
        }
        return cloned;
    }
}
