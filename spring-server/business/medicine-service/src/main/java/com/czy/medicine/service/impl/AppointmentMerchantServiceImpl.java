package com.czy.medicine.service.impl;

import com.api.mapper.medicine.mybatis.DoctorMerchantAppointmentMapper;
import com.api.mapper.medicine.mybatis.UserCustomerAppointmentOrderMapper;
import com.api.mapper.medicine.mybatis.bo.DoctorMerchantBoMapper;
import com.api.mapper.medicine.redis.AppointmentDoctorOrderRedisMapper;
import com.api.mapper.medicine.redis.DoctorMerchantAppointmentRedisMapper;
import com.czy.api.constant.ErrorConstant;
import com.czy.api.constant.UserOrderStatusEnum;
import com.czy.api.constant.medicine.AppointmentMerchantStatusEnum;
import com.czy.api.constant.medicine.AppointmentSortTypeEnum;
import com.czy.api.constant.medicine.MedicineConstant;
import com.czy.api.constant.medicine.MedicineRedisKey;
import com.czy.api.converter.domain.medicine.AppointmentDoctorOrderConverter;
import com.czy.api.converter.domain.medicine.RegisterAppointmentDoctorCardConverter;
import com.czy.api.domain.Do.medicine.DoctorMerchantAppointmentDo;
import com.czy.api.domain.Do.medicine.UserCustomerAppointmentDo;
import com.czy.api.domain.ao.medicine.AppointmentDoctorOrderListAo;
import com.czy.api.domain.ao.medicine.AppointmentDoctorSelectAo;
import com.czy.api.domain.ao.medicine.HospitalAo;
import com.czy.api.domain.ao.medicine.RegisterAppointmentDoctorCardAo;
import com.czy.api.domain.bo.medicine.AppointmentDoctorMerchantCardBo;
import com.czy.api.domain.bo.medicine.UserAppointmentOrderBo;
import com.czy.api.domain.dto.mq.AppointmentOrderDto;
import com.czy.api.domain.dto.mq.AppointmentPayResultDto;
import com.czy.api.domain.vo.medicine.AppointmentDoctorDataVo;
import com.czy.api.domain.vo.medicine.AppointmentDoctorMerchantCardVo;
import com.czy.api.domain.vo.medicine.AppointmentDoctorOrderListVo;
import com.czy.api.domain.vo.medicine.AppointmentDoctorPageVo;
import com.czy.api.domain.vo.medicine.DoctorVo;
import com.czy.api.exception.CommonExceptions;
import com.czy.api.exception.MedicineExceptions;
import com.czy.medicine.mq.AppointmentMqSender;
import com.czy.medicine.service.AppointmentDoctorService;
import com.czy.medicine.service.transactional.AppointmentTransactionalService;
import com.czy.medicine.utils.AppointmentMerchantStatusCalculator;
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
import org.jetbrains.annotations.Nullable;
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
public class AppointmentMerchantServiceImpl implements AppointmentDoctorService {

    private final DoctorMerchantAppointmentMapper doctorMerchantAppointmentMapper;
    private final RegisterAppointmentDoctorCardConverter registerAppointmentDoctorCardConverter;
    private final DoctorMerchantBoMapper doctorMerchantBoMapper;
    private final OssService ossService;
    private final UserCustomerAppointmentOrderMapper userCustomerAppointmentOrderMapper;
    private final RedissonService redissonService;
    private final AppointmentDoctorOrderRedisMapper appointmentDoctorOrderRedisMapper;
    private final AppointmentTransactionalService appointmentTransactionalService;
    private final AppointmentDoctorOrderConverter appointmentDoctorOrderConverter;
    private final AppointmentMqSender appointmentMqSender;
    private final DoctorMerchantAppointmentRedisMapper doctorMerchantAppointmentRedisMapper;

    // 获取PageList
    @NotNull
    @Override
    public AppointmentDoctorPageVo getPage(@NotNull AppointmentDoctorSelectAo ao) throws AppException {
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

        // 获取可挂号的记录列表 todo 改为aop先查缓存
        List<DoctorMerchantAppointmentDo> doctorRegisterAppointmentDos = doctorMerchantAppointmentMapper.getDosByParam(
                    ao.registerLocation,
                    registerTime,
                    ao.registerDepartmentCode,
                    ao.registerSubjectCode
        );

        AppointmentDoctorPageVo pageVo = new AppointmentDoctorPageVo();
        // dataVo
        AppointmentDoctorDataVo dataVo = getDataVo(doctorRegisterAppointmentDos, ao.getRegisterTime());
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
                        .map(AppointmentDoctorMerchantCardVo::getHospitalAo)
                        .map(HospitalAo::getLongitude)
                        .orElse(null);
                Double latitude = Optional.ofNullable(cardAo)
                        .map(RegisterAppointmentDoctorCardAo::getVo)
                        .map(AppointmentDoctorMerchantCardVo::getHospitalAo)
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
                .map(AppointmentDoctorMerchantCardVo::getDoctorVo)
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
    private AppointmentDoctorDataVo getDataVo
            (List<DoctorMerchantAppointmentDo> dos, String dateStr){
        AppointmentDoctorDataVo dataVo = new AppointmentDoctorDataVo();
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
        List<AppointmentDoctorMerchantCardBo> bos = doctorMerchantBoMapper.getDoctorCardBosByDoctorMerchantDos(
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
    public List<AppointmentDoctorDataVo> getDataVoList(@NotNull AppointmentDoctorSelectAo ao) throws AppException{
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
        List<AppointmentDoctorDataVo> dataVos = new ArrayList<>();
        for (LocalDateTime registerDate : registerDates) {
            // 获取可挂号的记录列表
            List<DoctorMerchantAppointmentDo> doctorRegisterAppointmentDos =
                    doctorMerchantAppointmentMapper.getDosByParam(
                        ao.registerLocation,
                        registerDate,
                        ao.registerDepartmentCode,
                        ao.registerSubjectCode
            );

            String dateStr = DateUtils.yyyyMMddHHmmssToString(registerDate);

            // dataVo
            AppointmentDoctorDataVo dataVo = getDataVo(doctorRegisterAppointmentDos, dateStr);
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
    public void reviewOrder
    (@NotNull Long doctorMerchantAppointmentId, @NotNull Long userId, long orderId) throws AppException{
        // 执行事务
        // 对商品上锁，库减少（模拟减少，因为有5分钟的支付时间，未支付的话就库存数据增）
        // 缓存减少 事务sql
        long startTime = System.currentTimeMillis();
        log.info("[审核预约挂号][用户:{}, 订单:{}]开始预约事务", userId, orderId);
        appointmentTransactionalService.createAppointmentOrder(
                orderId, doctorMerchantAppointmentId, userId
        );
        log.info("[用户{}]预约事务完成，耗时{}ms", userId, System.currentTimeMillis() - startTime);

        // mq -> 传参并告知purchase-service生成待支付的订单 -> purchase-service直接将消息交给netty通知user，并且直接在purchase调用mapper或者dubbo修改数据库
        // 消息队列生成的未支付的订单如果回到死信队列就将其删掉，并且归还数据库的数据
        // 支付服务

        AppointmentOrderDto orderDto = new AppointmentOrderDto();
        orderDto.setDoctorMerchantAppointmentId(doctorMerchantAppointmentId);
        orderDto.setUserId(userId);
        orderDto.setOrderId(orderId);
        orderDto.setOrderStatusEnum(UserOrderStatusEnum.WAITING_PAYMENT);
        orderDto.setEffectiveTime(MedicineRedisKey.Appointment.appointmentOrder_EXPIRE_TIME);

        /// 订单有效时限: 发送
        // 发送待支付消息队列: APPOINTMENT_WAIT_PAY_ROUTING
        log.info("[审核预约挂号][通知支付服务创建待支付订单][mq消息: {}]", orderDto);
        appointmentMqSender.push(orderDto);
    }
    
    // 获取list
    /// 缓存: 1. 审核期间前端直接访问的vo 2. 用于判断是否重复申请
    /**
     * 生成订单缓存 (订单缓存不在此处落库, 在事务中落库; 并且审核失败需改改为状态审核未通过, 缓存不删除)
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
    @Override
    public void generateOrderCache(@NotNull Long doctorMerchantAppointmentId, @NotNull Long userId, long orderId, @NotNull RedissonClusterLock appointmentLock) throws AppException {
        // 检查商户 (aop: redis -> mybatis)
        log.info("[预约挂号][生成缓存][Aop获取商户记录: DoctorMerchantAppointmentDo]");
        DoctorMerchantAppointmentDo doctorMerchantAppointmentDo =
                doctorMerchantAppointmentMapper.getById(doctorMerchantAppointmentId);

        if (doctorMerchantAppointmentDo == null || doctorMerchantAppointmentDo.getId() == null){
            // 异常则解开分布式锁
            redissonService.unlock(appointmentLock);
            log.warn("[预约挂号][生成缓存][userId: {}][doctorMerchantAppointmentId: {}] 申请失败，不存在此预约信息", userId, doctorMerchantAppointmentId);
            throw new AppException(MedicineExceptions.DOCTOR_MERCHANT_NOT_EXIST);
        }
        List<DoctorMerchantAppointmentDo> dos = new ArrayList<>();
        dos.add(doctorMerchantAppointmentDo);

        // 获取预约信息 (aop: redis -> mybatis)
        log.info("[预约挂号][生成缓存][Aop获取预约信息: AppointmentDoctorMerchantCardBo]");
        List<AppointmentDoctorMerchantCardBo> boList =
                doctorMerchantBoMapper.getDoctorCardBosByDoctorMerchantDos(dos);

        if (CollectionUtils.isEmpty(boList) || boList.get(0) == null){
            // 异常则解开分布式锁
            redissonService.unlock(appointmentLock);
            log.warn("[预约挂号][生成缓存][userId: {}][doctorMerchantAppointmentId: {}] 申请失败，bo inner join 联合查询数据不完整", userId, doctorMerchantAppointmentId);
            throw new AppException(MedicineExceptions.DOCTOR_MERCHANT_NOT_EXIST);
        }

        // 中间数据结构, 不能直接使用appointmentDoctorOrderConverter, 因为此时订单数据库还没数据, 无法查询对应的bo
        AppointmentDoctorMerchantCardVo vo = registerAppointmentDoctorCardConverter.boToVo(boList.get(0));

        LocalDateTime registerDate = LocalDateTime.now();
        String dateStr = DateUtils.yyyyMMddHHmmssToString(registerDate);
        AppointmentDoctorOrderListVo listVo;
        try {
            listVo = registerAppointmentDoctorCardConverter.getAppointmentDoctorOrderListVo(vo,
                        dateStr,
                        AppointmentMerchantStatusEnum.NULL.getCode(),
                        UserOrderStatusEnum.WAITING_AUDIT.getCode()
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

        // 缓存到redis: 存储到user-orders ZSet; 此缓存只是用于查看, 还有很多数据缺失, 需要再在创建订单种更新缓存
        boolean isCached = appointmentDoctorOrderRedisMapper.saveSingleAppointmentDoctorOrderListAo(
                userId,
                ao
        );
        if (isCached){
            // 检查, 获取根据时间排序的ZSet
            List<AppointmentDoctorOrderListAo> aoList = appointmentDoctorOrderRedisMapper.getAppointmentRecordList(
                    userId,
                    AppointmentSortTypeEnum.TIME.getCode(),
                    null,
                    null
            );
            if (aoList == null || aoList.isEmpty()){
                log.warn("[预约挂号][生成缓存检查] 异常: aoList is empty");
                return;
            }

            // 获取第一个, 因为时间升序, 第一个是最新的
            log.info("[预约挂号][生成缓存检查][user: {}]申请商户[merchantId: {}][orderId: {}], 缓存订单成功, 等待后续处理. 当前缓存订单数量是: {}, 缓存结果: {}",
                    userId, doctorMerchantAppointmentId, orderId, aoList.size(), aoList.get(0));
        }
        else {
            log.error("[系统错误, Redis缓存异常][user: {}]申请商户[merchantId: {}][orderId: {}], 缓存订单失败",
                    userId, doctorMerchantAppointmentId, orderId);
        }

    }

    /// List<AppointmentDoctorOrderListAo>
    // 获取user预约订单列表
    @NotNull
    @Override
    public List<AppointmentDoctorOrderListAo> getAppointmentRecordList(@NotNull Long userId, int sortType,
                                                                       @Nullable Double userLongitude, @Nullable Double userLatitude) throws AppException {
        /// 查询redis缓存   redis: 11ms
        List<AppointmentDoctorOrderListAo> listAos = appointmentDoctorOrderRedisMapper.getAppointmentRecordList(
                userId, sortType, userLongitude, userLatitude
        );
        // 不为null说明命中, empty也是命中
        if (listAos != null){
            // 缓存命中
            log.info("[获取user预约订单列表]缓存命中: userId: {}", userId);
            return listAos;
        }
        /// 缓存未命中, 查询数据库    mysql: 816ms
        else {
            log.info("[获取user预约订单列表]缓存未命中, 开始查询数据库: userId: {}", userId);
            List<UserCustomerAppointmentDo> dos = userCustomerAppointmentOrderMapper.getDosByUserId(userId);
            if (CollectionUtils.isEmpty(dos)){
                return new ArrayList<>();
            }

            List<UserAppointmentOrderBo> bos = doctorMerchantBoMapper.getDoctorCardBosByUserCustomerAppointmentDos(
                     dos
            );
            if (CollectionUtils.isEmpty(bos)){
                return new ArrayList<>();
            }


            // 数据计算填充: MerchantStatus
            AppointmentMerchantStatusCalculator.calculateFillUserAppointmentOrderBos(
                    bos
            );

            LocalDateTime registerDate = LocalDateTime.now();
            String dateStr = DateUtils.yyyyMMddHHmmssToString(registerDate);
            // bos -> aos
            listAos = appointmentDoctorOrderConverter.getAosByBos(
                    bos,
                    dateStr
            );
            if (CollectionUtils.isEmpty(listAos)){
                listAos = new ArrayList<>();
            }

            // 缓存
            boolean result = appointmentDoctorOrderRedisMapper.saveAppointmentDoctorOrderListAo(
                    userId,
                    listAos
            );

            if (!result){
                log.error("Redis错误:getAppointmentRecordList查询到mysql数据但是Redis缓存失败");
            }
        }
        return listAos;
    }

    @Override
    public void handlePayResultMessage(@NotNull AppointmentPayResultDto dto) {
        try {
            // 解析dto
            int customerStatus = Optional.ofNullable(dto.getOrderStatusEnum())
                    .map(UserOrderStatusEnum::getCode)
                    .orElse(UserOrderStatusEnum.NULL.getCode());

            if (customerStatus == UserOrderStatusEnum.NULL.getCode()){
                log.warn("[预约订单服务][处理支付结果]订单dto状态错误");
                return;
            }

            // 获取订单
            UserCustomerAppointmentDo order = userCustomerAppointmentOrderMapper.getByOrderId(dto.getOrderId());
            if (order == null || order.getId() == null){
                // 订单不存在, 直接归为error级别
                log.error("[处理支付结果][数据库查询异常]订单: {} 不存在", dto.getOrderId());
                return;
            }
            log.info("[处理支付结果][更新订单状态]订单原状态: {}", order);
            order.setUserOrderStatus(customerStatus);

            /// 1. 更新数据库
            // 库存归还 [取消的情况下]
            if (customerStatus == UserOrderStatusEnum.CANCELED.getCode()){
                // 归还sql库存
                doctorMerchantAppointmentMapper.returnStock(order.getDoctorMerchantAppointmentId());
                // 归还redis库存
                if (!doctorMerchantAppointmentRedisMapper.cancelAppointment(order.getDoctorMerchantAppointmentId())){
                    log.warn("[取消支付]归还库存失败: {}", order.getDoctorMerchantAppointmentId());
                }
                log.info("[处理支付结果][取消支付] 归还sql库存 归还redis信号量库存");
            }
            // 订单状态更新
            userCustomerAppointmentOrderMapper.update(order);
            log.info("[处理支付结果]更新数据库成功: {}", order);

            // 更新缓存(如果缓存存在)
            AppointmentDoctorOrderListAo ao = appointmentDoctorOrderRedisMapper.getAppointmentDoctorOrderListAoByOrderId(
                    dto.getUserId(),
                    // 此处商户id不能传递, 因为我确定在支付服务中, 只是对订单进行支付, 是不知道商户id的, 所以返回的是null; 并且不需要商户id也能查询到
//                dto.getDoctorMerchantAppointmentId(),
                    dto.getOrderId()
            );
            if (ao != null){
                boolean updateResult = appointmentDoctorOrderRedisMapper.updateAppointmentDoctorOrderListAoStatus(
                        dto.getUserId(),
                        dto.getOrderId(),
                        customerStatus
                );
                log.warn("[处理支付结果][更新订单状态 AppointmentDoctorOrderListAo缓存: {}]", updateResult);
            }
            else {
                log.warn("[处理支付结果][更新订单状态 AppointmentDoctorOrderListAo缓存失败, 找不到AppointmentDoctorOrderListAo缓存信息]");
            }
        } catch (Exception e){
            log.error("[处理支付结果异常: ", e);
        } finally {
            if (dto.getUserId() == null || dto.getOrderId() == null){
                log.warn("[处理支付结果][分布式锁解除失败][dto参数不足]");
            }
            else {
                // 获得锁
                AppointmentDoctorOrderListAo ao = appointmentDoctorOrderRedisMapper.getAppointmentDoctorOrderListAoByOrderId(
                        dto.getUserId(),
                        dto.getOrderId()
                );
                Long merchantId = Optional.ofNullable(ao)
                        .map(AppointmentDoctorOrderListAo::getDoctorMerchantId)
                        .orElse(null);
                if (merchantId == null){
                    log.warn("[处理支付结果][分布式锁解除失败][merchantId == null]");
                }
                else {
                    String dataId = merchantId + ":" + dto.getUserId();
                    String mappingPath = MedicineConstant.RegisterAppointment_CONTROLLER + MedicineConstant.APPOINTMENT;
                    RedissonClusterLock appointmentLock = new RedissonClusterLock(
                            dataId,
                            mappingPath
                    );
                    /// 3. 解除申请分布式锁
                    redissonService.unlock(appointmentLock);
                    log.info("[解除申请分布式锁][dataId: {}]", dataId);
                    /// 2. netty通知前端, 考虑用spring event实现
                }
            }
        }

    }

    /**
     * 检查用户是否预约此商户并且拥有有效订单
     * @param userId                        用户id
     * @param doctorMerchantAppointmentId   商户预约id
     * @return                              true:存在有效订单
     */
    @Override
    public boolean checkIsUserEffectiveAppointmentExist(@NotNull Long userId, @NotNull Long doctorMerchantAppointmentId) throws AppException{
        try {
             /*
             因为Redis的过期时间是4天, 远远超过待支付的5分钟; 同时也与商户的商品存活时间相等, 所以默认Redis没有数据就未订购
             当然要改为长久商品也很简单, 如果此处没有查询到数据就查询一下数据库就好了.
             此项目预约不需要查询数据库, 只有购买需要查询数据库
             */
            AppointmentDoctorOrderListAo orderListAo = appointmentDoctorOrderRedisMapper.getAppointmentDoctorOrderListAoByMerchantId(
                    userId,
                    doctorMerchantAppointmentId
            );
            if (orderListAo == null){
                log.info("[预约挂号][行为幂等检查][用户: {}在商户: {}不存在订单]",
                        userId, doctorMerchantAppointmentId);
                return false;
            }
            int status = Optional.ofNullable(orderListAo.listVo)
                    .map(vo -> vo.customerStatus)
                    .orElse(UserOrderStatusEnum.NULL.getCode());
            UserOrderStatusEnum userOrderStatusEnum = UserOrderStatusEnum.getByCode(status);
            log.info("[预约挂号][行为幂等检查][用户: {}在商户: {}存在订单]用户订单状态为：{}",
                    userId, doctorMerchantAppointmentId, userOrderStatusEnum);

            // isHaveEffective
            return
                    // 审核中
                    UserOrderStatusEnum.WAITING_AUDIT.equals(userOrderStatusEnum) ||
                            // 待支付
                            UserOrderStatusEnum.WAITING_PAYMENT.equals(userOrderStatusEnum) ||
                            // 待使用
                            UserOrderStatusEnum.WAITING_USE.equals(userOrderStatusEnum) ||
                            // 退款中
                            UserOrderStatusEnum.REFUNDING.equals(userOrderStatusEnum) ||
                            // 退款失败
                            UserOrderStatusEnum.REFUND_FAILED.equals(userOrderStatusEnum);
        } catch (Exception e){
            log.error("[检查用户是否预约此商户并且拥有有效订单][redis异常]", e);
            throw new AppException(CommonExceptions.SYSTEM_REDIS_ERROR);
        }
    }
}
