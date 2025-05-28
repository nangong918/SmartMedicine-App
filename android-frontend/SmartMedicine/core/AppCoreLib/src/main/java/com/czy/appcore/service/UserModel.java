package com.czy.appcore.service;

import android.app.Application;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.czy.baseUtilsLib.file.SecuritySharedPreferencesUtils;
import com.czy.dal.ao.login.LoginTokenAo;
import com.czy.dal.ao.chat.UserLoginInfoAo;


/**
 * @author 13225
 */
public class UserModel {
    private static final String TAG = UserModel.class.getName();

    private static UserModel instance;

    public static String USER_INFO_FILE_NAME = "userInfo";
    private final SharedPreferences prefs;

    private UserModel(Application application) {
        this.prefs = SecuritySharedPreferencesUtils.getSecuritySharedPreferences(USER_INFO_FILE_NAME, application);
    }


    public static synchronized UserModel getInstance(Application application) {
        if (instance == null) {
            instance = new UserModel(application);
        }
        return instance;
    }

    // 设备访问Token和刷新Token

    public void saveLoginToken(LoginTokenAo loginTokenAo) {
        if (loginTokenAo != null) {
            try {
                loginTokenAo.saveToSharePreferences(prefs);
            } catch (Exception e) {
                Log.e(TAG, "saveLoginToken error", e);
            }
        }
        else {
            Log.w(TAG, "loginTokenAo is null");
        }
    }

    public LoginTokenAo getLoginTokenAo() {
        LoginTokenAo loginTokenAo = new LoginTokenAo();
        try {
            loginTokenAo.getFromSharePreferences(prefs);
            return loginTokenAo;
        } catch (Exception e) {
            Log.e(TAG, "getLoginTokenA0 error", e);
        }
        return null;
    }

    // 用户其他信息
    public void saveUserInfo(UserLoginInfoAo userLoginInfoAo){
//        if (prefs != null) {
//            userLoginInfoAo.saveToSharePreferences(prefs);
//        }
        if (userLoginInfoAo != null && !TextUtils.isEmpty(userLoginInfoAo.phone)) {
            try {
                userLoginInfoAo.saveToSharePreferences(prefs);
            } catch (Exception e) {
                Log.e(TAG, "saveUserInfo error", e);
            }
        }
        else {
            Log.w(TAG, "userLoginInfoAo is null");
        }
    }

    public UserLoginInfoAo getUserInfo(){
//        UserLoginInfoAo userLoginInfoAo = new UserLoginInfoAo();
//        userLoginInfoAo.getFromSharePreferences(prefs);
//        return userLoginInfoAo;
        try {
            UserLoginInfoAo userLoginInfoAo = new UserLoginInfoAo();
            userLoginInfoAo.getFromSharePreferences(prefs);
            return userLoginInfoAo;
        } catch (Exception e) {
            Log.e(TAG, "getUserInfo error", e);
        }
        return null;
    }
}
