package com.czy.smartmedicine.manager;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class HttpRequestManager {

    // 线程安全的Map<String, Boolean> 类似Redis
    private static final ConcurrentHashMap<String, Boolean> isFirstOpenMap = new ConcurrentHashMap<>(new HashMap<>());

    private static void setIsFirstOpenValue(String key){
        if (TextUtils.isEmpty(key)){
            return;
        }
        isFirstOpenMap.put(key, false);
    }

    // 判断某个key的变量是否第一次打开
    // MessageFragment；ChatActivity:ContactAccount
    public static boolean getIsFirstOpen(String key){
        if (TextUtils.isEmpty(key)){
            return false;
        }
        // 如果没有Key就是第一次打开
//        return Boolean.TRUE.equals(isFirstOpenMap.get(key));‘
        if (isFirstOpenMap.get(key) == null){
            setIsFirstOpenValue(key);
            return true;
        }
        return Boolean.TRUE.equals(isFirstOpenMap.get(key));
    }

    // 刷新全部值 (在网络断开的时候调用)
    public static void refreshAllValue(){
        isFirstOpenMap.clear();
    }
}
