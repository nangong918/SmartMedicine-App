package com.example.chattest.Sign;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.chattest.Test.MyDebug;
import com.example.chattest.URL.UrlUtil;
import com.example.chattest.Utils.CallBackInterface;
import com.example.chattest.Utils.ImageUtils;
import com.example.chattest.Utils.RequestUtils;
import com.example.chattest.Utils.Type.R_Util;
import com.example.chattest.Utils.Type.R_dataType;
import com.example.chattest.databinding.ActivitySignUpBinding;

import java.io.IOException;

public class SignUp extends AppCompatActivity implements CallBackInterface {
    private ActivitySignUpBinding binding;
    private SignUp THIS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Init();

        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setListeners();
    }

    private void Init(){
        THIS = this;
        signUpNewAccountType = new SignUpNewAccountType();
    }

    private SignUpNewAccountType signUpNewAccountType;
    private final String CheckAccountRequest = "SignUp_checkAccount";
    private final String SignUpNewAccountRequest = "SignUp_sendNewAccount";
    private void setListeners(){
        binding.textSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.userFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(view);
            }
        });

        binding.buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String error = SignUp_with_data();
                if(!error.equals("")){
                    Toast.makeText(THIS,"注册失败："+error,Toast.LENGTH_SHORT).show();
                    return;
                }
                if(ImageSelectJudge){
                    ImageUtils utils = new ImageUtils();
                    try {
                        signUpNewAccountType.NewUser_imageBytes = utils.resizeImageIfNeeded(utils.uriToBitmap(THIS,Image_uri));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                String Account = binding.SignUpInputEmail.getText().toString();
                RequestUtils checkAccountRequest = new RequestUtils(5000,CheckAccountRequest,"GET", UrlUtil.Get_checkAccount_url(Account));
                checkAccountRequest.callback = THIS;
                checkAccountRequest.StartThread();
            }
        });
    }

    //---------------注册事件------------------

    private String SignUp_with_data(){
        String Error = "";
        String Name = binding.SignUpInputName.getText().toString();
        String Account = binding.SignUpInputEmail.getText().toString();
        String password = binding.SignUpInputPassword.getText().toString();
        String confirmPassword = binding.SignUpInputConfirmPassword.getText().toString();

        if(Name.isEmpty()){
            Error = "Error:Name is empty!";
        }
        else if (Account.isEmpty()){
            Error = "Error:Account is empty!";
        }
        else if (password.isEmpty()){
            Error = "Error:password is empty!";
        }
        else if (confirmPassword.isEmpty()){
            Error = "Error:confirmPassword is empty!";
        }
        else if (!confirmPassword.equals(password)){
            Error = "Error:Password is not equals to confirm Password!";
        }

        signUpNewAccountType.Account = Account;
        signUpNewAccountType.Name = Name;
        signUpNewAccountType.Password = password;

        return Error;
    }

    private void SignUp_NewAccount(){
        assert signUpNewAccountType != null;
        R_dataType rDataType = new R_dataType(signUpNewAccountType);
        String jsonData = R_Util.R_JsonUtils.toJson(rDataType);
        RequestUtils checkAccountRequest = new RequestUtils(5000,SignUpNewAccountRequest,"POST", UrlUtil.Get_SignUp_url(),jsonData);
        checkAccountRequest.callback = THIS;
        checkAccountRequest.StartThread();
    }

    //---------------从相册选择照片------------------


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (THIS != null) {
            THIS.onFragmentResult(requestCode, resultCode, data);
        }
    }

    @SuppressLint("IntentReset")
    private void selectImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, 2);
    }

    private boolean ImageSelectJudge = false;
    private Uri Image_uri;
    public void onFragmentResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            Image_uri = data.getData();
            binding.userFace.setImageURI(Image_uri);
            ImageSelectJudge = true;
        }
    }

    //---------------请求事件------------------

    @Override
    public void onSuccess(String callbackClass, R_dataType rData) {
        if(callbackClass.equals(CheckAccountRequest)){
            Object data = R_Util.get_DoubleR_data(rData);
            Integer data1 = (Integer) data;
            if(data1 > 0){
                Toast.makeText(THIS,"该账号已经注册过了",Toast.LENGTH_SHORT).show();
                return;
            }
            SignUp_NewAccount();
        }
        if (callbackClass.equals(SignUpNewAccountRequest)){
            Toast.makeText(THIS,"注册成功",Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    }

    @Override
    public void onFailure(String callbackClass) {
        if(callbackClass.equals(CheckAccountRequest)){
            Toast.makeText(THIS,"注册请求连接失败",Toast.LENGTH_SHORT).show();
        }
        if (callbackClass.equals(SignUpNewAccountRequest)){
            Toast.makeText(THIS,"注册请求连接失败",Toast.LENGTH_SHORT).show();
        }
    }
}