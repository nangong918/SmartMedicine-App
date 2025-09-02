package com.czy.medicine.mq;

import com.api.mapper.medicine.redis.DoctorMerchantAppointmentRedisMapper;
import com.czy.api.MqConstants;
import com.czy.api.constant.medicine.MedicineConstant;
import com.czy.api.constant.netty.NettyConstants;
import com.czy.api.constant.netty.ResponseMessageType;
import com.czy.api.constant.purchase.PurchaseConstant;
import com.czy.api.domain.ao.medicine.AppointmentDoctorAo;
import com.czy.api.domain.dto.socket.response.AppointmentResultResponse;
import com.czy.api.utils.NettyUtils;
import com.czy.medicine.service.RegisterAppointmentService;
import com.utils.redisson.service.RedissonClusterLock;
import com.utils.redisson.service.RedissonService;
import exception.AppException;
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
    private final AppointmentMqSender appointmentMqSender;
    private final DoctorMerchantAppointmentRedisMapper doctorMerchantAppointmentRedisMapper;

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
        log.info("DoctorMerchantAppointmentMessage::接收到预约消息：{}", message);
        Long userId = message.getUserId();
        Long doctorMerchantAppointmentId = message.getDoctorMerchantAppointmentId();
        Long orderId = message.getOrderId();

        if (userId == null || doctorMerchantAppointmentId == null){
            log.warn("[预约 消息队列错误][消息错误]用户id或预约记录id为空");
            return;
        }
        if (orderId == null){
            log.warn("[预约 消息队列错误][消息错误][预约 订单id为空]");
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

        // netty通知前端结果
        AppointmentResultResponse response = new AppointmentResultResponse();
        response.setSenderId(NettyConstants.SERVER_ID);
        response.setType(ResponseMessageType.Appointment.APPOINTMENT_RESULT);
        response.setReceiverId(userId);
        response.setOrderId(orderId);
        response.setIsSuccess(false);

        try {
            // 创建订单
            registerAppointmentService.appointment(
                    doctorMerchantAppointmentId,
                    userId,
                    orderId
            );

            // 用netty通知前端某个的处理结果
            response.setIsSuccess(true);
            log.info("netty发送消息给前端: {}", response);
            appointmentMqSender.push(response);

        } catch (AppException appe){
            log.error("用户预约失败", appe);
            // 预约失败归还库存
            if (!doctorMerchantAppointmentRedisMapper.cancelAppointment(doctorMerchantAppointmentId)){
                log.error("[预约失败][归还库存失败]: ");
            }
            // 将枚举错误填充
            response.setException(appe.getExceptionEnums());
            // 通知失败原因
            NettyUtils.sendErrorMessage(
                    userId,
                    appe,
                    appointmentMqSender
            );
        } catch (Exception e){
            log.error("[预约 消息队列错误][userId: {}][merchantId: {}][orderId: {}]",
                    userId, doctorMerchantAppointmentId, orderId, e);
            // 预约失败归还库存
            if (!doctorMerchantAppointmentRedisMapper.cancelAppointment(doctorMerchantAppointmentId)){
                log.error("[预约失败][归还库存失败]: ");
            }
            // 通知订单失败消息
            response.setMessage(e.getMessage());
            appointmentMqSender.push(response);
        } finally {
            // 解除分布式锁 （无论成功还是失败都解除）
            redissonService.unlock(appointmentLock);
            log.info("【订单】结束处理订单消息，userId: {}, doctorMerchantAppointmentId: {}, orderId: {}", userId, doctorMerchantAppointmentId, orderId);
        }
    }

}
