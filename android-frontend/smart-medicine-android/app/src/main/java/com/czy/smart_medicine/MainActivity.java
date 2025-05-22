package com.czy.smart_medicine;


import android.widget.TextView;

import com.czy.baseUtilsLib.activity.BaseActivity;
import com.czy.smart_medicine.databinding.ActivityMainBinding;

/**
 * @author 13225
 */
public class MainActivity extends BaseActivity<ActivityMainBinding> {

    static {
        System.loadLibrary("smart_medicine");
    }


    public MainActivity() {
        super(MainActivity.class);
    }

    @Override
    protected void init() {
        super.init();

        // Example of a call to a native method
        TextView tv = binding.sampleText;
        tv.setText(stringFromJNI());
    }


    public native String stringFromJNI();
}