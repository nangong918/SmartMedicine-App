package com.czy.dal.constant;

public enum SelectItemEnum {
    HOME(0),
    SEARCH(1),
    AI(2),
    FRIENDS(3),
    NOTIFICATIONS(4),
    MESSAGE(5);

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
