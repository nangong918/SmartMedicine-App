package com.czy.api.constant.medicine;

/**
 * @author 13225
 * @date 2025/8/21 11:42
 */
public interface MedicineRedisKey {
    String ID = "medicine:";
    interface Appointment {
        String ID = "appointment:";
        String getDataVoList_KEY_PREFIX = MedicineRedisKey.ID + ID + "getDataVoList:";
        // 预约的缓存订单: key: userId: orderId
        String appointmentOrder_KEY_PREFIX = MedicineRedisKey.ID + ID + "appointmentOrder:";
        // 缓存订单的过期时间: 5min (300s)
        Long appointmentOrder_EXPIRE_TIME = 5 * 60L;
    }
}
