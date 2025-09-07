package com.czy.api.domain.dto.socket.response;

import com.czy.api.constant.netty.ResponseMessageType;
import com.czy.api.domain.dto.base.BaseResponseData;
import json.BaseBean;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * @author 13225
 * @date 2025/8/21 10:16
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AppointmentResultResponse extends BaseResponseData implements BaseBean {
    public Long orderId;
    public Boolean isSuccess = false;

    public AppointmentResultResponse() {
        super();
        this.setType(
                ResponseMessageType.Appointment.APPOINTMENT_RESULT
        );
    }

    @Override
    public Map<String, String> toDataMap() {
        Map<String, String> map = super.toDataMap();
        map.put("orderId", String.valueOf(orderId));
        map.put("isSuccess", String.valueOf(isSuccess));
        return map;
    }
}
