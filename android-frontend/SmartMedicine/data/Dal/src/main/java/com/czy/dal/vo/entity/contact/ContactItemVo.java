package com.czy.dal.vo.entity.contact;


import java.util.Objects;

/**
 * @author 13225
 */
public class ContactItemVo {
    // 静态变量用于生成自增 ID
    private static int currentId = 0;
    public ContactItemVo() {
        this.id = currentId++;
    }
    // vo id
    public int id;
    // 头像（支持网络 URL 或本地 URI）
    public String avatarUrlOrUri = "";
    // 名称
    public String name = "";
    // 账号
    public String account = "";

    // DiffUtil 可以通过比较内容来判断两个对象是否相同
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContactItemVo that)) return false;

//        String thisAvatarUrl = avatarUrlOrUri.getValue() == null ? "" : avatarUrlOrUri.getValue();
//        String thatAvatarUrl = that.avatarUrlOrUri.getValue() == null ? "" : that.avatarUrlOrUri.getValue();
//        String thisName = name.getValue() == null ? "" : name.getValue();
//        String thatName = that.name.getValue() == null ? "" : that.name.getValue();
//        String thisAccount = account.getValue() == null ? "" : account.getValue();
//        String thatAccount = that.account.getValue() == null ? "" : that.account.getValue();
//
//        return thisAvatarUrl.equals(thatAvatarUrl) &&
//                thisName.equals(thatName) &&
//                thisAccount.equals(thatAccount);

        String thisAvatarUrl = avatarUrlOrUri == null ? "" : avatarUrlOrUri;
        String thatAvatarUrl = that.avatarUrlOrUri == null ? "" : that.avatarUrlOrUri;
        String thisName = name == null ? "" : name;
        String thatName = that.name == null ? "" : that.name;
        String thisAccount = account == null ? "" : account;
        String thatAccount = that.account == null ? "" : that.account;

        return thisAvatarUrl.equals(thatAvatarUrl) &&
                thisName.equals(thatName) &&
                thisAccount.equals(thatAccount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(avatarUrlOrUri, name, account);
    }
}
