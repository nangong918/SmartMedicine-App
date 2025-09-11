package com.czy.medicine.controller;

import cn.hutool.core.util.IdUtil;
import com.api.mapper.medicine.redis.AppointmentDoctorOrderRedisMapper;
import com.api.mapper.medicine.redis.DoctorMerchantAppointmentRedisMapper;
import com.czy.api.constant.medicine.AppointmentSortTypeEnum;
import com.czy.api.constant.medicine.MedicineConstant;
import com.czy.api.constant.purchase.PurchaseConstant;
import com.czy.api.converter.domain.medicine.AppointmentDoctorOrderListConverter;
import com.czy.api.domain.ao.medicine.AppointmentDoctorAo;
import com.czy.api.domain.ao.medicine.AppointmentDoctorOrderDetailsAo;
import com.czy.api.domain.ao.medicine.AppointmentDoctorOrderListAo;
import com.czy.api.domain.dto.base.BaseResponse;
import com.czy.api.domain.dto.http.request.AppointmentDoctorRequest;
import com.czy.api.domain.dto.http.request.GetRegisterAppointmentListRequest;
import com.czy.api.domain.dto.http.request.GetUserAppointmentRecordRequest;
import com.czy.api.domain.dto.http.response.AppointmentDoctorResponse;
import com.czy.api.domain.dto.http.response.GetAllRegisterAppointmentDateResponse;
import com.czy.api.domain.dto.http.response.GetRegisterAppointmentListResponse;
import com.czy.api.domain.dto.http.response.GetUserAppointmentRecordResponse;
import com.czy.api.domain.vo.medicine.AppointmentDoctorDataVo;
import com.czy.api.domain.vo.medicine.AppointmentDoctorPageVo;
import com.czy.api.exception.CommonExceptions;
import com.czy.api.exception.MedicineExceptions;
import com.czy.api.exception.PurchaseExceptions;
import com.czy.medicine.mq.AppointmentMqSender;
import com.czy.medicine.service.AppointmentDoctorService;
import com.utils.redisson.service.RedissonClusterLock;
import com.utils.redisson.service.RedissonService;
import exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 13225
 * @date 2025/8/18 14:23
 * 本来需要pay-service，但是为了内存只能将pay-service和purchase-service合并
 * todo 增删缓存
 *      purchase的支付服务；订单生成，超时未支付关闭订单；商户状态/订单状态变化
 *      取消预约 + 退款
 *      再次预约
 */
@Slf4j
@CrossOrigin(origins = "*") // 跨域
@RestController
@RequiredArgsConstructor
@RequestMapping(MedicineConstant.RegisterAppointment_CONTROLLER)
public class RegisterAppointmentController {

    private final AppointmentDoctorService appointmentDoctorService;
    private final RedissonService redissonService;
    private final AppointmentMqSender appointmentMqSender;
    private final AppointmentDoctorOrderRedisMapper appointmentDoctorOrderRedisMapper;
    private final DoctorMerchantAppointmentRedisMapper doctorMerchantAppointmentRedisMapper;
    private final AppointmentDoctorOrderListConverter appointmentDoctorOrderListConverter;

    /// 查：获取

    // 获取可预约列表
    @PostMapping("/getList")
    public BaseResponse<GetRegisterAppointmentListResponse>
    getList(@Validated @RequestBody GetRegisterAppointmentListRequest request){
        // 参数校验 （此处ao中部分参数因为复用没写校验所以需要单独校验逻辑）
        AppointmentDoctorPageVo pageVo = appointmentDoctorService.getPage(
                request.getRequestAo()
        );

        GetRegisterAppointmentListResponse response = new GetRegisterAppointmentListResponse();
        response.setPageVo(pageVo);
        return BaseResponse.getResponseEntitySuccess(response);
    }

    // 获取所有预约时间
    @PostMapping("/getAllDate")
    public BaseResponse<GetAllRegisterAppointmentDateResponse>
    getAllDate(@Validated @RequestBody GetRegisterAppointmentListRequest request) {
        List<AppointmentDoctorDataVo> dataVos = appointmentDoctorService.getDataVoList(
                request.getRequestAo()
        );

        GetAllRegisterAppointmentDateResponse response = new GetAllRegisterAppointmentDateResponse();

        response.setDataVos(dataVos);

        return BaseResponse.getResponseEntitySuccess(response);
    }

    // 获取user预约订单列表 mysql: 816ms; redis: 11ms
    @PostMapping("/getCustomerList")
    public BaseResponse<GetUserAppointmentRecordResponse>
    getAppointmentRecordList
    (@Validated @RequestBody GetUserAppointmentRecordRequest request){
        // 参数校验
        int sortType = request.getSortType();
        boolean sortTypeLegal = false;

        for (AppointmentSortTypeEnum value : AppointmentSortTypeEnum.values()) {
            if (value.getCode() == sortType){
                sortTypeLegal = true;
                break;
            }
        }
        if (!sortTypeLegal){
            return BaseResponse.LogBackError(CommonExceptions.SORT_TYPE_NOT_FOUND);
        }

        // 获取订单记录 todo 改为 AOP, 此处查询存在问题
        List<AppointmentDoctorOrderListAo> currentOrders = appointmentDoctorService.getAppointmentRecordList(
                request.getUserId(), sortType,
                request.getUserLongitude(), request.getUserLatitude()
        );
        // 获取未处理的待审核订单 此部分订单只会维持 5分钟
        List<AppointmentDoctorOrderListAo> unprocessedOrders = appointmentDoctorOrderRedisMapper.getAllAppointmentRecordList(
                request.getUserId()
        );
        // 去重
        Set<Long> currentOrderIds = currentOrders
                .stream()
                .map(AppointmentDoctorOrderListAo::getOrderId)
                .collect(Collectors.toSet());
        unprocessedOrders = Optional.of(unprocessedOrders)
                .filter(list -> !list.isEmpty())
                .map(l -> {
                    l.removeIf(order -> currentOrderIds.contains(order.getOrderId()));
                    return l;
                })
                .orElse(Collections.emptyList());

        GetUserAppointmentRecordResponse response = new GetUserAppointmentRecordResponse();
        response.setCurrentOrders(currentOrders);
        response.setUnprocessedOrders(unprocessedOrders);

        return BaseResponse.getResponseEntitySuccess(response);
    }

    @GetMapping("/getCustomerDetails")
    public BaseResponse<AppointmentDoctorOrderDetailsAo>
    getAppointmentRecordDetails
            (@RequestParam("userId") Long userId,
             @RequestParam("orderId") Long orderId){
        AppointmentDoctorOrderListAo listAo = appointmentDoctorService.getAppointmentRecordDetails(userId, orderId);
        AppointmentDoctorOrderDetailsAo detailsAo = appointmentDoctorOrderListConverter.toDetailsAo(listAo);
        return BaseResponse.getResponseEntitySuccess(detailsAo);
    }

    // 获取某个预约订单的详情

    /// 增：预约
    @PostMapping(MedicineConstant.APPOINTMENT)
    public BaseResponse<AppointmentDoctorResponse> appointment
    (@Validated @RequestBody AppointmentDoctorRequest request){
        /// 1. 行为幂等 [1.1] 行为分布式锁
        // 分布式锁（避免重复预约；避免死锁设置 5分钟自动锁消除）
        String dataId = request.getDoctorMerchantAppointmentId().toString() + ":" + request.getUserId().toString();
        String mappingPath = MedicineConstant.RegisterAppointment_CONTROLLER + MedicineConstant.APPOINTMENT;
        RedissonClusterLock appointmentLock = new RedissonClusterLock(
                dataId,
                mappingPath,
                // 5分钟(300s)，单位：秒
                PurchaseConstant.PAY_TIMEOUT
        );
        log.info("[预约挂号][行为幂等检查][开始获取分布式锁失败][userId: {}][merchantId: {}]",
                request.getUserId(), request.getDoctorMerchantAppointmentId());
        // 获取分布式锁
        if (!redissonService.tryLock(appointmentLock)){
            log.warn("[预约挂号][获取分布式锁失败][user: {}][商户: {}]",
                    request.getUserId(), request.getDoctorMerchantAppointmentId());
            BaseResponse.LogBackError(PurchaseExceptions.REPEAT_APPLY_LOCK);
        }

        /// 1. 行为幂等 [1.2] 获取用户订单, 检查重复预约; 防止用户重复预约
        log.info("[预约挂号][行为幂等检查][开始获取用户订单][检查重复预约; 防止用户重复预约][user: {}][商户: {}]",
                request.getUserId(), request.getDoctorMerchantAppointmentId());
        try {
            // user:商户预约是否已经存在 (会抛出redis连接失败的错误, 避免出现redis挂掉导致超卖问题)
            boolean isExist = appointmentDoctorService.checkIsUserEffectiveAppointmentExist(
                    request.getUserId(), request.getDoctorMerchantAppointmentId()
            );
            if (isExist){
                redissonService.unlock(appointmentLock);
                log.warn("[预约挂号][用户已存在有效预约][user: {}][商户: {}]", request.getUserId(), request.getDoctorMerchantAppointmentId());
                return BaseResponse.LogBackError(
                        MedicineExceptions.APPOINTMENT_DOCTOR_ORDER_EXIST
                );
            }
        } catch (AppException e){
            redissonService.unlock(appointmentLock);
            log.error("[预约挂号][用户预约业务异常][解除预约行为分布式锁][user: {}][商户: {}]", request.getUserId(), request.getDoctorMerchantAppointmentId(), e);
            return BaseResponse.LogBackError(e);
        } catch (Exception e){
            redissonService.unlock(appointmentLock);
            log.error("[预约挂号][用户预约系统异常][解除预约行为分布式锁][user: {}][商户: {}]", request.getUserId(), request.getDoctorMerchantAppointmentId(), e);
            return BaseResponse.LogBackError(
                    MedicineExceptions.APPOINTMENT_DOCTOR_ORDER_EXIST
            );
        }

        /// 2. Redis原子性获取库存许可
        try {
            boolean acquiredResult = doctorMerchantAppointmentRedisMapper.reserveAppointment(request.getDoctorMerchantAppointmentId());
            if (acquiredResult){
                log.info("[user: {}][商户: {}][获取预约permit成功]继续执行流程", request.getUserId(), request.getDoctorMerchantAppointmentId());
            }
            else {
                redissonService.unlock(appointmentLock);
                log.warn("[预约doctor商户: {}失败][获取redisson permit失败][库存不足][解除预约行为分布式锁]", request.getDoctorMerchantAppointmentId());
                return BaseResponse.LogBackError(PurchaseExceptions.ORDER_INVENTORY_APPLY_FAILED);
            }
        } catch (AppException e){
            log.warn("[预约挂号][用户预约业务异常][解除预约行为分布式锁]", e);
            redissonService.unlock(appointmentLock);
            return BaseResponse.LogBackError(e);
        } catch (Exception e){
            log.error("[预约挂号][用户预约系统异常][解除预约行为分布式锁] ", e);
            redissonService.unlock(appointmentLock);
            return BaseResponse.LogBackError(PurchaseExceptions.ORDER_INVENTORY_APPLY_FAILED);
        }

        /// 3. 生成缓存
        // 缓存功能: 1. user查询订单状态 2. 后端检查是否重复预约
        // 缓存数据结构: AppointmentDoctorOrderListAo
        // 缓存存储方式: ZSet 有序集合
        // 订单Id生成: 在加入消息队列之前先生成订单id然后缓存到Redis避免找不到; see: getAppointmentRecordList
        long orderId = IdUtil.getSnowflakeNextId();
        log.info("[预约挂号][开始生成用户订单view (AppointmentDoctorOrderListAo) 缓存, 订单id: {}]", orderId);
        try {
            appointmentDoctorService.generateOrderCache(
                    request.getDoctorMerchantAppointmentId(),
                    request.getUserId(),
                    orderId,
                    appointmentLock
            );
        } catch (AppException e){
            log.error("[预约挂号][生成缓存失败]: ", e);
            // 归还库存
            if(!doctorMerchantAppointmentRedisMapper.cancelAppointment(request.getDoctorMerchantAppointmentId())){
                log.warn("[预约挂号业务异常][库存归还失败][商户: {}][用户: {}]",
                        request.getDoctorMerchantAppointmentId(), request.getUserId());
            }
            redissonService.unlock(appointmentLock);
            return BaseResponse.LogBackError(e);
        } catch (Exception e){
            log.error("[预约失败][生成缓存系统异常]: ", e);
            // 归还库存
            if(!doctorMerchantAppointmentRedisMapper.cancelAppointment(request.getDoctorMerchantAppointmentId())){
                log.warn("[预约挂号系统异常][库存归还失败][商户: {}][用户: {}]",
                        request.getDoctorMerchantAppointmentId(), request.getUserId());
            }
            redissonService.unlock(appointmentLock);
            return BaseResponse.LogBackError(CommonExceptions.SYSTEM_ERROR);
        }

        /// 4. 加入消息队列，避免数据库qps过高
        // 使用rabbitmq，避免jvm单机挂掉消息丢失，出现分布式死锁。
        AppointmentDoctorAo appointmentDoctorAo = new AppointmentDoctorAo();
        appointmentDoctorAo.setUserId(request.getUserId());
        appointmentDoctorAo.setDoctorMerchantAppointmentId(request.getDoctorMerchantAppointmentId());
        appointmentDoctorAo.setOrderId(orderId);
        log.info("[预约挂号][生成缓存成功][订单状态: 待审核][待审核加入消息队列，避免审核访问数据库qps过高][商户: {}][用户: {}]",
                request.getDoctorMerchantAppointmentId(), request.getUserId());
        appointmentMqSender.push(appointmentDoctorAo);

        // 通知前端耐心等待预约结果
        return BaseResponse.getResponseEntitySuccess(new AppointmentDoctorResponse(
                request.getDoctorMerchantAppointmentId(),
                orderId
        ));
    }

    /// 改（删）：取消订单 (退款) todo 后续再做, 因为涉及 分布式事务
    @PostMapping(MedicineConstant.CANCEL)
    public BaseResponse<Object> cancelAppointment
    (@Validated @RequestBody AppointmentDoctorRequest request){

        /// 1.1 行为幂等: 分布式锁
        // 获取分布式锁; MedicineConstant.RegisterAppointment_CONTROLLER + MedicineConstant.CANCEL;
        String dataId = request.getDoctorMerchantAppointmentId().toString() + ":" + request.getUserId().toString();
        String mappingPath = MedicineConstant.RegisterAppointment_CONTROLLER + MedicineConstant.CANCEL;
        RedissonClusterLock cancelLock = new RedissonClusterLock(
                dataId,
                mappingPath,
                // 5分钟(300s)，单位：秒
                PurchaseConstant.PAY_TIMEOUT
        );

        /// 1.2 行为幂等: 订单状态检查
        // 订单已使用 (待评价 -> 不可退款)

        // 取消订单
        // todo 金额: [支付系统, 订单系统] 分布式事务Saga

        return null;
    }
    //

    /// 使用订单 待时用 -> 待评价
}
