package com.czy.appcore.network.api.handle;

public interface AsyncRequestCallback {
    // 记录错误，并记录网络请求结束一条
    void onThrowable(Throwable throwable);
    // 记录网络请求结束一条
    void onSingleRequestSuccess();
    String RESPONSE_BASE_ERROR = "ResponseTool.handleResponse存在问题";
}
