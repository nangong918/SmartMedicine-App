package com.czy.medicine.utils;

import com.czy.api.constant.UserOrderStatusEnum;
import com.czy.api.domain.Do.medicine.UserCustomerAppointmentDo;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

/**
 * @author 13225
 * @date 2025/8/22 14:21
 */
public class UserOrderStatusUtils {

    /**
     * 检查是否可以申请预约
     * @param orderList 当前订单列表
     * @return  是否可以预约
     */
    public static boolean checkAppointment(List<UserCustomerAppointmentDo> orderList){
        if (CollectionUtils.isEmpty(orderList)){
            return true;
        }

        for (UserCustomerAppointmentDo orderDo : orderList){
            // 是否不可预约
            if (checkCantAppointment(orderDo)){
                // 返回不可申请
                return false;
            }
        }

        return true;
    }

    public static boolean checkCantAppointment(UserCustomerAppointmentDo orderDo){
        if (orderDo == null){
            // 不存在则可以申请
            return false;
        }
        Integer status = Optional.ofNullable(orderDo.getUserOrderStatus())
                .orElse(UserOrderStatusEnum.NULL.getCode());

        return  // 审核中
                status == UserOrderStatusEnum.WAITING_AUDIT.getCode() ||
                // 待支付
                status.equals(UserOrderStatusEnum.WAITING_PAYMENT.getCode()) ||
                // 待使用
                status.equals(UserOrderStatusEnum.WAITING_USE.getCode()) ||
                // 退款中
                status.equals(UserOrderStatusEnum.REFUNDING.getCode()) ||
                // 退款失败
                status.equals(UserOrderStatusEnum.REFUND_FAILED.getCode());
    }

}
