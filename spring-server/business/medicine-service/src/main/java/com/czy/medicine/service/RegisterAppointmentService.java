package com.czy.medicine.service;

import com.czy.api.domain.Do.medicine.DoctorRegisterAppointmentDo;
import com.czy.api.domain.ao.medicine.RegisterAppointmentSelectAo;
import com.czy.api.domain.vo.medicine.RegisterAppointmentDataVo;
import com.czy.api.domain.vo.medicine.RegisterAppointmentDoctorCardVo;
import com.czy.api.domain.vo.medicine.RegisterAppointmentPageVo;
import exception.AppException;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author 13225
 * @date 2025/8/18 16:12
 */
public interface RegisterAppointmentService {
    /**
     * 获取PageList   （某天的日期vo 和 医生卡片voList）
     * @param ao            查询参数
     * @return              PageList
     * @throws AppException 错误
     */
    @NotNull RegisterAppointmentPageVo getPage(@NotNull RegisterAppointmentSelectAo ao) throws AppException;

    /**
     * 获取DoctorCardVo
     * @param dos   DoctorRegisterAppointmentDo
     * @return      DoctorCardVo
     */
    @NotNull List<RegisterAppointmentDoctorCardVo> getDoctorCardVo
            (@NotNull List<DoctorRegisterAppointmentDo> dos);

    /**
     * 获取四天的日期vo
     * @param ao                RegisterAppointmentSelectAo
     * @return                  List<RegisterAppointmentDataVo>
     * @throws AppException     AppException
     */
    @NotNull List<RegisterAppointmentDataVo> getDataVoList(@NotNull RegisterAppointmentSelectAo ao) throws AppException;
}
