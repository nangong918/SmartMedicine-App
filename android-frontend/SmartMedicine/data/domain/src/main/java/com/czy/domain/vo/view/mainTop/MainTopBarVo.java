package com.czy.domain.vo.view.mainTop;

import com.czy.domain.constant.SelectItemEnum;

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
