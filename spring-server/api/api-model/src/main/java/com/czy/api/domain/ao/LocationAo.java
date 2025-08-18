package com.czy.api.domain.ao;

import lombok.Data;

/**
 * @author 13225
 * @date 2025/8/18 10:35
 * 地区ao
 * 地区Ao使用String是为了解耦，如果使用id来代表地区的话，那么还需要再最初让前端查询地区信息，然后前端存储然后交给后端。
 */
@Data
public class LocationAo {
    public String province;
    public String city;
    public String region;
}
