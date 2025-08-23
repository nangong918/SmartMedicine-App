package com.czy.baseUtilLib.network;




public interface OnBaseSuccessCallback<T>{
    void onResponse(BaseResponse<T> response);
}
