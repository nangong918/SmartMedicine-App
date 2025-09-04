package com.czy.appview.view.medicine;

public enum MedicineViewPagerEnum {

    // 挂号预约
    APPOINTMENT(0),
    // 医疗百科
    MEDICAL_WIKI(1),
    // 医疗购物
    MEDICAL_SHOPPING(2),
    // AI问答
    AI_QUESTION(3),
    // 健康提醒
    HEALTH_REMINDER(4),
    ;
    private final int index;
    MedicineViewPagerEnum(int index) {
        this.index = index;
    }

    // index
    public int getIndex() {
        return index;
    }

    // index -> o
    public static MedicineViewPagerEnum getEnumByIndex(int index) {
        for (MedicineViewPagerEnum value : MedicineViewPagerEnum.values()) {
            if (value.index == index) {
                return value;
            }
        }
        return APPOINTMENT;
    }

    public static int getCount() {
        return MedicineViewPagerEnum.values().length;
    }

}
