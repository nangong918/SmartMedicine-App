package com.czy.medicine.mq;

import com.czy.api.MqConstants;
import com.czy.api.constant.medicine.MedicineConstant;
import com.czy.api.constant.purchase.PurchaseConstant;
import com.czy.api.domain.ao.medicine.AppointmentDoctorAo;
import com.czy.medicine.service.RegisterAppointmentService;
import com.utils.redisson.service.RedissonClusterLock;
import com.utils.redisson.service.RedissonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author 13225
 * @date 2025/8/20 17:43
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class DoctorMerchantAppointmentMqHandler {

    private final RegisterAppointmentService registerAppointmentService;
    private final RedissonService redissonService;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            name = MqConstants.AppointmentQueue.DOCTOR_MERCHANT_QUEUE,
                            // 持久化队列
                            durable = "true",
                            // 排他队列
                            exclusive = "false",
                            // 自动删除：消息队列，需要高可靠
                            autoDelete = "false"
                    ),
                    exchange = @Exchange(
                            value = MqConstants.Exchange.APPOINTMENT_EXCHANGE,
                            type = ExchangeTypes.TOPIC,
                            durable = "true"  // 持久化交换机
                    ),
                    key = MqConstants.AppointmentQueue.Routing.DOCTOR_MERCHANT_ROUTING
            )
    )
    public void handleDoctorMerchantAppointmentMessage(AppointmentDoctorAo message){
        Long userId = message.getUserId();
        Long doctorMerchantAppointmentId = message.getDoctorMerchantAppointmentId();
        Long orderId = message.getOrderId();

        if (userId == null || doctorMerchantAppointmentId == null){
            log.warn("[预约 消息队列错误]用户id或预约记录id为空");
            return;
        }

        String dataId = doctorMerchantAppointmentId + ":" + userId;
        String mappingPath = MedicineConstant.RegisterAppointment_CONTROLLER + MedicineConstant.APPOINTMENT;
        RedissonClusterLock appointmentLock = new RedissonClusterLock(
                dataId,
                mappingPath,
                // 5分钟(300s)，单位：秒
                PurchaseConstant.PAY_TIMEOUT
        );

        try {
            registerAppointmentService.appointment(
                    doctorMerchantAppointmentId,
                    userId,
                    orderId
            );



        } catch (Exception e){

        } finally {
            // 解除分布式锁 （无论成功还是失败都解除）
            redissonService.unlock(appointmentLock);
        }

    }

}
