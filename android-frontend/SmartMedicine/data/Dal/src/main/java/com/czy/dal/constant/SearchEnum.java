package com.czy.dal.constant;

public enum SearchEnum {
    USER(0),
    GROUP(1),
    ARTICLE(2),
    VIDEO(3),
    MUSIC(4),
    PHOTO(5),
    DOCUMENT(6),
    OTHER(7);

    private final int code;

    public static final String INTENT_EXTRA_NAME = "SearchActivity.SearchType";

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
        return null;
    }
}
