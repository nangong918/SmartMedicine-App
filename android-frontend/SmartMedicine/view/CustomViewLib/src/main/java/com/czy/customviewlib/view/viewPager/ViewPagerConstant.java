package com.czy.customviewlib.view.viewPager;

public enum ViewPagerConstant {
    USER(0),
    GROUP(1);

    private final int index;

    ViewPagerConstant(int index) {
        this.index = index;
    }

    // index
    public int getIndex() {
        return index;
    }

    // index -> ViewPagerConstant
    public static ViewPagerConstant getViewPagerConstant(int index) {
        for (ViewPagerConstant viewPagerConstant : ViewPagerConstant.values()) {
            if (viewPagerConstant.index == index) {
                return viewPagerConstant;
            }
        }
        return null;
    }

    // number
    public static int getViewPagerCount() {
        return ViewPagerConstant.values().length;
    }
}
