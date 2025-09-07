package com.czy.api.constant.medicine;

/**
 * @author 13225
 * @date 2025/4/18 18:20
 */
public class MedicineConstant {
    /// service
    public static final String serviceName = "medicine-service";
    // serviceRoute
    public static final String serviceRoute = "/" + serviceName;
    // Knowledge_CONTROLLER
    public static final String Knowledge_CONTROLLER = "/knowledge";
    // RegisterAppointment_CONTROLLER
    public static final String RegisterAppointment_CONTROLLER = "/registerAppointment";

    // serviceUri
    public static final String serviceUri = "lb://" + serviceName;

    /// api
    // 预约
    public static final String APPOINTMENT = "/appointment";
    // 取消预约/退款
    public static final String CANCEL = "/cancel";

    /// constant
    // 一共多少日期可看
    public static final int TOTAL_APPOINTMENT_DATE = 4;
    // 可开放的天数
    public static final int APPOINTMENT_OPEN_DAYS = 3;
}
