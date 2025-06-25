package com.czy.dal.vo.entity.addContact;



import com.czy.dal.OnPositionItemButtonContentClick;
import com.czy.dal.ao.newUser.AddUserStatusAo;
import com.czy.dal.constant.newUserGroup.ApplyButtonStatusEnum;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author 13225
 */
public class AddContactItemVo {
    // 静态变量用于生成自增 ID
    private static int currentId = 0;
    // 构造函数
    public AddContactItemVo() {
        this.id = generateId();
    }
    // 生成自增 ID
    private static int generateId() {
        return currentId++;
    }

    // vo的id：解决网络异步的时候Position会改变问题
    public int id;
    // 头像（支持网络 URL 或本地 URI）
    public String avatarUrlOrUri = "";
    // 名称
    public String name = "";
    // 账号
    public String account = "";
    // id
    public Long uid;
    // 添加状态
    public AddUserStatusAo addUserStatusAo = new AddUserStatusAo();
    // AddContactItemVo(对方)是被添加的
    public boolean isBeAdd = true;
    // 是否已添加
    public Integer[] buttonStates = new Integer[]{ApplyButtonStatusEnum.APPLY_ADD.code};
//    // 头像（支持网络 URL 或本地 URI）
//    public String avatarUrlOrUri = "";
//    // 名称
//    public String name = "";
//    // 账号
//    public String account = "";
//    // 是否已添加
//    public AddUserIsAgreeStateEnum isAdded = AddUserIsAgreeStateEnum.NOT_SELECT;
    // 点击回调
    public OnPositionItemButtonContentClick onPositionButtonContentClick = null;

    public boolean isItemEquals(Object o){
        if (o instanceof AddContactItemVo that){
            return this.account.equals(that.account);
        }
        return false;
    }

    public boolean isContentEquals(Object o){
        if (this == o) return true;
        if (o instanceof AddContactItemVo that){
            String thisArchiveUrl = avatarUrlOrUri == null ? "" : avatarUrlOrUri;
            String thatArchiveUrl = that.avatarUrlOrUri == null ? "" : that.avatarUrlOrUri;
            String thisName = name == null ? "" : name;
            String thatName = that.name == null ? "" : that.name;
            String thisAccount = account == null ? "" : account;
            String thatAccount = that.account == null ? "" : that.account;
            boolean thisIsAdded = addUserStatusAo.isBeAdd(account);
            boolean thatIsAdded = that.addUserStatusAo.isBeAdd(that.account);
            int thisButtonStatesLength = buttonStates.length;
            int thatButtonStatesLength = that.buttonStates.length;
            int thisButton = buttonStates[0];
            int thatButton = that.buttonStates[0];

            return thisArchiveUrl.equals(thatArchiveUrl) &&
                    thisName.equals(thatName) &&
                    thisAccount.equals(thatAccount) &&
                    thisIsAdded == thatIsAdded &&
                    thisButtonStatesLength == thatButtonStatesLength &&
                    thisButton == thatButton;
        }
        return false;
    }

    // DiffUtil 可以通过比较内容来判断两个对象是否相同
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AddContactItemVo that)) return false;

        // 使用 Optional 或直接检查 null
//        String thisAvatarUrl = avatarUrlOrUri.getValue() == null ? "" : avatarUrlOrUri.getValue();
//        String thatAvatarUrl = that.avatarUrlOrUri.getValue() == null ? "" : that.avatarUrlOrUri.getValue();
//        String thisName = name.getValue() == null ? "" : name.getValue();
//        String thatName = that.name.getValue() == null ? "" : that.name.getValue();
//        String thisAccount = account.getValue() == null ? "" : account.getValue();
//        String thatAccount = that.account.getValue() == null ? "" : that.account.getValue();
////        AddUserIsAgreeStateEnum thisIsAdded = isAddedStates.getValue() == null ? AddUserIsAgreeStateEnum.NOT_SELECT : isAddedStates.getValue();
////        AddUserIsAgreeStateEnum thatIsAdded = that.isAddedStates.getValue() == null ? AddUserIsAgreeStateEnum.NOT_SELECT : that.isAddedStates.getValue();
//
//        return thisAvatarUrl.equals(thatAvatarUrl) &&
//                thisName.equals(thatName) &&
//                thisAccount.equals(thatAccount) /*&&
//                thisIsAdded.equals(thatIsAdded)*/;
        String thisArchiveUrl = avatarUrlOrUri == null ? "" : avatarUrlOrUri;
        String thatArchiveUrl = that.avatarUrlOrUri == null ? "" : that.avatarUrlOrUri;
        String thisName = name == null ? "" : name;
        String thatName = that.name == null ? "" : that.name;
        String thisAccount = account == null ? "" : account;
        String thatAccount = that.account == null ? "" : that.account;
        boolean thisIsAdded = addUserStatusAo.isBeAdd(account);
        boolean thatIsAdded = that.addUserStatusAo.isBeAdd(that.account);
        int thisButtonStatesLength = buttonStates.length;
        int thatButtonStatesLength = that.buttonStates.length;
        int thisButton = buttonStates[0];
        int thatButton = that.buttonStates[0];

        return thisArchiveUrl.equals(thatArchiveUrl) &&
                thisName.equals(thatName) &&
                thisAccount.equals(thatAccount) &&
                thisIsAdded == thatIsAdded &&
                thisButtonStatesLength == thatButtonStatesLength &&
                thisButton == thatButton;
    }

    @Override
    public int hashCode() {
        return Objects.hash(avatarUrlOrUri, name, account, Arrays.hashCode(buttonStates));
    }
}
