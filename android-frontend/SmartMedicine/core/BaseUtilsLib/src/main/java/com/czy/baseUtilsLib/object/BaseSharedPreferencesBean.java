package com.czy.baseUtilsLib.object;

import android.content.SharedPreferences;

import com.czy.baseUtilsLib.file.SharedPreferencesBeanUtil;

public class BaseSharedPreferencesBean {

    public void saveToSharePreferences(SharedPreferences sp) throws Exception{
        SharedPreferencesBeanUtil.saveToSharedPreferences(this, sp);
    }

    public void getFromSharePreferences(SharedPreferences sp) throws Exception{
        SharedPreferencesBeanUtil.getFromSharedPreferences(this, sp);
    }

}
