package com.czy.dal.constant;

public enum SearchEnum {
    USER(0),
    GROUP(1),
    POST(2),
    PRODUCTS(3),
    OTHER(4);

    private final int code;

    SearchEnum(int code) {
        this.code = code;
    }

    public int getPosition() {
        return code;
    }

    // code -> item
    public static SearchEnum getItem(int code) {
        for (SearchEnum item : SearchEnum.values()) {
            if (item.getPosition() == code) {
                return item;
            }
        }
        return USER;
    }
}
