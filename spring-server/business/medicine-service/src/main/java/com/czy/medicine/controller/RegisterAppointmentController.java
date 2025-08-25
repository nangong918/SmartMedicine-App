package com.czy.medicine.controller;

import cn.hutool.core.util.IdUtil;
import com.czy.api.constant.medicine.AppointmentSortTypeEnum;
import com.czy.api.constant.medicine.MedicineConstant;
import com.czy.api.constant.purchase.PurchaseConstant;
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
import com.czy.api.domain.vo.medicine.RegisterAppointmentDataVo;
import com.czy.api.domain.vo.medicine.RegisterAppointmentPageVo;
import com.czy.api.exception.CommonExceptions;
import com.czy.api.exception.PurchaseExceptions;
import com.czy.medicine.mq.AppointmentMqSender;
import com.czy.medicine.service.RegisterAppointmentService;
import com.utils.redisson.service.RedissonClusterLock;
import com.utils.redisson.service.RedissonService;
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

import java.util.List;

/**
 * @author 13225
 * @date 2025/8/18 14:23
 * 本来需要pay-service，但是为了内存只能将pay-service和purchase-service合并
 * todo 1.虚拟数据导入脚本（1.根据当前时间生成4天，2.每天至少生成5条 3.生成地址定位为：广东-深圳-南山）
 *      2.获取可预约列表，获取所有预约时间：mysql查数据查询测试
 *      3.user预约：mysql插入数据测试（redis缓存 + mysql避免超卖问题）
 *      4.purchase的支付服务；订单生成，超时未支付关闭订单；商户状态/订单状态变化
 *      5.获取user预约订单列表
 *      6.获取订单详情
 *      7.取消预约 + 退款
 *      8.再次预约
 */
@Slf4j
@CrossOrigin(origins = "*") // 跨域
@RestController
@RequiredArgsConstructor
@RequestMapping(MedicineConstant.RegisterAppointment_CONTROLLER)
public class RegisterAppointmentController {

    private final RegisterAppointmentService registerAppointmentService;
    private final RedissonService redissonService;
    private final AppointmentMqSender appointmentMqSender;

    /// 查：获取

    // 获取可预约列表
    @PostMapping("/getList")
    public BaseResponse<GetRegisterAppointmentListResponse>
    getList(@Validated @RequestBody GetRegisterAppointmentListRequest request){
        // 参数校验 （此处ao中部分参数因为复用没写校验所以需要单独校验逻辑）
        RegisterAppointmentPageVo pageVo = registerAppointmentService.getPage(
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
        List<RegisterAppointmentDataVo> dataVos = registerAppointmentService.getDataVoList(
                request.getRequestAo()
        );

        GetAllRegisterAppointmentDateResponse response = new GetAllRegisterAppointmentDateResponse();

        response.setDataVos(dataVos);

        return BaseResponse.getResponseEntitySuccess(response);
    }

    // 获取user预约订单列表
    @PostMapping("/getCustomerList")
    public BaseResponse<List<AppointmentDoctorOrderListAo>>
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

        List<AppointmentDoctorOrderListAo> aos = registerAppointmentService.getAppointmentRecordList(
                request.getUserId(), sortType,
                request.getUserLongitude(), request.getUserLatitude()
        );

        return BaseResponse.getResponseEntitySuccess(aos);
    }

    @GetMapping("/getCustomerDetails")
    public BaseResponse<AppointmentDoctorOrderDetailsAo>
    getAppointmentRecordDetails
            (@RequestParam("userId") Long userId,
             @RequestParam("orderId") Long orderId){
        // 暂时不开发
        return null;
    }

    // 获取某个预约订单的详情

    /// 增：预约
    @PostMapping(MedicineConstant.APPOINTMENT)
    public BaseResponse<AppointmentDoctorResponse> appointment
    (@Validated @RequestBody AppointmentDoctorRequest request){

        /// 分布式锁（避免重复预约；避免死锁设置 5分钟自动锁消除）
        String dataId = request.getDoctorMerchantAppointmentId().toString() + ":" + request.getUserId().toString();
        String mappingPath = MedicineConstant.RegisterAppointment_CONTROLLER + MedicineConstant.APPOINTMENT;
        RedissonClusterLock appointmentLock = new RedissonClusterLock(
                dataId,
                mappingPath,
                // 5分钟(300s)，单位：秒
                PurchaseConstant.PAY_TIMEOUT
        );
        // 获取分布式锁
        if (!redissonService.tryLock(appointmentLock)){
            log.warn("[预约挂号][获取分布式锁失败][user: {}][商户: {}]", request.getUserId(), request.getDoctorMerchantAppointmentId());
            BaseResponse.LogBackError(PurchaseExceptions.REPEAT_APPLY_LOCK);
        }

        /// 生成缓存
        // 在增加和修改的时候可以直接访问数据库, 因为是不可避免的; 但是在查询的时候就要尽量避免使用数据库而是缓存

        // 在加入消息队列之前先生成订单id然后缓存到redis避免找不到。see: getAppointmentRecordList
        long orderId = IdUtil.getSnowflakeNextId();
        // 加入redis缓存 用于前端等待结果的时候去主动查询订单状态
        // 创建AppointmentDoctorOrderListVo；并且在订单为出来之前AppointmentDoctorOrderListVo就足够担任详情页
        // 确定存储redis的数据和数据结构, 那就得看查询的时候是怎么查询的, 参数是怎么传递的? 参数是userId, 查询对象是 AppointmentDoctorOrderListAo
        // redis的缓存对象不应该分开存储, 因为分开存储就面临问题: 1.需要新的dataId对象来专门存储Id 2.设计大量的RedisMapper 3. 缓存击穿之后还得单独调用Mybatis的接口, 然和Mybatis的查询基本是联合查询, 单独查询反而性能差
        registerAppointmentService.generateOrderCache(
                request.getDoctorMerchantAppointmentId(),
                request.getUserId(),
                orderId,
                appointmentLock
        );

        /// 加入消息队列，避免数据库qps过高
        // 使用rabbitmq，避免jvm单机挂掉消息丢失，出现分布式死锁。
        AppointmentDoctorAo appointmentDoctorAo = new AppointmentDoctorAo();
        appointmentDoctorAo.setUserId(request.getUserId());
        appointmentDoctorAo.setDoctorMerchantAppointmentId(request.getDoctorMerchantAppointmentId());
        appointmentDoctorAo.setOrderId(orderId);
        appointmentMqSender.push(appointmentDoctorAo);

        // 通知前端耐心等待预约结果
        return BaseResponse.getResponseEntitySuccess(new AppointmentDoctorResponse(
                request.getDoctorMerchantAppointmentId(),
                orderId
        ));
    }

    /// 改（删）：取消预约
    @PostMapping("/cancel")
    public BaseResponse<Object> cancelAppointment
    (@Validated @RequestBody AppointmentDoctorRequest request){
        return null;
    }
}
