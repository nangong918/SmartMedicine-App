package com.czy.medicine.service.impl;

import com.api.mapper.medicine.mybatis.DoctorMerchantAppointmentMapper;
import com.api.mapper.medicine.mybatis.UserCustomerAppointmentOrderMapper;
import com.api.mapper.medicine.mybatis.bo.DoctorMerchantBoMapper;
import com.api.mapper.medicine.redis.RegisterAppointmentRedisMapper;
import com.czy.api.constant.ErrorConstant;
import com.czy.api.constant.UserOrderStatusEnum;
import com.czy.api.constant.medicine.AppointmentMerchantStatusEnum;
import com.czy.api.constant.medicine.MedicineRedisKey;
import com.czy.api.converter.domain.medicine.RegisterAppointmentDoctorCardConverter;
import com.czy.api.domain.Do.medicine.DoctorMerchantAppointmentDo;
import com.czy.api.domain.ao.medicine.AppointmentDoctorOrderListAo;
import com.czy.api.domain.ao.medicine.HospitalAo;
import com.czy.api.domain.ao.medicine.RegisterAppointmentDoctorCardAo;
import com.czy.api.domain.ao.medicine.RegisterAppointmentSelectAo;
import com.czy.api.domain.bo.medicine.RegisterAppointmentDoctorCardBo;
import com.czy.api.domain.vo.medicine.AppointmentDoctorOrderListVo;
import com.czy.api.domain.vo.medicine.DoctorVo;
import com.czy.api.domain.vo.medicine.RegisterAppointmentDataVo;
import com.czy.api.domain.vo.medicine.RegisterAppointmentDoctorCardVo;
import com.czy.api.domain.vo.medicine.RegisterAppointmentPageVo;
import com.czy.api.exception.MedicineExceptions;
import com.czy.medicine.service.RegisterAppointmentService;
import com.czy.medicine.service.transactional.AppointmentTransactionalService;
import com.utils.minio.service.OssService;
import com.utils.redisson.service.RedissonClusterLock;
import com.utils.redisson.service.RedissonService;
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

    private final DoctorMerchantAppointmentMapper doctorMerchantAppointment;
    private final RegisterAppointmentDoctorCardConverter registerAppointmentDoctorCardConverter;
    private final DoctorMerchantBoMapper doctorMerchantBoMapper;
    private final OssService ossService;
    private final UserCustomerAppointmentOrderMapper userCustomerAppointmentOrderMapper;
    private final RedissonService redissonService;
    private final RegisterAppointmentRedisMapper registerAppointmentRedisMapper;
    private final AppointmentTransactionalService appointmentTransactionalService;

    // 获取PageList
    @NotNull
    @Override
    public RegisterAppointmentPageVo getPage(@NotNull RegisterAppointmentSelectAo ao) throws AppException {
        /// 参数校验
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime registerTime;
        try {
            registerTime = DateUtils.getLocalDateTime(ao.getRegisterTime(), formatter);
        } catch (Exception e) {
            String errorMessage = "时间转换错误, timeStr: " + ao.getRegisterTime();
            log.error(errorMessage, e);
            throw new AppException(errorMessage, e);
        }

        /// 数据源
        /*
            首先查询Redis缓存，如果Redis缓存未查询到再查询 Mysql
            由于时间处理会比较特殊，所以redis_key不存储时间参数，而是获取时间参数之外的数据然后计算时间参数
            如果不按照上述方式去处理的话会出现的bug: 每次传入的时间参数都是当前时间，那么每次都找不到缓存不说还会产生大量垃圾缓存。
         */
        StringBuilder keyBuilder = new StringBuilder();

        // 获取可挂号的记录列表
        List<DoctorMerchantAppointmentDo> doctorRegisterAppointmentDos =
                doctorMerchantAppointment.getDosByParam(
                    ao.registerLocation,
                    registerTime,
                    ao.registerDepartmentCode,
                    ao.registerSubjectCode
        );

        RegisterAppointmentPageVo pageVo = new RegisterAppointmentPageVo();
        // dataVo
        RegisterAppointmentDataVo dataVo = getDataVo(doctorRegisterAppointmentDos, ao.getRegisterTime());
        pageVo.setDataVo(dataVo);

        // cardAos
        List<RegisterAppointmentDoctorCardAo> cardAos = getDoctorCardAo(doctorRegisterAppointmentDos);
        pageVo.setCardAos(cardAos);

        if (CollectionUtils.isEmpty(cardAos)){
            return pageVo;
        }

        /// 数据填充（数据库查询出来的数据继续计算）
        // 距离填充
        if (ao.getLatitude() == null || ao.getLongitude() == null){
            for (RegisterAppointmentDoctorCardAo cardAo : pageVo.getCardAos()){
                // 设置值是错误值
                cardAo.getVo().setDistance(-1.0);
            }
        }
        else {
            for (RegisterAppointmentDoctorCardAo cardAo : pageVo.getCardAos()){
                Double longitude = Optional.ofNullable(cardAo)
                        .map(RegisterAppointmentDoctorCardAo::getVo)
                        .map(RegisterAppointmentDoctorCardVo::getHospitalAo)
                        .map(HospitalAo::getLongitude)
                        .orElse(null);
                Double latitude = Optional.ofNullable(cardAo)
                        .map(RegisterAppointmentDoctorCardAo::getVo)
                        .map(RegisterAppointmentDoctorCardVo::getHospitalAo)
                        .map(HospitalAo::getLatitude)
                        .orElse(null);
                if (longitude != null && latitude != null){
                    double distance = GeoUtils.calculateDistance(
                            latitude, longitude,
                            cardAo.getVo().getHospitalAo().getLatitude(),
                            cardAo.getVo().getHospitalAo().getLongitude()
                    );
                    cardAo.getVo().setDistance(distance);
                }
            }
        }

        // oss填充
        List<Long> doctorAvatarFileIds = cardAos.stream()
                .map(RegisterAppointmentDoctorCardAo::getVo)
                .map(RegisterAppointmentDoctorCardVo::getDoctorVo)
                .map(DoctorVo::getDoctorAvatarFileAo)
                .map(FileResAo::getFileId)
                .collect(Collectors.toList());

        List<String> fileUrls = ossService.getFileUrlsByFileIds(doctorAvatarFileIds);

        for (int i = 0; i < cardAos.size(); i++){
            RegisterAppointmentDoctorCardAo cardAo = cardAos.get(i);
            cardAo.getVo().getDoctorVo().getDoctorAvatarFileAo().setFileUrl(fileUrls.get(i));
        }

        return pageVo;
    }

    // 获取DataVo (此do -> vo无需查询数据库, 无需redis缓存)
    private RegisterAppointmentDataVo getDataVo
            (List<DoctorMerchantAppointmentDo> dos, String dateStr){
        RegisterAppointmentDataVo dataVo = new RegisterAppointmentDataVo();
        dataVo.setData(dateStr);

        if (CollectionUtils.isEmpty(dos)){
            dataVo.setRemainCount(0);
            dataVo.setMinCost(ErrorConstant.NULL_STRING);
            return dataVo;
        }

        int allRemainCount = 0;
        BigDecimal minCost = null;
        for (DoctorMerchantAppointmentDo doctorRegisterAppointmentDo : dos){
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

    // 获取doctorCardAo
    @NotNull
    @Override
    public List<RegisterAppointmentDoctorCardAo> getDoctorCardAo
            (@NotNull List<DoctorMerchantAppointmentDo> dos){
        if (CollectionUtils.isEmpty(dos)){
            return new ArrayList<>();
        }

        // 用dos批量查询 -> do; 避免逐个查询产生多余的io (Mybatis不会添加null对象)
        List<RegisterAppointmentDoctorCardBo> bos = doctorMerchantBoMapper.getDoctorCardBosByDos(
                dos
        );
        if (CollectionUtils.isEmpty(bos)){
            return new ArrayList<>();
        }

        // converter: bo -> vo
        return registerAppointmentDoctorCardConverter.bosToAos(bos);
    }

    // 获取DataVoList
    @NotNull
    @Override
    public List<RegisterAppointmentDataVo> getDataVoList(@NotNull RegisterAppointmentSelectAo ao) throws AppException{
        /// 参数校验
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

        /// 数据源
        /*
            首先查询Redis缓存，如果Redis缓存未查询到再查询 Mysql
            由于时间处理会比较特殊，所以redis_key不存储时间参数，而是获取时间参数之外的数据然后计算时间参数
            如果不按照上述方式去处理的话会出现的bug: 每次传入的时间参数都是当前时间，那么每次都找不到缓存不说还会产生大量垃圾缓存。
            Key: key = PREFIX:${province}:${city}:${region}:${departmentCode}:${subjectCode}
            过期时间设计: 预约数据只能查询0~3天的数据, 所以应该设计4天为过期时间
            缓存对象: 缓存对象为Do, 因为Vo涉及对Do的计算, 如果只因为数据存在就返回数据, 那么一定会出现缓存Vo和Do计算出来的Vo不一致
            如果缓存对象为Vo, 那么每次更新Do的时候都要删除缓存, 避免返回过时的Vo这一问题
         */
        StringBuilder keyBuilder = new StringBuilder(MedicineRedisKey.Appointment.getDataVoList_KEY_PREFIX);

        // 添加地点信息
        keyBuilder.append(ao.getRegisterLocation().getProvince()).append(":");
        keyBuilder.append(ao.getRegisterLocation().getCity() == null ? "null" : ao.getRegisterLocation().getCity()).append(":");
        keyBuilder.append(ao.getRegisterLocation().getRegion() == null ? "null" : ao.getRegisterLocation().getRegion()).append(":");

        // 添加科室和科目代码
        keyBuilder.append(ao.getRegisterDepartmentCode()).append(":");
        keyBuilder.append(ao.getRegisterSubjectCode() == null ? "null" : ao.getRegisterSubjectCode()).append(":");

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
            List<DoctorMerchantAppointmentDo> doctorRegisterAppointmentDos =
                    doctorMerchantAppointment.getDosByParam(
                        ao.registerLocation,
                        registerDate,
                        ao.registerDepartmentCode,
                        ao.registerSubjectCode
            );

            String dateStr = DateUtils.yyyyMMddHHmmssToString(registerDate);

            // dataVo
            RegisterAppointmentDataVo dataVo = getDataVo(doctorRegisterAppointmentDos, dateStr);
            dataVos.add(dataVo);
        }
        return dataVos;
    }

    /**
     * 预约
     * @param doctorMerchantAppointmentId   医生商户id
     * @param userId                        用户id
     * @param orderId                       订单id        (因为此方法是消息队列监听者异步调用的，
     * 但是在此之前user不能没有订单id信息，不然就找不到订单了，
     * 所以由上游创建订单id，如果此处处理失败了，就将redis的数据清除)
     * @throws AppException                 预约失败的异常
     */
    @Override
    public void appointment
    (@NotNull Long doctorMerchantAppointmentId, @NotNull Long userId, long orderId) throws AppException{
        // 检查当前状态是否是可预约 todo 查询改为redis 缓存查询

        // 执行事务
        // 对商品上锁，库减少（模拟减少，因为有5分钟的支付时间，未支付的话就库存数据增）
        // 缓存减少 事务sql
        long startTime = System.currentTimeMillis();
        log.info("[用户{}]开始预约事务", userId);
        appointmentTransactionalService.createAppointmentOrder(
                orderId, doctorMerchantAppointmentId, userId
        );
        log.info("[用户{}]预约事务完成，耗时{}ms", userId, System.currentTimeMillis() - startTime);

        // mq -> 传参并告知purchase-service生成待支付的订单 -> purchase-service直接将消息交给netty通知user，并且直接在purchase调用mapper或者dubbo修改数据库
        // 消息队列生成的未支付的订单如果回到死信队列就将其删掉，并且归还数据库的数据
        // todo 稍后开发 支付服务
    }
    
    // 获取list
    /// 缓存
    // 生成订单缓存
    @Override
    public void generateOrderCache(@NotNull Long doctorMerchantAppointmentId, @NotNull Long userId, long orderId, @NotNull RedissonClusterLock appointmentLock) throws AppException {
        DoctorMerchantAppointmentDo doctorMerchantAppointmentDo = doctorMerchantAppointment.getById(doctorMerchantAppointmentId);
        if (doctorMerchantAppointmentDo == null || doctorMerchantAppointmentDo.getId() == null){
            // 异常则解开分布式锁
            redissonService.unlock(appointmentLock);
            log.warn("[userId: {}][doctorMerchantAppointmentId: {}] 申请失败，不存在此预约信息", userId, doctorMerchantAppointmentId);
            throw new AppException(MedicineExceptions.DOCTOR_MERCHANT_NOT_EXIST);
        }
        List<DoctorMerchantAppointmentDo> dos = new ArrayList<>();
        dos.add(doctorMerchantAppointmentDo);
        List<RegisterAppointmentDoctorCardBo> boList = doctorMerchantBoMapper.getDoctorCardBosByDos(dos);
        if (CollectionUtils.isEmpty(boList) || boList.get(0) == null){
            // 异常则解开分布式锁
            redissonService.unlock(appointmentLock);
            log.warn("[userId: {}][doctorMerchantAppointmentId: {}] 申请失败，bo inner join 联合查询数据不完整", userId, doctorMerchantAppointmentId);
            throw new AppException(MedicineExceptions.DOCTOR_MERCHANT_NOT_EXIST);
        }

        RegisterAppointmentDoctorCardVo vo = registerAppointmentDoctorCardConverter.boToVo(boList.get(0));

        LocalDateTime registerDate = LocalDateTime.now();
        String dateStr = DateUtils.yyyyMMddHHmmssToString(registerDate);
        AppointmentDoctorOrderListVo listVo;
        try {
            listVo = registerAppointmentDoctorCardConverter.getAppointmentDoctorOrderListVo(vo,
                        dateStr,
                        AppointmentMerchantStatusEnum.NULL.getCode(),
                        UserOrderStatusEnum.NULL.getCode()
                    );
        } catch (CloneNotSupportedException e) {
            // 异常则解开分布式锁
            redissonService.unlock(appointmentLock);
            log.error("[userId: {}][doctorMerchantAppointmentId: {}] 申请失败，生成缓存失败, copy失败", userId, doctorMerchantAppointmentId);
            throw new AppException("系统错误, 生成订单失败", e);
        }

        // 生成缓存
        AppointmentDoctorOrderListAo ao = new AppointmentDoctorOrderListAo();
        ao.setListVo(listVo);
        ao.setOrderId(orderId);
        ao.setDoctorMerchantId(doctorMerchantAppointmentId);

        // 缓存到redis
        boolean isCached = registerAppointmentRedisMapper.saveAppointmentDoctorOrderListAo(
                userId, doctorMerchantAppointmentId, orderId, ao
        );

        if (isCached){
            // 检查
            AppointmentDoctorOrderListAo cacheObject =
                    registerAppointmentRedisMapper.getAppointmentDoctorOrderListAo(
                            userId, doctorMerchantAppointmentId, orderId
                    );
            log.info("[user: {}]申请商户[merchantId: {}][orderId: {}], 缓存订单成功, 等待后续处理. 缓存结果: {}",
                    userId, doctorMerchantAppointmentId, orderId, cacheObject);
        }
        else {
            log.error("[系统错误, Redis缓存异常][user: {}]申请商户[merchantId: {}][orderId: {}], 缓存订单失败",
                    userId, doctorMerchantAppointmentId, orderId);
        }

    }

    // 获取user预约订单列表
    @NotNull
    public List<AppointmentDoctorOrderListAo> getAppointmentRecordList(@NotNull Long userId, int sortType) throws AppException {
        return new ArrayList<>();
    }
}
