package com.czy.dal.ao.chat;

//import android.content.SharedPreferences;
//import android.text.TextUtils;

import com.czy.baseUtilsLib.json.BaseBean;
import com.czy.baseUtilsLib.object.BaseSharedPreferencesBean;

public class UserLoginInfoAo extends BaseSharedPreferencesBean implements BaseBean {
    public Long userId;
    public String account;
    public String phone;
    public String userName;

    public UserLoginInfoAo() {}

    public UserLoginInfoAo(Long userId, String account, String phone, String userName) {
        this.userId = userId;
        this.account = account;
        this.phone = phone;
        this.userName = userName;
    }

//    public void saveToSharePreferences(SharedPreferences sp) {
//        if (sp != null){
//            if (!TextUtils.isEmpty(account)){
//                sp.edit().putString("account", account).apply();
//            }
//            if (!TextUtils.isEmpty(phone)){
//                sp.edit().putString("phone", phone).apply();
//            }
//            if (!TextUtils.isEmpty(userName)){
//                sp.edit().putString("userName", userName).apply();
//            }
//        }
//    }
//
//    public void getFromSharePreferences(SharedPreferences sp) {
//        if (sp != null){
//            this.account = sp.getString("account", "");
//            this.phone = sp.getString("phone", "");
//            this.userName = sp.getString("userName", "");
//        }
//    }
}
