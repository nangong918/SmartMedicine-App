package com.czy.appcore.utils.vcode;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.lifecycle.MutableLiveData;

import com.czy.appcore.BaseConfig;
import com.czy.appcore.utils.OnTextInputEnd;
import com.czy.appcore.utils.TextChangeLegalCallback;

public class VcodeTextUtil {

    public static void addPhoneTextChangeListener(EditText editText, TextChangeLegalCallback callback, OnTextInputEnd onTextInputEnd){
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                onTextInputEnd.onTextInput(s,start,count,after);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                vcodeLengthLimit(s,editText);
                if(vcodeLegitimateJudge(editText.getText().toString())){
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

    public static void addPhoneTextChangeListener(MutableLiveData<String> vcodeLd, EditText editText, TextChangeLegalCallback callback, OnTextInputEnd onTextInputEnd){
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                onTextInputEnd.onTextInput(s,start,count,after);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                vcodeLengthLimit(s,editText);
                if(vcodeLegitimateJudge(editText.getText().toString())){
                    vcodeLd.setValue(editText.getText().toString());
                    callback.legal();
                }
                else {
                    vcodeLd.setValue(editText.getText().toString());
                    callback.illegal();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                onTextInputEnd.onTextEnd(s);
            }
        });
    }

    private static void vcodeLengthLimit(CharSequence number, EditText editText){
        if (number != null && number.length() > BaseConfig.V_CODE_LENGTH){
            // 6位数
            editText.setText(number.subSequence(0, BaseConfig.V_CODE_LENGTH));
            editText.setSelection(BaseConfig.V_CODE_LENGTH);
        }
    }

    private static boolean vcodeLegitimateJudge(String number){
        if (number != null && !number.isEmpty()){
            // 6位数
            return number.length() == BaseConfig.V_CODE_LENGTH;
        }
        return false;
    }
}
