package com.czy.api.converter.domain.purchase;

import com.czy.api.domain.dto.mq.AppointmentOrderDto;
import com.czy.api.domain.dto.mq.AppointmentPayResultDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

/**
 * @author 13225
 * @date 2025/8/26 15:55
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AppointmentPayConverter {

    // INSTANCE
    AppointmentPayConverter INSTANCE = Mappers.getMapper(AppointmentPayConverter.class);

    // Order -> PayResult
    @Mapping(source = "doctorMerchantAppointmentId", target = "doctorMerchantAppointmentId")
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "orderId", target = "orderId")
    @Mapping(source = "orderStatusEnum", target = "orderStatusEnum")
    AppointmentPayResultDto orderToPayResult_(AppointmentOrderDto order);

    default AppointmentPayResultDto orderToPayResult(AppointmentOrderDto order, LocalDateTime handleTime){
        AppointmentPayResultDto result = orderToPayResult_(order);
        result.setHandleTime(handleTime);
        return result;
    }
}
