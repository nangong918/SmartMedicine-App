package com.api.mapper.medicine.redis;

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
public interface AppointmentDoctorOrderRedisMapper {

    /// AppointmentDoctorOrderListAo

    // 查询user-merchant
    AppointmentDoctorOrderListAo getAppointmentDoctorOrderListAoByMerchantId(
            @NotNull Long userId,
            @NotNull Long doctorMerchantAppointmentId
    );

    @Nullable AppointmentDoctorOrderListAo getAppointmentDoctorOrderListAoByOrderId(
            @NotNull Long userId,
            @NotNull Long orderId
    );

    boolean updateAppointmentDoctorOrderListAoStatus(
            @NotNull Long userId,
            @NotNull Long orderId,
            @NotNull Integer status
    );

    @NotNull List<AppointmentDoctorOrderListAo> getAllAppointmentRecordList(@NotNull Long userId);

    void deleteAppointmentDoctorOrderListAo(
            @NotNull Long userId,
            int sortType
    );

    void deleteAllAppointmentDoctorOrderListAo(
            @NotNull Long userId
    );

    boolean saveAppointmentDoctorOrderListAo(@NotNull Long userId, @NotNull List<AppointmentDoctorOrderListAo> aoList) throws AppException;

    // 单个存储, 在创建订单的时候存储
    boolean saveSingleAppointmentDoctorOrderListAo(@NotNull Long userId, @NotNull AppointmentDoctorOrderListAo ao) throws AppException;

    // 单个删除, 在取消订单和支付超时的时候存储
    void deleteSingleAppointmentDoctorOrderListAo(@NotNull Long userId, @NotNull AppointmentDoctorOrderListAo ao);

    @Nullable List<AppointmentDoctorOrderListAo> getAppointmentRecordList(@NotNull Long userId, int sortType, @Nullable Double userLongitude, @Nullable Double userLatitude) throws AppException;
}
