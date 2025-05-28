package com.czy.baseUtilsLib.language;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;


import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;


// TODO 解决生命周期管理存在的问题
// TODO 尝试直接配置静态国际化
public class LanguageUtil {

    private static final String TAG = LanguageUtil.class.getSimpleName();

    /*语言类型：
     * 此处支持3种语言类型，更多可以自行添加。
     * */
    public static final String ENGLISH = "en";
    public static final String CHINESE = "ch";

    private static HashMap<String, Locale> languagesList;

    private LanguageUtil(){
        init();
    }

    private static void init(){
        languagesList = new HashMap<>(){{
            put(ENGLISH, Locale.ENGLISH);
            put(CHINESE, Locale.CHINESE);
        }};
    }

    private static HashMap<String, Locale> getLanguagesList(){
        if(languagesList == null){
            init();
        }
        return languagesList;
    }

    /**
     * 修改语言
     *
     * @param activity 上下文
     * @param language 例如修改为 英文传“en”，参考上文字符串常量
     * @param cls      要跳转的类（一般为入口类）
     */
    public static void changeAppLanguage(Activity activity, String language, Class<?> cls) {
        Resources resources = activity.getResources();
        Configuration configuration = resources.getConfiguration();
        // app locale 默认简体中文
        Locale locale = getLocaleByLanguage(language.isEmpty() ? "zh" : language);

        // 更新 Configuration
        configuration.setLocale(locale);
        // 使用新的方法更新 Context
        Context context = activity.createConfigurationContext(configuration);
        activity.getResources().getConfiguration().setLocale(locale);

        //finish();
        // 重启 app
        Intent intent = new Intent(context, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

/*        DisplayMetrics dm = resources.getDisplayMetrics();
        resources.updateConfiguration(configuration, dm);

        //finish();
        // 重启app
        Intent intent = new Intent(activity, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);*/

        //加载动画
        //activity.overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
        //activity.overridePendingTransition(0, 0);

        Log.i(TAG,"设置的语言：" + language);
    }


    /**
     * 获取指定语言的locale信息，如果指定语言不存在
     * 返回本机语言，如果本机语言不是语言集合中的一种，返回英语
     */
    private static Locale getLocaleByLanguage(String language) {
        if (isContainsKeyLanguage(language)) {
            return getLanguagesList().get(language);
        } else {
            Locale locale = Locale.getDefault();
            for (String key : getLanguagesList().keySet()) {
                if (TextUtils.equals(Objects.requireNonNull(getLanguagesList().get(key)).getLanguage(), locale.getLanguage())) {
                    return locale;
                }
            }
        }
        return Locale.ENGLISH;
    }


    /**
     * 如果此映射包含指定键的映射关系，则返回 true
     */
    private static boolean isContainsKeyLanguage(String language) {
        return getLanguagesList().containsKey(language);
    }

}
