package com.czy.domain.ao;


import androidx.annotation.NonNull;

import java.io.Serializable;

/**
 * @author 13225
 * @date 2025/8/18 10:35
 * 地区ao
 * 地区Ao使用String是为了解耦，如果使用id来代表地区的话，那么还需要再最初让前端查询地区信息，然后前端存储然后交给后端。
 */
public class LocationAo implements Cloneable , Serializable {
    public String province;
    public String city;
    public String region;

    public LocationAo() {
    }

    public LocationAo(String province, String city, String region) {
        this.province = province;
        this.city = city;
        this.region = region;
    }

    @NonNull
    @Override
    public LocationAo clone() throws CloneNotSupportedException {
        return (LocationAo) super.clone();
    }
}
