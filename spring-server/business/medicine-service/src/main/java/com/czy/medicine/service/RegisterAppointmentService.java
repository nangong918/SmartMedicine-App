package com.czy.medicine.service;

import com.czy.api.domain.Do.medicine.DoctorMerchantAppointmentDo;
import com.czy.api.domain.ao.medicine.RegisterAppointmentDoctorCardAo;
import com.czy.api.domain.ao.medicine.RegisterAppointmentSelectAo;
import com.czy.api.domain.vo.medicine.RegisterAppointmentDataVo;
import com.czy.api.domain.vo.medicine.RegisterAppointmentPageVo;
import com.utils.redisson.service.RedissonClusterLock;
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
     * 获取DoctorCardAo(Vo)
     * @param dos   DoctorRegisterAppointmentDo
     * @return      DoctorCardVo
     */
    @NotNull List<RegisterAppointmentDoctorCardAo> getDoctorCardAo
            (@NotNull List<DoctorMerchantAppointmentDo> dos);

    /**
     * 获取四天的日期vo
     * @param ao                RegisterAppointmentSelectAo
     * @return                  List<RegisterAppointmentDataVo>
     * @throws AppException     AppException
     */
    @NotNull List<RegisterAppointmentDataVo> getDataVoList(@NotNull RegisterAppointmentSelectAo ao) throws AppException;

    /**
     * 预约
     * @param doctorMerchantAppointmentId   医生商户id
     * @param userId                        用户id
     * @param orderId                       订单id        (因为此方法是消息队列监听者异步调用的，
     * 但是在此之前user不能没有订单id信息，不然就找不到订单了，
     * 所以由上游创建订单id，如果此处处理失败了，就将redis的数据清除)
     * @throws AppException                 预约失败的异常
     */
    void appointment(
            @NotNull Long doctorMerchantAppointmentId, @NotNull Long userId, long orderId) throws AppException;

    // 生成订单缓存
    void generateOrderCache(@NotNull Long doctorMerchantAppointmentId, @NotNull Long userId, long orderId, @NotNull RedissonClusterLock appointmentLock) throws AppException;
}
