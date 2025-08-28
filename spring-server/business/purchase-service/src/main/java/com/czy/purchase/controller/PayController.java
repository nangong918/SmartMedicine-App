package com.czy.purchase.controller;

import com.czy.api.constant.purchase.PurchaseConstant;
import com.czy.api.constant.purchase.RechargeEnum;
import com.czy.api.domain.dto.base.BaseResponse;
import com.czy.api.domain.dto.http.request.PayAppointmentOrderRequest;
import com.czy.api.domain.dto.http.request.RechargeMoneyRequest;
import com.czy.api.domain.dto.http.response.PayAppointmentResponse;
import com.czy.api.domain.dto.http.response.RechargeMoneyResponse;
import com.czy.api.exception.PurchaseExceptions;
import com.czy.purchase.service.PayService;
import com.utils.redisson.service.RedissonClusterLock;
import com.utils.redisson.service.RedissonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 13225
 * @date 2025/8/26 16:05
 */
@Slf4j
@CrossOrigin(origins = "*") // 跨域
@RestController
@RequiredArgsConstructor
@RequestMapping(PurchaseConstant.Pay_CONTROLLER)
public class PayController {

    private final RedissonService redissonService;
    private final PayService payService;

    @PostMapping(PurchaseConstant.AppointmentPay_API)
    public BaseResponse<PayAppointmentResponse>
    payAppointmentOrder(@RequestBody PayAppointmentOrderRequest request){

        // 支付分布式锁
        String dataId = request.getUserId().toString() + ":" + request.getOrderId().toString();
        String mappingPath = PurchaseConstant.Pay_CONTROLLER + PurchaseConstant.AppointmentPay_API;
        RedissonClusterLock appointmentPayLock = new RedissonClusterLock(
                dataId,
                mappingPath,
                // 5分钟(300s)，单位：秒
                PurchaseConstant.PAY_TIMEOUT
        );

        // 获取分布式锁
        if (!redissonService.tryLock(appointmentPayLock)){
            log.warn("[支付预约订单][获取分布式锁失败][user: {}][订单: {}]", request.getUserId(), request.getOrderId());
            BaseResponse.LogBackError(PurchaseExceptions.REPEAT_PAY_LOCK);
        }

        // 支付订单
        int payStatus = payService.payAppointmentOrder(
                request.getUserId(),
                request.getOrderId()
        );

        PayAppointmentResponse response = new PayAppointmentResponse();
        response.setOrderId(request.getOrderId());
        response.setPayResult(payStatus);

        return BaseResponse.getResponseEntitySuccess(response);
    }

    // 充值测试
    @PostMapping("/test-recharge")
    public BaseResponse<RechargeMoneyResponse> testRecharge(@RequestBody RechargeMoneyRequest request) {
        RechargeEnum rechargeEnum = RechargeEnum.getByCode(request.getType());
        if (rechargeEnum == null || rechargeEnum.equals(RechargeEnum.NULL)) {
            return BaseResponse.LogBackError(PurchaseExceptions.RECHARGE_AMOUNT_ERROR);
        }
        RechargeMoneyResponse response = payService.testRecharge(
                request.getUserId(),
                rechargeEnum
        );
        return BaseResponse.getResponseEntitySuccess(
                response
        );
    }
}
