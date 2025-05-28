package com.czy.customviewlib.view.home;

public interface OnRecommendCardClick {
    /**
     * 推荐卡片点击
     * @param position  行索引
     * @param cardType  卡片类型：1 单个卡片，2 两个卡片
     * @see com.czy.dal.constant.home.RecommendCardType
     * @param cardId    卡片id：两个卡片中的哪个？0是第一个，1是第二个；但卡片情况此值无效
     */
    void onCardClick(int position, int cardType, int cardId);

    /**
     * 推荐卡片按钮点击
     * @param position      行索引
     * @param cardType      卡片类型：1 单个卡片，2 两个卡片
     * @see com.czy.dal.constant.home.RecommendCardType
     * @param cardId        卡片id：两个卡片中的哪个？0是第一个，1是第二个；但卡片情况此值无效
     * @param buttonType    按钮类型：1 点赞，2 收藏，3 不喜欢
     * @see com.czy.dal.constant.home.RecommendButtonType
     */
    void onButtonClick(int position, int cardType, int cardId, int buttonType);
}
