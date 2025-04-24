package com.example.chattest.Test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.chattest.R;
import com.example.chattest.Utils.ImageUtils;
import com.example.chattest.databinding.ActivityTestBinding;

public class TestActivity extends AppCompatActivity implements testCallback {

    private ActivityTestBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        HTTPTest.callback = this;
        HTTPTest.StartThread(1);


    }

    @Override
    public void onSuccess() {
        binding.ContentTest.setText(HTTPTest.Context);
        ImageUtils imageUtils = new ImageUtils();
        imageUtils.SetImageByByte(binding.ImageTest,HTTPTest.Image,R.drawable.chat_ai);
    }

    @Override
    public void onFailure() {
        Log.d("Runtime", "callback onFailure: "+HTTPTest.flag);
    }
}