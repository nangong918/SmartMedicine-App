package com.czy.baseutil.network;




public interface OnBaseSuccessCallback<T>{
    void onResponse(BaseResponse<T> response);
}
