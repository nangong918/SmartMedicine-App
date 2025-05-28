package com.czy.baseUtilsLib.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ActivityLaunchUtils {

    public interface IntentConfig {
        void configure(Intent intent);
    }

    /**
     * 启动新任务
     * @param context   上下文
     * @param intent    intent
     * @param config    配置
     */
    public static void launchNewTask(Context context, Intent intent, IntentConfig config) {
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        // 允许额外配置 Intent
        if (config != null) {
            config.configure(intent);
        }

        context.startActivity(intent);
    }

    public static <T extends Activity> void launchNewTask(Context context, Class<T> activityClass, IntentConfig config) {
        Intent intent = new Intent(context, activityClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        // 允许额外配置 Intent
        if (config != null) {
            config.configure(intent);
        }

        context.startActivity(intent);
    }


    public static <T extends Activity> void launch(Context context, Class<T> activityClass, IntentConfig config) {
        Intent intent = new Intent(context, activityClass);

        // 允许额外配置 Intent
        if (config != null) {
            config.configure(intent);
        }

        context.startActivity(intent);
    }

    public interface handleActivityLaunchResult{
        // Intent data = result.getData();
        // Uri uri = data.getData();
        void onActivityResult(ActivityResult result);
    }

    public static ActivityResultLauncher<Intent> getResultLauncher(
            AppCompatActivity activity,
            handleActivityLaunchResult callback
    ){
        return activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                callback::onActivityResult
        );
    }

    public static <T extends Activity> void launchForResult(
            @NonNull Activity activity,
            @NonNull ActivityResultLauncher<Intent> launcher,
            @NonNull Class<T> activityClass,
            @Nullable IntentConfig config) {

        Intent intent = new Intent(activity, activityClass);

        // 允许额外配置 Intent
        if (config != null) {
            config.configure(intent);
        }

        launcher.launch(intent);
    }

}
