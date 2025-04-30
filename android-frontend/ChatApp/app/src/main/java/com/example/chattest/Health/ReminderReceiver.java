package com.example.chattest.Health;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;

import com.example.chattest.R;

public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        broadcast(context);
    }

    private void broadcast(Context context) {
        String remindData = "吃药";
        String name = "药品";
        if(ReminderService.remindData != null && !ReminderService.remindData.reminder.isEmpty()){
            remindData = ReminderService.remindData.reminder;
        }
        if(ReminderService.remindData != null && !ReminderService.remindData.name.isEmpty()){
            name = ReminderService.remindData.name;
        }
        // 创建通知
        String channelId = "用药提醒！！！";
        Notification notification = new Notification.Builder(context,channelId)
                .setContentTitle("该吃药啦！！！" + name)
                .setContentText(remindData)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.basic_user)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.basic_user))   //设置大图标
                .build();

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel(channelId,"用药提醒！！！", NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);

        notificationManager.notify(1, notification);
    }
}