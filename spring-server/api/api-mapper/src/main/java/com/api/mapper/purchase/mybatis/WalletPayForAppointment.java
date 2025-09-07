package com.api.mapper.purchase.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author 13225
 * @date 2025/8/27 17:49
 */
@Mapper
public interface WalletPayForAppointment {

    /**
     * 为AppointmentOrder支付      (未测试，很可能出出现问题)
     * @param orderId        订单id
     * @param successCode    如果成功，更新订单的状态的code
     * @return               支付结果
    UPDATE
        user_wallet uw
    SET
        uw.balance = uw.balance - (
            SELECT dma.cost
            FROM user_customer_appointment_order ucao
            INNER JOIN doctor_merchant_appointment dma ON ucao.doctor_merchant_appointment_id = dma.id
            WHERE ucao.id = #{orderId}
        )
    WHERE
        uw.user_id = (
            SELECT user_id
            FROM user_customer_appointment_order
            WHERE id = #{orderId}
        )
    AND uw.balance >= (
        SELECT dma.cost
        FROM user_customer_appointment_order ucao
        INNER JOIN doctor_merchant_appointment dma ON ucao.doctor_merchant_appointment_id = dma.id
        WHERE ucao.id = #{orderId}
    );

    UPDATE
        user_customer_appointment_order
    SET
        user_order_status = #{successCode}
    WHERE
        id = #{orderId}
    AND EXISTS (
        SELECT 1
        FROM user_wallet uw
        WHERE uw.user_id = (
            SELECT user_id
            FROM user_customer_appointment_order
            WHERE id = #{orderId}
        )
        AND uw.balance >= (
            SELECT dma.cost
            FROM user_customer_appointment_order ucao
            INNER JOIN doctor_merchant_appointment dma ON ucao.doctor_merchant_appointment_id = dma.id
            WHERE ucao.id = #{orderId}
        )
    );
     */
    int payForAppointmentOrder(
            @Param("orderId") Long orderId,
            @Param("successCode") Integer successCode
    );

}
