package com.czy.medicine.service;

import com.czy.api.domain.Do.medicine.DoctorMerchantAppointmentDo;
import com.czy.api.domain.ao.medicine.AppointmentDoctorOrderListAo;
import com.czy.api.domain.ao.medicine.RegisterAppointmentDoctorCardAo;
import com.czy.api.domain.ao.medicine.AppointmentDoctorSelectAo;
import com.czy.api.domain.dto.mq.AppointmentPayResultDto;
import com.czy.api.domain.vo.medicine.AppointmentDoctorDataVo;
import com.czy.api.domain.vo.medicine.AppointmentDoctorPageVo;
import com.utils.redisson.service.RedissonClusterLock;
import exception.AppException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author 13225
 * @date 2025/8/18 16:12
 */
public interface AppointmentDoctorService {
    /**
     * 获取PageList   （某天的日期vo 和 医生卡片voList）
     * @param ao            查询参数
     * @return              PageList
     * @throws AppException 错误
     */
    @NotNull AppointmentDoctorPageVo getPage(@NotNull AppointmentDoctorSelectAo ao) throws AppException;

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
    @NotNull List<AppointmentDoctorDataVo> getDataVoList(@NotNull AppointmentDoctorSelectAo ao) throws AppException;

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

    /**
     * 生成订单缓存
     * 缓存功能: 1. user查询订单状态 2. 后端检查是否重复预约
     * 缓存数据结构: AppointmentDoctorOrderListAo
     * 缓存存储方式: ZSet 有序集合
     * 订单Id生成: 在加入消息队列之前先生成订单id然后缓存到Redis避免找不到; see: getAppointmentRecordList
     * @param doctorMerchantAppointmentId   预约id
     * @param userId                        用户id
     * @param orderId                       订单id
     * @param appointmentLock               预约锁
     * @throws AppException                 预约异常
     */
    void generateOrderCache(@NotNull Long doctorMerchantAppointmentId, @NotNull Long userId, long orderId, @NotNull RedissonClusterLock appointmentLock) throws AppException;

    // 获取user预约订单列表
    @NotNull List<AppointmentDoctorOrderListAo> getAppointmentRecordList(@NotNull Long userId, int sortType,
                                                                         @Nullable Double userLongitude, @Nullable Double userLatitude) throws AppException;

    void handlePayResultMessage(@NotNull AppointmentPayResultDto dto);

    /**
     * 检查用户是否预约此商户并且拥有有效订单
     * @param userId                        用户id
     * @param doctorMerchantAppointmentId   商户预约id
     * @return                              true:存在有效订单
     */
    boolean checkIsUserEffectiveAppointmentExist(@NotNull Long userId, @NotNull Long doctorMerchantAppointmentId);
}
