package com.czy.api.converter.domain.medicine;

import com.czy.api.domain.ao.LocationAo;
import com.czy.api.domain.ao.medicine.AppointmentDoctorOrderListAo;
import com.czy.api.domain.ao.medicine.HospitalAo;
import com.czy.api.domain.bo.medicine.UserAppointmentOrderBo;
import com.czy.api.domain.vo.medicine.AppointmentDoctorOrderListVo;
import com.czy.api.domain.vo.medicine.DoctorVo;
import com.czy.api.domain.vo.medicine.HospitalVo;
import date.DateUtils;
import domain.FileResAo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author 13225
 * @date 2025/8/25 13:56
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AppointmentDoctorOrderConverter {

    AppointmentDoctorOrderConverter INSTANCE = Mappers.getMapper(AppointmentDoctorOrderConverter.class);

    default AppointmentDoctorOrderListAo getAoByBo(UserAppointmentOrderBo bo, String approveDate) {
        if (bo == null){
            return null;
        }
        AppointmentDoctorOrderListAo targetAo = new AppointmentDoctorOrderListAo();
        AppointmentDoctorOrderListVo targetVo = new AppointmentDoctorOrderListVo();
        targetVo.doctorVo = new DoctorVo();
        targetVo.doctorVo.doctorAvatarFileAo = new FileResAo();
        targetVo.doctorVo.doctorAvatarFileAo.fileId = bo.getDoctorAvatarFileId();
        targetVo.doctorVo.doctorName = bo.getDoctorName();
        targetVo.doctorVo.doctorTitle = bo.getDoctorTitle();

        targetVo.hospitalAo = new HospitalAo();
        targetVo.hospitalAo.hospitalVo = new HospitalVo();
        targetVo.hospitalAo.hospitalVo.name = bo.getHospitalName();
        targetVo.hospitalAo.hospitalVo.level = bo.getHospitalLevel();
        targetVo.hospitalAo.locationAo = new LocationAo();
        targetVo.hospitalAo.locationAo.province = bo.getLocationProvince();
        targetVo.hospitalAo.locationAo.city = bo.getLocationCity();
        targetVo.hospitalAo.locationAo.region = bo.getLocationRegion();
        targetVo.hospitalAo.longitude = bo.getLongitude();
        targetVo.hospitalAo.latitude = bo.getLatitude();

        targetVo.cost = bo.getCost();
        targetVo.beginDate = DateUtils.yyyyMMddHHmmssToString(bo.getBeginDate());
        targetVo.endDate = DateUtils.yyyyMMddHHmmssToString(bo.getEndDate());
        targetVo.approveDate = approveDate;

        targetVo.merchantStatus = bo.getMerchantStatus();
        targetVo.customerStatus = bo.getCustomerStatus();

        targetAo.setListVo(targetVo);
        targetAo.setOrderId(bo.getOrderId());
        targetAo.setDoctorMerchantId(bo.getDoctorMerchantId());
        return targetAo;
    }

    default List<AppointmentDoctorOrderListAo> getAosByBos(List<UserAppointmentOrderBo> bos, String approveDate){
        if (CollectionUtils.isEmpty(bos)){
            return new ArrayList<>();
        }
        return bos.stream()
                .filter(Objects::nonNull)
                .map(bo -> getAoByBo(bo, approveDate))
                .collect(Collectors.toList());
    }


}
