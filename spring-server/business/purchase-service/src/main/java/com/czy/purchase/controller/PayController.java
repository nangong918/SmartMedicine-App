package com.czy.purchase.controller;

import com.czy.api.constant.purchase.PurchaseConstant;
import com.czy.api.domain.dto.base.BaseResponse;
import com.czy.api.domain.dto.http.request.PayAppointmentOrderRequest;
import com.czy.api.domain.dto.http.response.PayAppointmentResponse;
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

    @PostMapping("/appointment")
    public BaseResponse<PayAppointmentResponse>
    payAppointmentOrder(@RequestBody PayAppointmentOrderRequest request){
        // 消息队列, 处理高并发支付订单 todo
        return null;
    }
}
