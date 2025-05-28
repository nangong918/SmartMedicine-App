package com.czy.appcore.utils.phone;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.czy.appcore.BaseConfig;
import com.czy.appcore.utils.OnTextInputEnd;
import com.czy.appcore.utils.TextChangeLegalCallback;

/**
 * 手机号合法工具s
 */
public class PhoneTextUtil {

    /**
     * 给TextView添加手机号合法判断和限制
     * @param editText      手机号输入框
     * @param callback      手机号合法和非法回调
     */
    public static void addPhoneTextChangeListener(EditText editText, TextChangeLegalCallback callback, OnTextInputEnd onTextInputEnd){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                onTextInputEnd.onTextInput(s,start,count,after);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                phoneNumberLengthLimit(s,editText);
                if(phoneNumberLegitimateJudge(editText.getText().toString())){
                    callback.legal();
                }
                else {
                    callback.illegal();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                onTextInputEnd.onTextEnd(s);
            }
        });
    }

    /**
     *  手机号最大输入长度限制
     * @param number    手机号
     * @param editText  输入框
     */
    private static void phoneNumberLengthLimit(CharSequence number, EditText editText){
        if (number != null) {
            // 1开头且为11位数（必须是1）
            if(number.length() > BaseConfig.PHONE_LENGTH){
                editText.setText(number.subSequence(0, BaseConfig.PHONE_LENGTH));
                editText.setSelection(BaseConfig.PHONE_LENGTH);
            }
        }
    }

    /**
     * 判断手机号是否合法
     * @param number    手机号
     * @return          合法？
     */
    public static boolean phoneNumberLegitimateJudge(String number) {
        if (number != null && !number.isEmpty()) {
            // 1开头且为11位数
            return number.startsWith(BaseConfig.PHONE_PREFIX) && number.length() == BaseConfig.PHONE_LENGTH;
        }
        return false;
    }

}
