package com.example.chattest.Utils;

import com.example.chattest.Utils.Type.R_dataType;

public interface CallBackInterface {
    void onSuccess(String callbackClass, R_dataType rData);
    void onFailure(String callbackClass);
}
