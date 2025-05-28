package com.czy.baseUtilsLib.file;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import android.util.Log;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class SecuritySharedPreferencesUtils {

    protected final static String TAG = SecuritySharedPreferencesUtils.class.getSimpleName();

    public static SharedPreferences getSecuritySharedPreferences(String fileName, Application appInstance){
        if (fileName == null || fileName.isEmpty()){
            Log.e(TAG, "fileName 不能为空");
            return null;
        }
        try {
            KeyGenParameterSpec keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC;
            String masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec);
            return EncryptedSharedPreferences.create(
                    fileName,
                    masterKeyAlias,
                    appInstance,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            Log.e(TAG, "创建加密 SharedPreferences 失败: " + e.getMessage(), e);
            return appInstance.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        }
    }

    public static void clearSecuritySharedPreferences(String fileName, Application appInstance) {
        SharedPreferences sp = getSecuritySharedPreferences(fileName, appInstance);
        if (sp != null) {
            SharedPreferences.Editor editor = sp.edit();
            editor.clear();  // 清空所有数据
            editor.apply();  // 提交更改
            Log.d(TAG, "成功清空 SharedPreferences: " + fileName);
        } else {
            Log.e(TAG, "无法获取 SharedPreferences，无法清空数据");
        }
    }

}
