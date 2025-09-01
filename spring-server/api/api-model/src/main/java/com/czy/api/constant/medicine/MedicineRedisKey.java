package com.czy.api.constant.medicine;

/**
 * @author 13225
 * @date 2025/8/21 11:42
 */
public interface MedicineRedisKey {
    String ID = "medicine:";
    interface Appointment {
        String ID = "appointment:";
        /// getDataVoList
        String getDataVoList_KEY_PREFIX = MedicineRedisKey.ID + ID + "getDataVoList:";
        // 预约的缓存订单: key: userId: doctorMerchantId: orderId
        /// appointmentOrder
        String appointmentOrder_KEY_PREFIX = MedicineRedisKey.ID + ID + "appointmentOrder:";
        // 缓存订单的过期时间: 5min (300s)
        long appointmentOrder_EXPIRE_TIME = 5 * 60L;
        /// DoctorMerchant
        String DoctorMerchant_KEY_PREFIX = MedicineRedisKey.ID + ID + "DoctorMerchant:";
        // 过期时间: 4天
        long DoctorMerchant_EXPIRE_TIME = (long) 4 * 60 * 60 * 24;
        /// 用户订单记录
        // key: prefix:userId:sortType
        String AppointmentDoctorOrderListAoList_KEY_PREFIX = MedicineRedisKey.ID + ID + "AppointmentDoctorOrderListAoList:";
        // 过期时间: 4天
        long AppointmentDoctorOrderListAoList_EXPIRE_TIME = (long) 60 * 60 * 24 * 4;
    }
}
