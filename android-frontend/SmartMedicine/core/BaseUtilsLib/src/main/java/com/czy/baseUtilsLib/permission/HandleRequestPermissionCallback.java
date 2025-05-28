package com.czy.baseUtilsLib.permission;

import java.util.Map;


public interface HandleRequestPermissionCallback {
    /**
     * 权限申请结果
     * @param permissionStatus  权限申请结果
     */
    void handlePermissionGranted(Map<String, Boolean> permissionStatus);
}
