package com.example.chattest.Test;

import android.util.Log;

public class MyDebug {
    public static final String TAG = "Runtime";
    public static void Print(String data){
        Log.d(TAG,data);
    }
}
