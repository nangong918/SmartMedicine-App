package com.czy.medicine.service.impl;

import com.api.mapper.medicine.DoctorMapper;
import com.api.mapper.medicine.DoctorRegisterAppointmentMapper;
import com.czy.api.constant.ErrorConstant;
import com.czy.api.converter.domain.medicine.RegisterAppointmentDoctorCardConverter;
import com.czy.api.domain.Do.medicine.DoctorRegisterAppointmentDo;
import com.czy.api.domain.ao.LocationAo;
import com.czy.api.domain.ao.medicine.HospitalAo;
import com.czy.api.domain.ao.medicine.RegisterAppointmentSelectAo;
import com.czy.api.domain.bo.medicine.RegisterAppointmentDoctorCardBo;
import com.czy.api.domain.vo.medicine.DoctorVo;
import com.czy.api.domain.vo.medicine.RegisterAppointmentDataVo;
import com.czy.api.domain.vo.medicine.RegisterAppointmentDoctorCardVo;
import com.czy.api.domain.vo.medicine.RegisterAppointmentPageVo;
import com.czy.medicine.service.RegisterAppointmentService;
import com.utils.minio.service.OssService;
import date.DateUtils;
import domain.FileResAo;
import exception.AppException;
import location.GeoUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author 13225
 * @date 2025/8/18 16:12
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class RegisterAppointmentServiceImpl implements RegisterAppointmentService {

    private final DoctorMapper doctorMapper;
    private final DoctorRegisterAppointmentMapper doctorRegisterAppointmentMapper;
    private final RegisterAppointmentDoctorCardConverter registerAppointmentDoctorCardConverter;
    private final OssService ossService;

    // 获取PageList
    @NotNull
    public RegisterAppointmentPageVo getPage(@NotNull RegisterAppointmentSelectAo ao) throws AppException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime registerTime;
        try {
            registerTime = DateUtils.getLocalDateTime(ao.getRegisterTime(), formatter);
        } catch (Exception e) {
            String errorMessage = "时间转换错误, timeStr: " + ao.getRegisterTime();
            log.error(errorMessage, e);
            throw new AppException(errorMessage, e);
        }

        RegisterAppointmentPageVo pageVo = new RegisterAppointmentPageVo();

        // 获取可挂号的记录列表
        List<DoctorRegisterAppointmentDo> doctorRegisterAppointmentDos = getDoctorRegisterAppointmentDo(
                ao.registerLocation,
                registerTime,
                ao.registerDepartmentCode,
                ao.registerSubjectCode
        );

        // dataVo
        RegisterAppointmentDataVo dataVo = getDataVo(doctorRegisterAppointmentDos, ao.getRegisterTime());
        pageVo.setDataVo(dataVo);

        // cardVos
        List<RegisterAppointmentDoctorCardVo> cardVos = getDoctorCardVo(doctorRegisterAppointmentDos);
        pageVo.setCardVos(cardVos);

        if (CollectionUtils.isEmpty(cardVos)){
            return pageVo;
        }

        /// 数据填充（数据库查询出来的数据继续计算）
        // 距离填充
        if (ao.getLatitude() == null || ao.getLongitude() == null){
            for (RegisterAppointmentDoctorCardVo cardVo : pageVo.getCardVos()){
                // 设置值是错误值
                cardVo.setDistance(-1.0);
            }
        }
        else {
            for (RegisterAppointmentDoctorCardVo cardVo : pageVo.getCardVos()){
                Double longitude = Optional.ofNullable(cardVo)
                        .map(RegisterAppointmentDoctorCardVo::getHospitalAo)
                        .map(HospitalAo::getLongitude)
                        .orElse(null);
                Double latitude = Optional.ofNullable(cardVo)
                        .map(RegisterAppointmentDoctorCardVo::getHospitalAo)
                        .map(HospitalAo::getLatitude)
                        .orElse(null);
                if (longitude != null && latitude != null){
                    double distance = GeoUtils.calculateDistance(
                            latitude, longitude,
                            cardVo.getHospitalAo().getLatitude(),
                            cardVo.getHospitalAo().getLongitude()
                    );
                    cardVo.setDistance(distance);
                }
            }
        }

        // oss填充
        List<Long> doctorAvatarFileIds = cardVos.stream()
                .map(RegisterAppointmentDoctorCardVo::getDoctorVo)
                .map(DoctorVo::getDoctorAvatarFileAo)
                .map(FileResAo::getFileId)
                .collect(Collectors.toList());

        List<String> fileUrls = ossService.getFileUrlsByFileIds(doctorAvatarFileIds);

        for (int i = 0; i < cardVos.size(); i++){
            RegisterAppointmentDoctorCardVo cardVo = cardVos.get(i);
            cardVo.getDoctorVo().getDoctorAvatarFileAo().setFileUrl(fileUrls.get(i));
        }

        return pageVo;
    }

    @NotNull
    public List<DoctorRegisterAppointmentDo> getDoctorRegisterAppointmentDo(
            @NotNull LocationAo registerLocation,
            @NotNull LocalDateTime registerDate,
            @NotNull Integer registerDepartmentCode,
            @NotNull Integer registerSubjectCode
            ){
        return doctorRegisterAppointmentMapper.getDosByParam(
                registerLocation,
                registerDate,
                registerDepartmentCode,
                registerSubjectCode
        );
    }

    // 获取DataVo
    private RegisterAppointmentDataVo getDataVo
            (List<DoctorRegisterAppointmentDo> dos, String dateStr){
        RegisterAppointmentDataVo dataVo = new RegisterAppointmentDataVo();
        dataVo.setData(dateStr);

        if (CollectionUtils.isEmpty(dos)){
            dataVo.setRemainCount(0);
            dataVo.setMinCost(ErrorConstant.NULL_STRING);
            return dataVo;
        }

        int allRemainCount = 0;
        BigDecimal minCost = null;
        for (DoctorRegisterAppointmentDo doctorRegisterAppointmentDo : dos){
            int remainCount = doctorRegisterAppointmentDo.getRemainCount();
            allRemainCount += remainCount;

            // 初始化 minCost
            if (minCost == null || (minCost.compareTo(doctorRegisterAppointmentDo.getCost()) > 0)) {
                minCost = doctorRegisterAppointmentDo.getCost();
            }
        }

        dataVo.setRemainCount(allRemainCount);
        // 设置 minCost
        dataVo.setMinCost(
                Optional.ofNullable(minCost)
                        .map(BigDecimal::toPlainString)
                        .orElse(ErrorConstant.NULL_STRING)
        );

        return dataVo;
    }

    public List<RegisterAppointmentDoctorCardVo> getDoctorCardVo
            (@NotNull List<DoctorRegisterAppointmentDo> dos){
        if (CollectionUtils.isEmpty(dos)){
            return new ArrayList<>();
        }

        // 用dos批量查询 -> do; 避免逐个查询产生多余的io (Mybatis不会添加null对象)
        List<RegisterAppointmentDoctorCardBo> bos = doctorRegisterAppointmentMapper.getDoctorCardBosByDos(
                dos
        );
        if (CollectionUtils.isEmpty(bos)){
            return new ArrayList<>();
        }

        // converter: bo -> vo
        return registerAppointmentDoctorCardConverter.bosToVos(bos);
    }

    // 获取DataVoList
    public List<RegisterAppointmentDataVo> getDataVoList(@NotNull RegisterAppointmentSelectAo ao) throws AppException{
        if (!StringUtils.hasText(ao.getRegisterTime())){
            return new ArrayList<>();
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime registerTime;
        try {
            registerTime = DateUtils.getLocalDateTime(ao.getRegisterTime(), formatter);
        } catch (Exception e) {
            String errorMessage = "时间转换错误, timeStr: " + ao.getRegisterTime();
            log.error(errorMessage, e);
            throw new AppException(errorMessage, e);
        }

        // 获取今天~3天后挂号列表
        LocalDateTime[] registerDates = new LocalDateTime[]{
                // 今天
                registerTime,
                // 明天
                registerTime.plusDays(1),
                // 后天
                registerTime.plusDays(2),
                // 3天后
                registerTime.plusDays(3),
        };

        // 获取数据
        List<RegisterAppointmentDataVo> dataVos = new ArrayList<>();
        for (LocalDateTime registerDate : registerDates) {
            // 获取可挂号的记录列表
            List<DoctorRegisterAppointmentDo> doctorRegisterAppointmentDos = getDoctorRegisterAppointmentDo(
                    ao.registerLocation,
                    registerDate,
                    ao.registerDepartmentCode,
                    ao.registerSubjectCode
            );

            // dataVo
            RegisterAppointmentDataVo dataVo = getDataVo(doctorRegisterAppointmentDos, ao.getRegisterTime());
            dataVos.add(dataVo);
        }
        return dataVos;
    }
}
