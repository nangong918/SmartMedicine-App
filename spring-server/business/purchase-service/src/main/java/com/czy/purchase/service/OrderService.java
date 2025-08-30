package com.czy.purchase.service;

import com.czy.api.domain.dto.mq.AppointmentOrderDto;
import org.jetbrains.annotations.NotNull;

/**
 * @author 13225
 * @date 2025/8/26 9:35
 */
public interface OrderService {

    void handleOutTimeOrder(@NotNull AppointmentOrderDto dto);
}
