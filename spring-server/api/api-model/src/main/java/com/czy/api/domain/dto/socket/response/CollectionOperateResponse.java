package com.czy.api.domain.dto.socket.response;

import com.czy.api.domain.dto.base.NettyOptionResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * @author 13225
 * @date 2025/7/28 18:43
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CollectionOperateResponse extends NettyOptionResponse {
    private Long collectionFolderId;

    @Override
    public Map<String, String> toDataMap() {
        Map<String, String> map = super.toDataMap();
        map.put("collectionFolderId", String.valueOf(collectionFolderId));
        return map;
    }
}
