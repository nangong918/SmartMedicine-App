package com.api.mapper.medicine.mybatis.bo;

import com.czy.api.domain.bo.medicine.AppointmentOrderStatusBo;

/**
 * @author 13225
 * @date 2025/8/27 16:11
 */
public interface AppointmentOrderStatusBoMapper {

    /**
     * 根据订单id获取预约订单状态
     SELECT
        ucao.id AS orderId,
        dma.id AS doctorMerchantId,
        ucao.user_id AS userId,
        ucao.user_order_status AS userOrderStatus,
        dma.cost AS merchantPrice,
        dma.begin_date AS beginDate
     FROM
         user_appointment_order AS ucao
      INNER JOIN doctor_merchant_appointment AS dma ON ucao.doctor_merchant_appointment_id = dma.id
      WHERE
          ucao.id = #{orderId}
     * @param orderId           订单id
     * @return                  预约订单状态
     */
    AppointmentOrderStatusBo fetchAndLockBoByOrderId(Long orderId);

}
