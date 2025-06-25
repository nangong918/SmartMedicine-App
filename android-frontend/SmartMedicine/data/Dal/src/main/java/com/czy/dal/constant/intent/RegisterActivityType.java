package com.czy.dal.constant.intent;


/**
 * 注册 1
 * 重置密码 2
 */
public enum RegisterActivityType {
    REGISTER(1),
    RESET_PWD(2);

    private final int type;

    RegisterActivityType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public static RegisterActivityType getType(int type) {
        for (RegisterActivityType registerActivityType : RegisterActivityType.values()) {
            if (registerActivityType.getType() == type) {
                return registerActivityType;
            }
        }
        return REGISTER;
    }
}
