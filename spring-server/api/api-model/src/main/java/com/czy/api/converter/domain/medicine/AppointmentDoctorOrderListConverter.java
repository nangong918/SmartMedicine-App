package com.czy.api.converter.domain.medicine;

import com.czy.api.domain.ao.medicine.AppointmentDoctorOrderDetailsAo;
import com.czy.api.domain.ao.medicine.AppointmentDoctorOrderListAo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

/**
 * @author 13225
 * @date 2025/9/11 18:18
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AppointmentDoctorOrderListConverter {

    AppointmentDoctorOrderListConverter INSTANCE = Mappers.getMapper(AppointmentDoctorOrderListConverter.class);

    // listAo -> detailsAo
    default AppointmentDoctorOrderDetailsAo toDetailsAo(AppointmentDoctorOrderListAo listAo){
        AppointmentDoctorOrderDetailsAo detailsAo = new AppointmentDoctorOrderDetailsAo();
        detailsAo.orderId = listAo.orderId;
        detailsAo.doctorMerchantId = listAo.doctorMerchantId;
        detailsAo.detailsVo.setListVo(listAo.listVo);
        return detailsAo;
    }

}
