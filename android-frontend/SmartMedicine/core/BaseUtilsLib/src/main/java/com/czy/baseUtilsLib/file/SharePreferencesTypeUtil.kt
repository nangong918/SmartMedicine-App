package com.czy.baseUtilsLib.file

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

/**
 * 由于SharePreferences需要根据不同的类型进行存取操作，所以需要构建此类
 */
class SharePreferencesTypeUtil(appInstant: Application) {
    private var mPref: SharedPreferences? = null

    init {
        mPref = appInstant
            .getSharedPreferences(this.javaClass.simpleName, Context.MODE_PRIVATE)
    }

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param key
     * @param data
     */
    fun save(key: String?, data: Any) {
        val editor = mPref?.edit()
        when (data) {
            is String -> {
                editor?.putString(key, data)
            }

            is Int -> {
                editor?.putInt(key, data)
            }

            is Boolean -> {
                editor?.putBoolean(key, data)
            }

            is Float -> {
                editor?.putFloat(key, data)
            }

            is Long -> {
                editor?.putLong(key, data)
            }

            else -> {
                editor?.putString(key, data.toString())
            }
        }
        editor?.apply()
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param key
     * @param defaultObject
     * @return
     */
    fun get(key: String?, defaultObject: Any): Any {
        return when (defaultObject) {
            is String -> {
                mPref?.getString(key, defaultObject) ?: ""
            }

            is Int -> {
                mPref?.getInt(key, defaultObject) ?: 0
            }

            is Boolean -> {
                mPref?.getBoolean(key, defaultObject) ?: false
            }

            is Float -> {
                mPref?.getFloat(key, defaultObject) ?: 0f
            }

            is Long -> {
                mPref?.getLong(key, defaultObject) ?: 0L
            }

            else -> ""
        }
    }
}