package com.czy.api.domain.dto.socket.response;


import com.czy.api.constant.user_relationship.newUserGroup.ApplyStatusEnum;
import com.czy.api.domain.dto.base.BaseResponseData;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * @author 13225
 * @date 2025/3/10 17:26
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DeleteUserResponse extends BaseResponseData {
    public final Integer applyStatus = ApplyStatusEnum.DELETED.code;
    @Override
    public Map<String, String> toDataMap() {
        Map<String, String> map = super.toDataMap();
        map.put("applyStatus", String.valueOf(applyStatus));
        return map;
    }
}
