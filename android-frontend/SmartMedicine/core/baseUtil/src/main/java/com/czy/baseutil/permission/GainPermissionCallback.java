package com.czy.baseutil.permission;

/**
 * 权限获取之后的回调
 */
public interface GainPermissionCallback {
    /**
     * 获得了全部权限执行
     */
    void allGranted();

    /**
     * 存在没有获取的权限执行
     */
    void notGranted(String[] notGrantedPermissions);
}
