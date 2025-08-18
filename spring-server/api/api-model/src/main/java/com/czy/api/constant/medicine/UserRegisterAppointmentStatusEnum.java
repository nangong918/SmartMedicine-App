package com.czy.api.constant.medicine;

import com.czy.api.constant.BaseEnum;
import lombok.Getter;

/**
 * @author 13225
 * @date 2025/8/18 15:56
 * 预约list的状态
 */
@Getter
public enum UserRegisterAppointmentStatusEnum {

    // 待预约
    AVAILABLE("待预约", 1),
    // 已预约
    RESERVED("已预约", 2),
    // 已取消
    CANCELED("已取消", 3),
    // 已完成
    COMPLETED("已完成", 4),
    // 预约失败
    FAILED("预约失败", 5),
    // 预约过期
    EXPIRED("预约过期", 6),
    ;

    private final String name;
    private final int code;

    UserRegisterAppointmentStatusEnum(String name, int code) {
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
    public static UserRegisterAppointmentStatusEnum getByCode(int code) {
        for (UserRegisterAppointmentStatusEnum value : UserRegisterAppointmentStatusEnum.values()) {
            if (value.code == code) {
                return value;
            }
        }
        return AVAILABLE;
    }

}
