package com.example.chattest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;

import com.example.chattest.Chat.main.Chat;
import com.example.chattest.Health.HealthFragment;
import com.example.chattest.Health.ReminderService;
import com.example.chattest.Home.HomeFragment;
import com.example.chattest.Search.SearchActivity;
import com.example.chattest.Test.MyDebug;
import com.example.chattest.User.Service.HeartBeatService;
import com.example.chattest.User.UserFragment;
import com.example.chattest.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity implements MainActivityInterface{

    public ActivityMainBinding binding;

    @SuppressLint("StaticFieldLeak")
    public static Context MainContext;
    public static int Window_Width;
    public static int Window_Height;

    //------------------------------------onCreate-------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainContext = this;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Window_Width = displayMetrics.widthPixels;
        Window_Height = displayMetrics.heightPixels;
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Service
        start_Service();

        //监听器
        setListeners();

        //Fragment
        Fragment_FragmentContainer();

        //test
        test();
    }

    //------------------------------------onCreate-------------------------------------------------


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService();
    }
    //-------------------------Service-------------------------

    private void start_Service(){
        Intent intent = new Intent(MainActivity.this, HeartBeatService.class);
        startService(intent);
        Intent intent1 = new Intent(MainActivity.this, ReminderService.class);
        startService(intent1);
    }

    private void stopService() {
        Intent serviceIntent = new Intent(MainActivity.this, ReminderService.class);
        stopService(serviceIntent);
        Intent intent = new Intent(MainActivity.this, HeartBeatService.class);
        stopService(intent);
    }


    //-------------------------------------SetListener-------------------------------------------------

    private void setListeners(){
        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Chat.class));
            }
        });
    }

    //-------------------------------------test------------------------------------------------

    private void test(){

    }

    //------------------------------------Fragment-------------------------------------------------

    private HomeFragment homeFragment;
    //private HospitalFragment hospitalFragment;
    private HealthFragment healthFragment;
    private UserFragment userFragment;
    private Fragment initialFragment;


    private void Fragment_FragmentContainer(){
        Init_FragmentData();
        SetFragmentSwitchListener();
    }


    private void Init_FragmentData(){
        //初始化内存
        //hospitalFragment = new HospitalFragment();
        homeFragment = new HomeFragment();
        healthFragment = new HealthFragment();
        userFragment = new UserFragment();

        // 设置初始显示的 Fragment
        initialFragment = homeFragment;

        // 设置底部应用栏
        //getSupportFragmentManager().beginTransaction().add(binding.fragmentContainer.getId(), hospitalFragment).hide(hospitalFragment).commit();
        getSupportFragmentManager().beginTransaction().add(binding.fragmentContainer.getId(), healthFragment).hide(healthFragment).commit();
        getSupportFragmentManager().beginTransaction().add(binding.fragmentContainer.getId(), userFragment).hide(userFragment).commit();
        getSupportFragmentManager().beginTransaction().add(binding.fragmentContainer.getId(), homeFragment).commit();

    }


    private void FragmentSwitch(Fragment targetFragment){
        // 切换 Fragment 的方法
        getSupportFragmentManager().beginTransaction().hide(initialFragment).show(targetFragment).commit();
        initialFragment = targetFragment;
    }


    private void SetFragmentSwitchListener(){
        binding.bottomAppBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.menuHomeIcon){
                    FragmentSwitch(homeFragment);
                    return true;
                }
                else if (item.getItemId() == R.id.menuHealthIcon) {
                    FragmentSwitch(healthFragment);
                    return true;
                }
//                else if (item.getItemId() == R.id.menuHospitalIcon) {
//                    FragmentSwitch(hospitalFragment);
//                    return true;
//                }
                else if (item.getItemId() == R.id.menuUserIcon) {
                    FragmentSwitch(userFragment);
                    return true;
                }
                return false;
            }
        });
    }


    //--------------------------------------Interface-----------------------------------------------

    @Override
    public void onBottomNavigationItemClick() {
        //点击了按钮之后，自动切换到User
        binding.bottomAppBar.setSelectedItemId(R.id.menuUserIcon);

    }


    @Override
    public void searchBarClick() {
        //点击了搜索之后，跳转到搜索Activity
        startActivity(new Intent(getApplicationContext(), SearchActivity.class));
    }

}