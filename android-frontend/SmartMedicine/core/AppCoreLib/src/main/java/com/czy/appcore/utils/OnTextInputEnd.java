package com.czy.appcore.utils;

import android.text.Editable;

public interface OnTextInputEnd {
    void onTextInput(CharSequence s, int start, int count, int after);
    void onTextEnd(Editable s);
}
