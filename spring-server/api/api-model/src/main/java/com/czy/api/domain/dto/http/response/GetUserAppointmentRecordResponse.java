package com.czy.api.domain.dto.http.response;

import com.czy.api.domain.ao.medicine.AppointmentDoctorOrderListAo;
import lombok.Data;

import java.util.List;

/**
 * @author 13225
 * @date 2025/8/25 16:09
 */
@Data
public class GetUserAppointmentRecordResponse {
    private List<AppointmentDoctorOrderListAo> currentOrders;
    private List<AppointmentDoctorOrderListAo> unprocessedOrders;
}
