package com.czy.appcore.network.api.handle;

public interface SyncRequestCallback {
    // 记录错误，并记录网络请求结束一条
    void onThrowable(Throwable throwable);
    // 全部请求执行完成
    void onAllRequestSuccess();
    String RESPONSE_BASE_ERROR = "ResponseTool.handleResponse存在问题";
}
