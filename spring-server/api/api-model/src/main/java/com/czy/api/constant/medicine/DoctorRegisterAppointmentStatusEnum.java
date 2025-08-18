package com.czy.api.constant.medicine;

import com.czy.api.constant.BaseEnum;
import lombok.Getter;

/**
 * @author 13225
 * @date 2025/8/18 15:56
 * 预约list的状态
 */
@Getter
public enum DoctorRegisterAppointmentStatusEnum {

    // 可预约
    AVAILABLE("可预约", 1),
    // 已预约
    RESERVED("已预约", 2),
    // 已无可预约数量
    NO_AVAILABLE("已无可预约数量", 3),
    // 等待开放预约
    WAITING_OPEN("等待开放预约", 4),
    // 已过期
    EXPIRED("已过期", 5),
    ;

    private final String name;
    private final int code;

    DoctorRegisterAppointmentStatusEnum(String name, int code) {
        this.name = name;
        this.code = code;
    }

    // o -> BaseEnum
    public BaseEnum toBaseEnum() {
        return new BaseEnum() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public int getCode() {
                return code;
            }
        };
    }

    // code -> o
    public static DoctorRegisterAppointmentStatusEnum getByCode(int code) {
        for (DoctorRegisterAppointmentStatusEnum value : DoctorRegisterAppointmentStatusEnum.values()) {
            if (value.code == code) {
                return value;
            }
        }
        return WAITING_OPEN;
    }

}
