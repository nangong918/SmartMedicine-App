package com.czy.appview.view.message;

public enum MessageViewPagerEnum {

    // message
    MESSAGE(0),
    // address book
    ADDRESS_BOOK(1),
    ;
    private final int index;
    MessageViewPagerEnum(int index) {
        this.index = index;
    }

    // index
    public int getIndex() {
        return index;
    }

    // index -> o
    public static MessageViewPagerEnum getEnumByIndex(int index) {
        for (MessageViewPagerEnum value : MessageViewPagerEnum.values()) {
            if (value.index == index) {
                return value;
            }
        }
        return MESSAGE;
    }

    public static int getCount() {
        return MessageViewPagerEnum.values().length;
    }

}
