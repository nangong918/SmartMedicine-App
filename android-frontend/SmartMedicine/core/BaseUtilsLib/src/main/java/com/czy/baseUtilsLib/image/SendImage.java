package com.czy.baseUtilsLib.image;

import com.czy.baseUtilsLib.network.OnThrowableCallback;

import okhttp3.RequestBody;

public interface SendImage {
    void sendImage(RequestBody paramsBody, OnThrowableCallback callback);
}
