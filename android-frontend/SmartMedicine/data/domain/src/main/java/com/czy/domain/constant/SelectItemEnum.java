package com.czy.domain.constant;

public enum SelectItemEnum {
    HOME(0),
    MEDICAL(1),
    MESSAGE(2),
    MINE(3),
    ;

    private final int position;

    public static final String INTENT_EXTRA_NAME = "MainBottomBar.SelectItem";

    SelectItemEnum(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public static SelectItemEnum getItem(int position) {
        for (SelectItemEnum item : SelectItemEnum.values()) {
            if (item.getPosition() == position) {
                return item;
            }
        }
        return null;
    }
}
