package com.czy.buildsrc;

import org.gradle.api.JavaVersion;
import org.gradle.api.Project;

public class BuildConfig {

    /**
     * gradle的import
     */
    public static class Plugins{
        /**
         * 配置 plugins
         * @param project       Android的gradle项目，用于获取plugins
         * @param nameSpace     域名空间：library 或 application
         */
        public static void basePlugins(Project project, String nameSpace){
            project.getPlugins().apply(nameSpace);
            project.getPlugins().apply("kotlin-android");
            project.getPlugins().apply("kotlin-kapt");
        }
    }

    public static class Version{
        public static final String jvmTarget = "17";

        public static final int compileVersion = 34;
        public static final int targetVersion = 34;
        public static final int minVersion = 28;

        public static final int versionCode = 1;
        public static final String versionName = "1.0.0";
        public static final String cppFlags = "-std=c++11";
        public static final String cmakeVision = "3.22.1";

        public static final JavaVersion javaVersion = JavaVersion.VERSION_17;
    }

    public static class BuildFeature{
        public static final boolean multiDex = true;
        // 混淆
        public static final boolean minify = false;
        public static final boolean dataBinding = true;
        public static final boolean viewBinding = true;
        public static final boolean aidl = true;
    }

}