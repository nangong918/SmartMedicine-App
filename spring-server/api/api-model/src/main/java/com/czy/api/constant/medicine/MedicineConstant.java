package com.czy.api.constant.medicine;

/**
 * @author 13225
 * @date 2025/4/18 18:20
 */
public class MedicineConstant {
    public static final String serviceName = "medicine-service";
    // serviceRoute
    public static final String serviceRoute = "/" + serviceName;
    // Knowledge_CONTROLLER
    public static final String Knowledge_CONTROLLER = "/knowledge";
    // RegisterAppointment_CONTROLLER
    public static final String RegisterAppointment_CONTROLLER = "/registerAppointment";

    // serviceUri
    public static final String serviceUri = "lb://" + serviceName;
}
