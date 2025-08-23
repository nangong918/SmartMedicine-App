package com.czy.baseUtilLib.object;

import android.content.SharedPreferences;

import com.czy.baseUtilLib.file.SharedPreferencesBeanUtil;

public class BaseSharedPreferencesBean {

    public void saveToSharePreferences(SharedPreferences sp) throws Exception{
        SharedPreferencesBeanUtil.saveToSharedPreferences(this, sp);
    }

    public void getFromSharePreferences(SharedPreferences sp) throws Exception{
        SharedPreferencesBeanUtil.getFromSharedPreferences(this, sp);
    }

}
