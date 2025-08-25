package com.api.mapper.medicine.redis;

import com.czy.api.domain.Do.medicine.DoctorMerchantAppointmentDo;
import com.czy.api.domain.ao.medicine.AppointmentDoctorOrderListAo;
import exception.AppException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author 13225
 * @date 2025/8/21 13:43
 * redis的缓存对象不应该分开存储, 因为分开存储就面临问题:
 * 1.需要新的dataId对象来专门存储Id
 * 2.设计大量的RedisMapper
 * 3. 缓存击穿之后还得单独调用Mybatis的接口, 然和Mybatis的查询基本是联合查询, 单独查询反而性能差
 * redis设计策略: 在增加和修改的时候可以直接访问数据库, 因为是不可避免的; 但是在查询的时候就要尽量避免使用数据库而是缓存
 *  增加修改的时候可以直接查询数据库,甚至不用缓存
 *  但是查询的时候必须使用缓存
 */
public interface RegisterAppointmentRedisMapper {

    /// AppointmentDoctorOrderListAo

    /**
     * 缓存预约申请信息
     * @param userId                        用户id
     * @param doctorMerchantAppointmentId   医生商户预约id
     * @param orderId                       订单id
     * @param ao                            预约信息
     * @return                              缓存结果
     */
    boolean saveAppointmentDoctorOrderListAo
            (@NotNull Long userId, @NotNull Long doctorMerchantAppointmentId,
             @NotNull Long orderId, @NotNull AppointmentDoctorOrderListAo ao);

    AppointmentDoctorOrderListAo getAppointmentDoctorOrderListAo(
            @NotNull Long userId,
            @NotNull Long doctorMerchantAppointmentId,
            @NotNull Long orderId
    );

    /// DoctorMerchantAppointmentDo
    /**
     * 保存/更新商户信息
     * @param doctorMerchantAppointmentDo   DoctorMerchantAppointmentDo
     * @return                              boolean
     */
    boolean saveDoctorMerchantAppointmentDo(@NotNull DoctorMerchantAppointmentDo doctorMerchantAppointmentDo);

    DoctorMerchantAppointmentDo getDoctorMerchantAppointmentDo(@NotNull Long doctorMerchantAppointmentId);

    boolean deleteDoctorMerchantAppointmentDo(@NotNull Long doctorMerchantAppointmentId);

    @Nullable List<AppointmentDoctorOrderListAo> getAppointmentRecordList(@NotNull Long userId, int sortType, @Nullable Double userLongitude, @Nullable Double userLatitude) throws AppException;

    void deleteAppointmentDoctorOrderListAo(
            @NotNull Long userId,
            int sortType
    );

    boolean saveAppointmentDoctorOrderListAo(@NotNull Long userId, @NotNull List<AppointmentDoctorOrderListAo> aoList) throws AppException;

    boolean saveSingleAppointmentDoctorOrderListAo(@NotNull Long userId, @NotNull AppointmentDoctorOrderListAo ao) throws AppException;

    void deleteSingleAppointmentDoctorOrderListAo(@NotNull Long userId, @NotNull AppointmentDoctorOrderListAo ao);
}
