package com.czy.medicine.utils;

import com.czy.api.constant.medicine.AppointmentMerchantStatusEnum;
import com.czy.api.constant.medicine.MedicineConstant;
import com.czy.api.domain.bo.medicine.UserAppointmentOrderBo;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 13225
 * @date 2025/8/20 15:47
 * 计算当前的状态
 */
public class AppointmentMerchantStatusCalculator {

    @NotNull
    public static AppointmentMerchantStatusEnum calculate(
            int remainCount,
            @NotNull LocalDateTime now,
            int gapDays,
            @NotNull LocalDateTime beginDate,
            @NotNull LocalDateTime endDate
    ){
        // 剩余 0 个了
        if (remainCount <= 0){
            return AppointmentMerchantStatusEnum.NO_AVAILABLE;
        }
        if (gapDays < 0){
            gapDays = 0;
        }
        // 检查是否开放可购买: 提前开放n天
        // 计算出n天后的今天, 看看now + n days < beginDate ?
        LocalDateTime afterGapDays = now.plusDays(gapDays);
        if (afterGapDays.isBefore(beginDate)){
            return AppointmentMerchantStatusEnum.WAITING_OPEN;
        }
        // 检查是否结束
        if (now.isAfter(endDate)){
            return AppointmentMerchantStatusEnum.EXPIRED;
        }
        return AppointmentMerchantStatusEnum.AVAILABLE;
    }

    public static void calculateFillUserAppointmentOrderBos(
            @NotNull List<UserAppointmentOrderBo> bos
    ){
        for (UserAppointmentOrderBo bo : bos){
            if (bo == null || bo.getRemainCount() == null || bo.getBeginDate() == null || bo.getEndDate() == null){
                continue;
            }
            AppointmentMerchantStatusEnum merchantStatusEnum = calculate(
                    bo.getRemainCount(),
                    LocalDateTime.now(),
                    MedicineConstant.APPOINTMENT_OPEN_DAYS,
                    bo.getBeginDate(),
                    bo.getEndDate()
            );
            bo.setMerchantStatus(merchantStatusEnum.getCode());
        }
    }

}
