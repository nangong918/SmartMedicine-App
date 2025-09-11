package com.czy.domain.dto.http.response;


import com.czy.domain.ao.medicine.AppointmentDoctorOrderListAo;

import java.util.List;

/**
 * @author 13225
 * @date 2025/8/25 16:09
 */
public class GetUserAppointmentRecordResponse {
    public List<AppointmentDoctorOrderListAo> currentOrders;
    public List<AppointmentDoctorOrderListAo> unprocessedOrders;
}
