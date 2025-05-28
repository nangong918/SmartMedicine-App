package com.czy.dal.Do;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.czy.baseUtilsLib.json.BaseBean;

@Entity(tableName = "user")
public class UserDo implements BaseBean {
    @PrimaryKey
    @ColumnInfo(name = "uid")
    public String uid;
    @ColumnInfo(name = "user_name")
    public String userName;
    public String account;
    public String phone;
    // 头像的uri
    @ColumnInfo(name = "avatar_uri")
    public String avatarUri;
}
