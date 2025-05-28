package com.czy.baseUtilsLib.permission;


import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.content.pm.PackageManager;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import com.permissionx.guolindev.PermissionX;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @see PermissionUtil      权限static工具类
 * @see PermissionManager   权限非static工具类
 * @see PermissionX         直接申请工具类
 */
public class PermissionUtil {

    private static final String TAG = PermissionUtil.class.getSimpleName();

    /**
     * 获取需要请求的权限
     * @param activity      Activity
     * @param permissions   需要的权限
     * @return              未申请的权限
     */
    public static String[] getRequiredPermission(FragmentActivity activity, String[] permissions) {
        if (permissions == null || permissions.length < 1){
            return new String[]{};
        }
        List<String> requiredPermissions = new LinkedList<>();
        for (String permission : permissions){
            if (activity.checkSelfPermission(permission) != PERMISSION_GRANTED){
                requiredPermissions.add(permission);
            }
        }
        return requiredPermissions.toArray(new String[0]);
    }

    /**
     * 申请权限             缺点：需要提前注册ActivityResultLauncher<String[]>，不如PermissionX
     * @param activity                  Activity
     * @param permissions               权限数组
     * @param requestPermissionLauncher 请求权限的ActivityResultLauncher
     */
    public static void requestPermissions(FragmentActivity activity, String[] permissions, ActivityResultLauncher<String[]> requestPermissionLauncher) {
        List<String> permissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }

        if (!permissionList.isEmpty()) {
            String[] permissionsArray = permissionList.toArray(new String[0]);
            requestPermissionLauncher.launch(permissionsArray);
        }
    }

    /**
     * 获取申请权限的ActivityResultLauncher
     * @param activity                  Activity
     * @param requestPermissionCallback 权限授权回调
     * @return  ActivityResultLauncher
     */
    public static ActivityResultLauncher<String[]> getRequestPermissionLauncher(
            FragmentActivity activity,
            HandleRequestPermissionCallback requestPermissionCallback){
        return activity.registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                requestPermissionCallback::handlePermissionGranted
        );
    }


    /**
     * PermissionX 可以不用注册ActivityResultLauncher<String[]>
     * @param activity      Activity
     * @param permissions   权限数组
     * @param callback      回调
     */
    public static void requestPermissionsX(FragmentActivity activity, String[] permissions, GainPermissionCallback callback){
        PermissionX.init(activity)
                .permissions(permissions)
                .request((allGranted, grantedList, deniedList) -> {
                    if(allGranted){
                        Log.i(TAG, TAG + ":全部授权");
                        callback.allGranted();
                    }
                    else {
                        Log.i(TAG, TAG + ":未授权：");
                        Log.i(TAG,"grantedList:" + Arrays.toString(grantedList.toArray()));
                        Log.i(TAG,"deniedList:" + Arrays.toString(deniedList.toArray()));
                        String[] deniedArray = deniedList.toArray(new String[0]);
                        callback.notGranted(deniedArray);
                    }
                });
    }


    /**
     * 申请必要权限并执行
     * @param activity              activity
     * @param mustPermission        必须权限
     * @param optionalPermission    可选权限
     * @param callback              授权后回调
     */
    public static void requestPermissionSelectX(
            FragmentActivity activity,
            String[] mustPermission,
            String[] optionalPermission,
            GainPermissionCallback callback) {

        // 合并必须和可选权限
        String[] allPermissions = new String[mustPermission.length + optionalPermission.length];
        System.arraycopy(mustPermission, 0, allPermissions, 0, mustPermission.length);
        System.arraycopy(optionalPermission, 0, allPermissions, mustPermission.length, optionalPermission.length);

        PermissionX.init(activity)
                .permissions(allPermissions)
                .request((allGranted, grantedList, deniedList) -> {
                    // 检查必须权限是否全部授予
                    boolean mustGranted = true;
                    for (String permission : mustPermission) {
                        if (!grantedList.contains(permission)) {
                            mustGranted = false;
                            break;
                        }
                    }

                    // 全部授权或者必须的权限全部授权
                    if (allGranted) {
                        Log.i(TAG, "全部授权");
                        callback.allGranted();
                    }
                    if (mustGranted) {
                        Log.i(TAG, "必要权限全部授权");
                        List<String> notOptionalPermission;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                            notOptionalPermission = deniedList
                                    .stream()
                                    .filter(
                                            permission -> !Arrays.asList(mustPermission).contains(permission)
                                    )
                                    .toList();
                        }
                        else {
                            notOptionalPermission = deniedList
                                    .stream()
                                    .filter(
                                            permission -> !Arrays.asList(mustPermission).contains(permission)
                                    )
                                    .collect(Collectors.toList());
                        }
                        Log.w(TAG, "未授权的非必要权限有：" + notOptionalPermission);
                        callback.allGranted();
                    }
                    else {
                        Log.w(TAG, "存在未授权的必须权限：");
                        Log.i(TAG, "已授权: " + Arrays.toString(grantedList.toArray()));
                        Log.w(TAG, "未授权: " + Arrays.toString(deniedList.toArray()));
                        // 记录未授权的必须权限
                        List<String> deniedMustPermissions = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
                            deniedMustPermissions = deniedList
                                    .stream()
                                    .filter(deniedList::contains)
                                    .toList();
                        }
                        else {
                            deniedMustPermissions = Arrays.stream(mustPermission)
                                    .filter(deniedList::contains)
                                    .collect(Collectors.toList());
                        }
                        Log.w(TAG, "未授权的必要权限有：" + deniedMustPermissions);
                        // 记录未授权的非必要权限
                        List<String> deniedOptionalPermissions = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
                            deniedOptionalPermissions = Arrays.stream(optionalPermission)
                                    .filter(deniedList::contains)
                                    .toList();
                        }
                        else {
                            deniedOptionalPermissions = Arrays.stream(optionalPermission)
                                    .filter(deniedList::contains)
                                    .collect(Collectors.toList());
                        }
                        Log.w(TAG, "未授权的非必要权限有：" + deniedOptionalPermissions);
                        String[] deniedArray = deniedList.toArray(new String[0]);
                        callback.notGranted(deniedArray);
                    }
                });
    }

    public static void requestPermissions_old(FragmentActivity activity, String[] permissions, int requestCode) {
        List<String> permissionList = new ArrayList<>();

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }

        if (!permissionList.isEmpty()) {
            String[] permissionsArray = permissionList.toArray(new String[0]);
            // 还需要监听Activity的结果，code还要对应
            ActivityCompat.requestPermissions(activity, permissionsArray, requestCode);
        }
    }

}
