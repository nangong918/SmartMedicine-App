package com.example.chattest.Health;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.example.chattest.Test.MyDebug;

public class ReminderService extends Service {

    public static class RemindData{
        int day;
        int hour;
        int min;
        int second;
        String name;
        String reminder;
        long setTime;

        public RemindData(int day, int hour, int min, int second, String name, String reminder, long setTime) {
            this.day = day;
            this.hour = hour;
            this.min = min;
            this.second = second;
            this.name = name;
            this.reminder = reminder;
            this.setTime = setTime;
        }

        private long timeCalculate(){
            int t = (int)(((Math.abs(day) * 24 + Math.abs(hour)*60) + Math.abs(min) * 60 + Math.abs(second)) * 1000);
            return (long)t;
        }

        private boolean checkOnTime(){
            long currentTime = System.currentTimeMillis();
            return currentTime - timeCalculate() - setTime >= 0;
        }
    }
    public static RemindData remindData = null;
    private static final long CHECK_TIME = 1000; // 心跳间隔时间，单位为毫秒

    public ReminderService() {
    }

    private Handler handler;
    private Runnable runnable;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                BroadCast();
                handler.postDelayed(this, CHECK_TIME);
            }
        };
    }

    private void Broadcast_static(){
        Intent intent = new Intent("com.example.chattest");
        try {
            intent.setClass(this,Class.forName("com.example.chattest.Health.ReminderReceiver"));
        }catch (ClassNotFoundException e){
            Log.e("Runtime","Error:"+e);
        }
        sendBroadcast(intent);
    }

    private void BroadCast(){
        if(remindData != null){
            MyDebug.Print("111111111成功");
            boolean judge = remindData.checkOnTime();
            if(judge){
                Broadcast_static();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.post(runnable);
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}