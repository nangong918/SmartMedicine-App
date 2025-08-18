package com.api.mapper.medicine;

import com.czy.api.domain.Do.medicine.DoctorRegisterAppointmentDo;
import com.czy.api.domain.bo.medicine.RegisterAppointmentDoctorCardBo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author 13225
 * @date 2025/8/18 17:16
 */
@Mapper
public interface DoctorRegisterAppointmentMapper {
    List<RegisterAppointmentDoctorCardBo> getDoctorCardBosByDos(
            List<DoctorRegisterAppointmentDo> dos
    );
}
