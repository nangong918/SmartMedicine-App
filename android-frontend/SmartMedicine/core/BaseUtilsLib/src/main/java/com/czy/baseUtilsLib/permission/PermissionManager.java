package com.czy.baseUtilsLib.permission;


import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.FragmentActivity;

import com.permissionx.guolindev.PermissionX;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @see PermissionUtil      权限static工具类
 * @see PermissionManager   权限非static工具类
 * @see PermissionX         直接申请工具类
 */
public class PermissionManager {

    private static final String TAG = PermissionManager.class.getSimpleName();

    // 请求的权限
    private final String[] requiredPermissions;
    // 未拥有的权限
    private final String[] notHavePermissions;
    // 权限请求启动器
    private final ActivityResultLauncher<String[]> requestPermissionLauncher;
    // 权限授权回调
    private final GainPermissionCallback gainPermissionCallback;
    // 必须的权限
    private String[] mustPermission;
    // 非必须的权限
    private String[] optionalPermission;

    /**
     * 构造函数
     * @param activity              fragmentActivity (ActivityResultLauncher需要)
     * @param requiredPermissions   需要申请的权限
     * @param callback              权限授权回调
     */
    public PermissionManager(FragmentActivity activity, String[] requiredPermissions, GainPermissionCallback callback){
        this.requiredPermissions = requiredPermissions;
        this.notHavePermissions = PermissionUtil.getRequiredPermission(activity, this.requiredPermissions);
        this.gainPermissionCallback = callback;
        // 此处统一全部检查，如果需要更细粒度的权限授权检查，请自行实现
        requestPermissionLauncher = PermissionUtil.getRequestPermissionLauncher(activity, permissionStatus -> {
            // 检查permissionStatus Map是否全为true
            boolean allGranted = true;
            List<String> notGrantedPermissionList = new LinkedList<>();
            for (Map.Entry<String, Boolean> entry : permissionStatus.entrySet()) {
                if (!entry.getValue()) {
                    allGranted = false;
                    notGrantedPermissionList.add(entry.getKey());
                    break;
                }
            }
            if (allGranted){
                Log.d(TAG, "所有权限已授权");
                this.gainPermissionCallback.allGranted();
            }
            else{
                String[] notGrantedPermissions = notGrantedPermissionList.toArray(new String[0]);
                Log.d(TAG, "未授权权限：" + Arrays.toString(notGrantedPermissions));
                this.gainPermissionCallback.notGranted(notGrantedPermissions);
            }
        });
    }

    public PermissionManager(FragmentActivity activity, String[] mustPermission,
                             String[] optionalPermission, GainPermissionCallback callback){
        // 合并必须和可选权限
        String[] allPermissions = new String[mustPermission.length + optionalPermission.length];
        System.arraycopy(mustPermission, 0, allPermissions, 0, mustPermission.length);
        System.arraycopy(optionalPermission, 0, allPermissions, mustPermission.length, optionalPermission.length);
        this.requiredPermissions = allPermissions;
        this.notHavePermissions = PermissionUtil.getRequiredPermission(activity, this.requiredPermissions);
        this.gainPermissionCallback = callback;

        this.mustPermission = mustPermission;
        this.optionalPermission = optionalPermission;

        requestPermissionLauncher = PermissionUtil.getRequestPermissionLauncher(activity, permissionStatus -> {
            // 检查permissionStatus Map是否全为true
            boolean allGranted = true;
            List<String> notGrantedPermissionList = new LinkedList<>();

            for (Map.Entry<String, Boolean> entry : permissionStatus.entrySet()) {
                if (!entry.getValue()) {
                    allGranted = false;
                    notGrantedPermissionList.add(entry.getKey());
                }
            }

            // 全部授权
            if (allGranted) {
                Log.i(TAG, "全部授权");
                this.gainPermissionCallback.allGranted();
            }
            else {
                // 检查必须权限是否全部授权
                boolean mustGranted = true;
                for (String permission : this.mustPermission) {
                    if (Boolean.FALSE.equals(permissionStatus.getOrDefault(permission, false))) {
                        mustGranted = false;
                        break;
                    }
                }

                if (mustGranted) {
                    Log.i(TAG, "必要权限全部授权");
                    List<String> deniedList = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        deniedList = notGrantedPermissionList.stream()
                                .filter(permission -> !Arrays.asList(mustPermission).contains(permission))
                                .toList();
                    }
                    else {
                        deniedList = notGrantedPermissionList.stream()
                                .filter(permission -> !Arrays.asList(mustPermission).contains(permission))
                                .collect(Collectors.toList());
                    }
                    Log.w(TAG, "未授权的非必要权限有：" + deniedList);
                    callback.allGranted();
                }
                else {
                    Log.w(TAG, "存在未授权的必须权限：");
                    Log.i(TAG, "已授权: " + Arrays.toString(permissionStatus.keySet().stream().filter(permissionStatus::get).toArray()));
                    Log.w(TAG, "未授权: " + Arrays.toString(notGrantedPermissionList.toArray()));

                    // 记录未授权的必须权限
                    List<String> deniedMustPermissions = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        deniedMustPermissions = Arrays.stream(this.mustPermission)
                                .filter(notGrantedPermissionList::contains)
                                .toList();
                    }
                    else {
                        deniedMustPermissions = Arrays.stream(this.mustPermission)
                                .filter(notGrantedPermissionList::contains)
                                .collect(Collectors.toList());
                    }
                    Log.w(TAG, "未授权的必要权限有：" + deniedMustPermissions);

                    // 记录未授权的非必要权限
                    List<String> deniedOptionalPermissions = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        deniedOptionalPermissions = Arrays.stream(this.optionalPermission)
                                .filter(notGrantedPermissionList::contains)
                                .toList();
                    }
                    else {
                        deniedOptionalPermissions = Arrays.stream(this.optionalPermission)
                                .filter(notGrantedPermissionList::contains)
                                .collect(Collectors.toList());
                    }
                    Log.w(TAG, "未授权的非必要权限有：" + deniedOptionalPermissions);

                    String[] deniedArray = notGrantedPermissionList.toArray(new String[0]);
                    this.gainPermissionCallback.notGranted(deniedArray);
                }
            }
        });
    }

    /**
     * 执行：没有权限申请权限，有权限直接回调
     */
    public void permissionsDone(){
        if (notHavePermissions.length < 1){
            Log.d(TAG, "权限已全部授权");
            gainPermissionCallback.allGranted();
        }
        else {
            Log.d(TAG, "未授权权限：" + Arrays.toString(notHavePermissions));
            requestPermissionLauncher.launch(notHavePermissions);
        }
    }

    public String[] getRequiredPermissions() {
        return requiredPermissions;
    }
}
