package com.example.chattest.Sign;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.example.chattest.MainActivity;
import com.example.chattest.SQLite.SQLiteService;
import com.example.chattest.URL.UrlUtil;
import com.example.chattest.User.HeartbeatTask;
import com.example.chattest.User.User;
import com.example.chattest.UserType.UserBackType;
import com.example.chattest.UserType.UserRequestType;
import com.example.chattest.Utils.CallBackInterface;
import com.example.chattest.Utils.RequestUtils;
import com.example.chattest.Utils.Type.R_Util;
import com.example.chattest.Utils.Type.R_dataType;
import com.example.chattest.databinding.ActivitySignInBinding;

public class SignIn extends AppCompatActivity implements CallBackInterface {

    //视图绑定
    private ActivitySignInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setListeners();

        //启动数据库Service
        startService(new Intent(this, SQLiteService.class));
    }


    //------------------------------------ClickListener------------------------------------

    private void setListeners(){
        binding.textCreateNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SignUp.class));
            }
        });

        binding.buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckSignIn();
            }
        });

        binding.textSkipAndEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }

    //------------------------------------SignIn------------------------------------
    private final String requestClass = "SignIn";
    private UserRequestType userData;
    private HeartbeatTask heartbeatTask;

    private void CheckSignIn(){
        String account = this.binding.SignInInputEmail.getText().toString();
        String password = this.binding.SignInInputPassword.getText().toString();
        if(account.equals("") || password.equals("")){
            return;
        }
        UserRequestType userRequestType = new UserRequestType(account,password);
        userData = userRequestType;
        R_dataType rDataType = new R_dataType(userRequestType);
        String jsonData = R_Util.R_JsonUtils.toJson(rDataType);
        RequestUtils post_requestUtils = new RequestUtils(5000, requestClass, "POST", UrlUtil.GetSignInUrl(), jsonData);
        post_requestUtils.callback = this;
        post_requestUtils.StartThread();
    }
    private void SignInAccount(int user_id){
        if(user_id > 0){
            User.user_id = user_id;
            Toast.makeText(this,"用户：" + User.user_id + "登录成功",Toast.LENGTH_SHORT).show();
            User.account = userData.account;
            User.password = userData.password;
            User.Logged = true;
            //心跳请求启动
//            heartbeatTask = new HeartbeatTask(user_id);
//            heartbeatTask.start();
            //页面跳转
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
        else {
            Toast.makeText(this,"账号密码错误",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onSuccess(String callbackClass, R_dataType rData) {
        if(callbackClass.equals(requestClass)){
            UserBackType userBackType = new UserBackType();
            userBackType.getByRData(rData);
            if(userBackType.imageBytes != null){
                User.User_image = userBackType.imageBytes;
            }
            User.Name = userBackType.name;
            User.user_id = userBackType.id;
            SignInAccount(userBackType.id);
        }
    }
    @Override
    public void onFailure(String callbackClass) {
        if(callbackClass.equals(requestClass)){
            //提示连接失败
            Toast.makeText(this,"登录连接失败",Toast.LENGTH_SHORT).show();
        }
    }
}