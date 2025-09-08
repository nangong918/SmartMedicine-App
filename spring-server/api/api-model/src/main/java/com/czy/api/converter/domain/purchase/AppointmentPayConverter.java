package com.czy.api.converter.domain.purchase;

import com.czy.api.domain.dto.mq.AppointmentOrderDto;
import com.czy.api.domain.dto.mq.AppointmentPayResultDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

/**
 * @author 13225
 * @date 2025/8/26 15:55
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AppointmentPayConverter {

    // INSTANCE
    AppointmentPayConverter INSTANCE = Mappers.getMapper(AppointmentPayConverter.class);

    // Order -> PayResult
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "orderId", target = "orderId")
    @Mapping(source = "orderStatusEnum", target = "orderStatusEnum")
    AppointmentPayResultDto orderToPayResult_(AppointmentOrderDto order);

    default AppointmentPayResultDto orderToPayResult(AppointmentOrderDto order, String handleTimeStr){
        AppointmentPayResultDto result = orderToPayResult_(order);
        result.setHandleTimeStr(handleTimeStr);
        return result;
    }
}
