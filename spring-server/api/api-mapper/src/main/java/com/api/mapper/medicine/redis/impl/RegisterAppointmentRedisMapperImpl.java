package com.api.mapper.medicine.redis.impl;

import com.api.mapper.medicine.redis.RegisterAppointmentRedisMapper;
import com.czy.api.constant.medicine.AppointmentSortTypeEnum;
import com.czy.api.constant.medicine.MedicineRedisKey;
import com.czy.api.domain.Do.medicine.DoctorMerchantAppointmentDo;
import com.czy.api.domain.ao.medicine.AppointmentDoctorOrderListAo;
import com.czy.api.exception.CommonExceptions;
import com.czy.api.exception.MedicineExceptions;
import com.utils.redisson.service.RedissonService;
import exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author 13225
 * @date 2025/8/21 13:43
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class RegisterAppointmentRedisMapperImpl implements RegisterAppointmentRedisMapper {

    private final RedissonService redissonService;
    private final RedissonClient redissonClient;

    /**
     * 缓存预约申请信息
     * @param userId                        用户id
     * @param doctorMerchantAppointmentId   医生商户预约id
     * @param orderId                       订单id
     * @param ao                            预约信息
     * @return                              缓存结果
     */
    @Override
    public boolean saveAppointmentDoctorOrderListAo
    (@NotNull Long userId, @NotNull Long doctorMerchantAppointmentId,
     @NotNull Long orderId, @NotNull AppointmentDoctorOrderListAo ao){
        String keyBuilder = MedicineRedisKey.Appointment.appointmentOrder_KEY_PREFIX +
                userId + ":" +
                doctorMerchantAppointmentId + ":" +
                orderId + ":";
        return redissonService.setObjectByJson(
                keyBuilder,
                ao,
                MedicineRedisKey.Appointment.appointmentOrder_EXPIRE_TIME
        );
    }

    // 全数据查询单个
    @Override
    public AppointmentDoctorOrderListAo getAppointmentDoctorOrderListAo(
            @NotNull Long userId,
            @NotNull Long doctorMerchantAppointmentId,
            @NotNull Long orderId
    ){
        String keyBuilder = MedicineRedisKey.Appointment.appointmentOrder_KEY_PREFIX +
                userId + ":" +
                doctorMerchantAppointmentId + ":" +
                orderId + ":";
        return redissonService.getObjectFromJson(
                keyBuilder,
                AppointmentDoctorOrderListAo.class
        );
    }

    @Override
    public boolean saveDoctorMerchantAppointmentDo(@NotNull DoctorMerchantAppointmentDo doctorMerchantAppointmentDo) {
        if (doctorMerchantAppointmentDo.getId() == null){
            return false;
        }
        String keyBuilder = MedicineRedisKey.Appointment.DoctorMerchant_KEY_PREFIX +
                doctorMerchantAppointmentDo.getId();
        return redissonService.setObjectByJson(keyBuilder, doctorMerchantAppointmentDo,
                MedicineRedisKey.Appointment.DoctorMerchant_EXPIRE_TIME);
    }

    @Override
    public DoctorMerchantAppointmentDo getDoctorMerchantAppointmentDo(@NotNull Long doctorMerchantAppointmentId) {
        String keyBuilder = MedicineRedisKey.Appointment.DoctorMerchant_KEY_PREFIX +
                doctorMerchantAppointmentId;
        return redissonService.getObjectFromJson(keyBuilder, DoctorMerchantAppointmentDo.class);
    }

    @Override
    public boolean deleteDoctorMerchantAppointmentDo(@NotNull Long doctorMerchantAppointmentId) {
        String keyBuilder = MedicineRedisKey.Appointment.DoctorMerchant_KEY_PREFIX +
                doctorMerchantAppointmentId;
        return redissonService.deleteObject(keyBuilder);
    }

    /// List<AppointmentDoctorOrderListAo> 用户预约订单
    /**
     * 获取用户预约记录
     * @param userId        用户id
     * @param sortType      排序方式
     * @see AppointmentSortTypeEnum
     * @param userLongitude 用户经度
     * @param userLatitude  用户纬度
     * @return              预约记录
     * @throws AppException 错误
     */
    @Nullable
    @Override
    public List<AppointmentDoctorOrderListAo> getAppointmentRecordList(@NotNull Long userId, int sortType, @Nullable Double userLongitude, @Nullable Double userLatitude) throws AppException {
        String keyBuilder = MedicineRedisKey.Appointment.AppointmentDoctorOrderListAoList_KEY_PREFIX + userId + ":" + sortType;
        // 不存在key返回null; null和empty是两个概念, 一个是缓存未命中, 一个是缓存命中，但结果为空
        if (!redissonService.hasKey(keyBuilder)){
            return null;
        }

        // 获取排序方法
        AppointmentSortTypeEnum sortTypeEnum = AppointmentSortTypeEnum.getByCode(sortType);
        List<Object> allItems = new ArrayList<>();

        // 排序方式
        switch (sortTypeEnum){
            // 默认, 时间: 从大到小
            case DEFAULT:
            case TIME:{
                // 时间是有序Set
                allItems = getZSetList(keyBuilder);
                allItems.sort((o1, o2) -> {
                    LocalDateTime beginDate1 = ((AppointmentDoctorOrderListAo) o1).getBeginDate();
                    LocalDateTime beginDate2 = ((AppointmentDoctorOrderListAo) o2).getBeginDate();
                    return beginDate2.compareTo(beginDate1);
                });
                break;
            }
            // 距离, 花费: 从小到大
            case DISTANCE:{
                if (userLatitude == null || userLongitude == null){
                    // 通知前端; 根据前端的code: MD_10005 来提示用户给予定位权限
                    throw new AppException(MedicineExceptions.LOCATION_NOT_NULL);
                }
                // 距离是无序Set
                allItems = getSetList(keyBuilder);
                allItems.sort((o1, o2) -> {
                    double distance1 = ((AppointmentDoctorOrderListAo) o1).getDistance(
                            userLongitude, userLatitude
                    );
                    double distance2 = ((AppointmentDoctorOrderListAo) o2).getDistance(
                            userLongitude, userLatitude
                    );
                    // 比较距离
                    int comparison = Double.compare(distance1, distance2);

                    // 如果距离相等，返回 0；可以添加额外的次要排序逻辑，例如按时间排序
                    if (comparison == 0) {
                        LocalDateTime beginDate1 = ((AppointmentDoctorOrderListAo) o1).getBeginDate();
                        LocalDateTime beginDate2 = ((AppointmentDoctorOrderListAo) o2).getBeginDate();
                        return beginDate1.compareTo(beginDate2); // 次要排序：按时间升序
                    }

                    return comparison; // 返回距离比较结果
                });
                break;
            }
            case COST:{
                // 花费是有序Set
                allItems = getZSetList(keyBuilder);
                allItems.sort((o1, o2) -> {
                    BigDecimal cost1 = ((AppointmentDoctorOrderListAo) o1).getCost();
                    BigDecimal cost2 = ((AppointmentDoctorOrderListAo) o2).getCost();

                    // 处理 null 的情况
                    if (cost1 == null && cost2 == null) {
                        return 0; // 两者均为 null，视为相等
                    }
                    else if (cost1 == null) {
                        return 1; // cost1 为 null，排到后面
                    }
                    else if (cost2 == null) {
                        return -1; // cost2 为 null，排到后面
                    }

                    // 两者均不为 null，正常比较
                    return cost1.compareTo(cost2);
                });
                break;
            }
            default:
                throw new AppException(CommonExceptions.SORT_TYPE_NOT_FOUND);
        }

        // 类型转换
        // 收集到列表中
        return allItems.stream()
                .map(item -> (AppointmentDoctorOrderListAo) item)
                .collect(Collectors.toList());
    }

    private List<Object> getZSetList(String key) {
        RScoredSortedSet<Object> zSet = redissonClient.getScoredSortedSet(key);
        List<Object> allItems = new ArrayList<>(zSet.readAll());
        if (CollectionUtils.isEmpty(allItems)){
            return new ArrayList<>();
        }

        // 过滤null
        return allItems.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    private List<Object> getSetList(String key) {
        RSet<Object> set = redissonClient.getSet(key);
        List<Object> allItems = new ArrayList<>(set.readAll());
        if (CollectionUtils.isEmpty(allItems)){
            return new ArrayList<>();
        }

        // 过滤null
        return allItems.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * 删除全部缓存list
     * @param userId        用户id
     * @param sortType      排序类型
     */
    @Override
    public void deleteAppointmentDoctorOrderListAo(
            @NotNull Long userId,
            int sortType
    ) {
        String keyBuilder = MedicineRedisKey.Appointment.AppointmentDoctorOrderListAoList_KEY_PREFIX + userId + ":" + sortType;

        if (!redissonService.hasKey(keyBuilder)){
            return;
        }

        redissonService.removeSet(keyBuilder);
    }

    /**
     * 保存预约
     * @param userId                        用户id
     * @param aoList                        预约
     * @return                              是否成功
     * @throws AppException                 保存失败的异常
     */
    @Override
    public boolean saveAppointmentDoctorOrderListAo(@NotNull Long userId, @NotNull List<AppointmentDoctorOrderListAo> aoList) throws AppException{
        String timeKeyBuilder = MedicineRedisKey.Appointment.AppointmentDoctorOrderListAoList_KEY_PREFIX + userId + ":" + AppointmentSortTypeEnum.TIME.getCode();
        String distanceKeyBuilder = MedicineRedisKey.Appointment.AppointmentDoctorOrderListAoList_KEY_PREFIX + userId + ":" + AppointmentSortTypeEnum.DISTANCE.getCode();
        String costKeyBuilder = MedicineRedisKey.Appointment.AppointmentDoctorOrderListAoList_KEY_PREFIX + userId + ":" + AppointmentSortTypeEnum.COST.getCode();

        /// 存储的list是空 -> 清空缓存
        if (aoList.isEmpty()){
            redissonClient.getKeys().delete(timeKeyBuilder, distanceKeyBuilder, costKeyBuilder);
            return true;
        }

        /// 创建有序集合
        // time
        RScoredSortedSet<AppointmentDoctorOrderListAo> timeZSet = redissonClient.getScoredSortedSet(timeKeyBuilder);
        timeZSet.expire(MedicineRedisKey.Appointment.AppointmentDoctorOrderListAoList_EXPIRE_TIME, TimeUnit.SECONDS);
        for (AppointmentDoctorOrderListAo ao : aoList){
            // 获取评分，可以选择时间、距离或成本等
            LocalDateTime beginDate = ao.getBeginDate();
            // 默认时区
            long timestamp = beginDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

            // 将对象存入有序集合
            if (!timeZSet.add((double)timestamp, ao)){
                log.error("[存储用户{}time订单数据到缓存失败]添加订单{}失败", userId, ao.getOrderId());
                throw new AppException(CommonExceptions.SYSTEM_REDIS_ERROR);
            }
        }

        // distance (普通set, 避免刻舟求剑)
        RSet<AppointmentDoctorOrderListAo> distanceSet = redissonClient.getSet(distanceKeyBuilder);
        distanceSet.expire(MedicineRedisKey.Appointment.AppointmentDoctorOrderListAoList_EXPIRE_TIME, TimeUnit.SECONDS);
        if (!distanceSet.addAll(aoList)){
            log.error("[存储用户{}distance订单数据到缓存失败]添加订单失败", userId);
            throw new AppException(CommonExceptions.SYSTEM_REDIS_ERROR);
        }

        // cost
        RScoredSortedSet<AppointmentDoctorOrderListAo> costZSet = redissonClient.getScoredSortedSet(costKeyBuilder);
        costZSet.expire(MedicineRedisKey.Appointment.AppointmentDoctorOrderListAoList_EXPIRE_TIME, TimeUnit.SECONDS);
        for (AppointmentDoctorOrderListAo ao : aoList) {
            // 获取价格
            BigDecimal cost = ao.getCost();

            double score;

            if (cost == null) {
                score = Double.MAX_VALUE; // 使用最大值表示缺失的成本
            }
            else {
                score = cost.doubleValue(); // 将成本转换为 double
            }

            // 将对象存入 ZSet
            if (!costZSet.add(score, ao)){
                log.error("[存储用户{}cost订单数据到缓存失败]添加订单失败", userId);
                throw new AppException(CommonExceptions.SYSTEM_REDIS_ERROR);
            }
        }

        return true;
    }

    /**
     * 存储用户预约订单列表
     * @param userId    用户id
     * @param ao        预约订单列表
     * @return          存储结果
     */
    @Override
    public boolean saveSingleAppointmentDoctorOrderListAo(@NotNull Long userId, @NotNull AppointmentDoctorOrderListAo ao) throws AppException{
        String timeKeyBuilder = MedicineRedisKey.Appointment.AppointmentDoctorOrderListAoList_KEY_PREFIX + userId + ":" + AppointmentSortTypeEnum.TIME.getCode();
        String distanceKeyBuilder = MedicineRedisKey.Appointment.AppointmentDoctorOrderListAoList_KEY_PREFIX + userId + ":" + AppointmentSortTypeEnum.DISTANCE.getCode();
        String costKeyBuilder = MedicineRedisKey.Appointment.AppointmentDoctorOrderListAoList_KEY_PREFIX + userId + ":" + AppointmentSortTypeEnum.COST.getCode();

        // 创建时间有序集合
        RScoredSortedSet<AppointmentDoctorOrderListAo> timeZSet = redissonClient.getScoredSortedSet(timeKeyBuilder);
        // 过期时间
        if (!timeZSet.isExists()){
            timeZSet.expire(MedicineRedisKey.Appointment.AppointmentDoctorOrderListAoList_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        LocalDateTime beginDate = ao.getBeginDate();
        long timestamp = beginDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        if (!timeZSet.add((double) timestamp, ao)) {
            log.error("[存储用户{}时间订单数据到缓存失败]添加订单{}失败", userId, ao.getOrderId());
            throw new AppException(CommonExceptions.SYSTEM_REDIS_ERROR);
        }

        // 存储距离到普通集合
        RSet<AppointmentDoctorOrderListAo> distanceSet = redissonClient.getSet(distanceKeyBuilder);
        if (!distanceSet.isExists()){
            distanceSet.expire(MedicineRedisKey.Appointment.AppointmentDoctorOrderListAoList_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        if (!distanceSet.add(ao)) {
            log.error("[存储用户{}距离订单数据到缓存失败]添加订单失败", userId);
            throw new AppException(CommonExceptions.SYSTEM_REDIS_ERROR);
        }

        // 创建成本有序集合
        RScoredSortedSet<AppointmentDoctorOrderListAo> costZSet = redissonClient.getScoredSortedSet(costKeyBuilder);
        if (!costZSet.isExists()){
            costZSet.expire(MedicineRedisKey.Appointment.AppointmentDoctorOrderListAoList_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        BigDecimal cost = ao.getCost();
        double score = (cost == null) ? Double.MAX_VALUE : cost.doubleValue();

        if (!costZSet.add(score, ao)) {
            log.error("[存储用户{}成本订单数据到缓存失败]添加订单失败", userId);
            throw new AppException(CommonExceptions.SYSTEM_REDIS_ERROR);
        }

        return true;
    }

    /**
     * 删除用户的指定预约订单
     * @param userId 用户id
     * @param ao     要删除的预约订单对象
     */
    @Override
    public void deleteSingleAppointmentDoctorOrderListAo(@NotNull Long userId, @NotNull AppointmentDoctorOrderListAo ao) {
        String timeKeyBuilder = MedicineRedisKey.Appointment.AppointmentDoctorOrderListAoList_KEY_PREFIX + userId + ":" + AppointmentSortTypeEnum.TIME.getCode();
        String distanceKeyBuilder = MedicineRedisKey.Appointment.AppointmentDoctorOrderListAoList_KEY_PREFIX + userId + ":" + AppointmentSortTypeEnum.DISTANCE.getCode();
        String costKeyBuilder = MedicineRedisKey.Appointment.AppointmentDoctorOrderListAoList_KEY_PREFIX + userId + ":" + AppointmentSortTypeEnum.COST.getCode();

        // 删除时间有序集合中的指定订单
        RScoredSortedSet<AppointmentDoctorOrderListAo> timeZSet = redissonClient.getScoredSortedSet(timeKeyBuilder);
        if (timeZSet.isExists()) {
            timeZSet.remove(ao);
        }

        // 删除距离集合中的指定订单
        RSet<AppointmentDoctorOrderListAo> distanceSet = redissonClient.getSet(distanceKeyBuilder);
        if (distanceSet.isExists()) {
            distanceSet.remove(ao);
        }

        // 删除成本有序集合中的指定订单
        RScoredSortedSet<AppointmentDoctorOrderListAo> costZSet = redissonClient.getScoredSortedSet(costKeyBuilder);
        if (costZSet.isExists()) {
            costZSet.remove(ao);
        }
    }
}
