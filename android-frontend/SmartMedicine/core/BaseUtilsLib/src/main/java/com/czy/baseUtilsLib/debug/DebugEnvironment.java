package com.czy.baseUtilsLib.debug;

import android.util.Log;

// 一些在Debug情况下的操作；Release情况下取消这些操作
public class DebugEnvironment {

    // Debug状态或者Release状态
    public static final Environment projectEnvironment = Environment.LOCAL;

    public static void logR(String message){
        if(projectEnvironment != Environment.PRODUCTION){
            Log.d("Runtime",message);
        }
    }

    public enum Environment {
        LOCAL("local", "本地环境"),
        TEST("test", "测试环境"),
        STAGING("staging", "正式(测试)环境"),
        PRODUCTION("production", "正式(线上)环境");

        private final String code;
        private final String description;

        Environment(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static Environment fromCode(String code) {
            for (Environment env : values()) {
                if (env.getCode().equalsIgnoreCase(code)) {
                    return env;
                }
            }
            throw new IllegalArgumentException("未知的环境代码: " + code);
        }
    }
}
