package com.czy.dal.vo.view.mainTop;

import com.czy.dal.constant.SelectItemEnum;

public class MainTopBarVo {
    public SelectItemEnum selectItemEnum = SelectItemEnum.HOME;
    public OnFriendCallback onFriendCallback;

    public MainTopBarVo() {
    }

    public MainTopBarVo(SelectItemEnum selectItemEnum) {
        this.selectItemEnum = selectItemEnum;
    }

    public MainTopBarVo(SelectItemEnum selectItemEnum, OnFriendCallback onFriendCallback) {
        this.selectItemEnum = selectItemEnum;
        this.onFriendCallback = onFriendCallback;
    }
}
