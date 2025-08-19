package com.czy.api.converter.domain.medicine;

import com.czy.api.domain.ao.LocationAo;
import com.czy.api.domain.ao.medicine.HospitalAo;
import com.czy.api.domain.bo.medicine.RegisterAppointmentDoctorCardBo;
import com.czy.api.domain.vo.medicine.DoctorVo;
import com.czy.api.domain.vo.medicine.HospitalVo;
import com.czy.api.domain.vo.medicine.RegisterAppointmentDoctorCardVo;
import date.DateUtils;
import domain.FileResAo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 13225
 * @date 2025/8/19 9:52
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RegisterAppointmentDoctorCardConverter {

    RegisterAppointmentDoctorCardConverter INSTANCE = Mappers.getMapper(RegisterAppointmentDoctorCardConverter.class);

    /**
     * bo -> vo
     * 注意：FileResAo中的fileUrl需要计算
     * @param bo     bo
     * @return       vo
     */
    default RegisterAppointmentDoctorCardVo boToVo(RegisterAppointmentDoctorCardBo bo){
        if (bo == null){
            return null;
        }
        RegisterAppointmentDoctorCardVo vo = new RegisterAppointmentDoctorCardVo();
        // DoctorVo
        DoctorVo doctorVo = new DoctorVo();
        FileResAo doctorAvatarFileAo = new FileResAo();
        doctorAvatarFileAo.fileId = bo.getDoctorAvatarFileId();
        doctorVo.setDoctorAvatarFileAo(doctorAvatarFileAo);
        doctorVo.setDoctorName(bo.getDoctorName());
        doctorVo.setDoctorTitle(bo.getDoctorTitle());
        vo.setDoctorVo(doctorVo);

        // HospitalAo
        HospitalAo hospitalAo = new HospitalAo();
        HospitalVo hospitalVo = new HospitalVo();
        hospitalVo.setName(bo.getHospitalName());
        hospitalVo.setLevel(bo.getHospitalLevel());
        hospitalAo.setHospitalVo(hospitalVo);
        LocationAo locationAo = new LocationAo();
        locationAo.setProvince(bo.getLocationProvince());
        locationAo.setCity(bo.getLocationCity());
        locationAo.setRegion(bo.getLocationRegion());
        hospitalAo.setLocationAo(locationAo);
        hospitalAo.setLongitude(bo.getLongitude());
        hospitalAo.setLatitude(bo.getLatitude());
        vo.setHospitalAo(hospitalAo);

        // data
        vo.setRemainCount(bo.getRemainCount());
        vo.setCost(bo.getCost());
        vo.setBeginDate(
                DateUtils.yyyyMMddHHmmssToString(bo.getBeginDate())
        );
        vo.setEndDate(
                DateUtils.yyyyMMddHHmmssToString(bo.getEndDate())
        );

        return vo;
    }

    default List<RegisterAppointmentDoctorCardVo> bosToVos(List<RegisterAppointmentDoctorCardBo> bos){
        if (CollectionUtils.isEmpty(bos)){
            return new ArrayList<>();
        }
        return bos.stream()
                .map(this::boToVo)
                .collect(Collectors.toList());
    }
}
