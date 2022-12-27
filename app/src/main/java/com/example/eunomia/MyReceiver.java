package com.example.eunomia;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.text.SimpleDateFormat;

import android.os.Build;
import android.support.v4.app.NotificationCompat;

//import androidx.core.app.NotificationCompat;

import java.util.Date;

import static android.content.Context.NOTIFICATION_SERVICE;

public class MyReceiver extends BroadcastReceiver {
    private Context context;
    private FileHelper fileHelper;  //自己写的文件工具类，封装了io操作的代码

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        showNotification(intent);  //弹出通知
        saveLog(intent);            //记录日志
    }

    //日志记录的函数
    private void saveLog(Intent intent) {
        String name = intent.getStringExtra("name");
        String permission = intent.getStringExtra("permission");
        String time;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        time = formatter.format(curDate);
        fileHelper = new FileHelper(context);
        fileHelper.save(name, time, permission);
    }

    //弹出通知的函数
//    private void showNotification(Intent intent)
//    {
//        String name= intent.getStringExtra("name");
//        String permission=intent.getStringExtra("permission");
//        NotificationManager manager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
//        Intent intent2=new Intent(context, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent2,0);
//        if(Build.VERSION.SDK_INT >= 26)
//        {
//            //当sdk版本大于26
//            String id = "channel_1";
//            String description = "222";
//            int importance = NotificationManager.IMPORTANCE_HIGH;
//            NotificationChannel channel = new NotificationChannel(id, description, importance);
////                     channel.enableLights(true);
//            channel.enableVibration(true);//
//            channel.setSound(null, Notification.AUDIO_ATTRIBUTES_DEFAULT);
//
//            manager.createNotificationChannel(channel);
//            Notification notification = new NotificationCompat.Builder(context, id)
////                        .setCategory(Notification.CATEGORY_MESSAGE)
//                    .setSmallIcon(R.mipmap.ic_launcher)
//                    .setContentTitle("监控助手")
//                    .setContentText(name+"使用了"+permission)
//                    .setContentIntent(pendingIntent)
//                    .setFullScreenIntent(pendingIntent,true)
//                    .setAutoCancel(true)
//                    .setPriority(NotificationManager.IMPORTANCE_HIGH)
//                    .build();
//            manager.notify(1, notification);
//        }
//        else
//        {
//        //当sdk版本小于26
//        Notification notification = new NotificationCompat.Builder(context)
//                .setContentTitle("监控助手")
//                .setContentText(name+"使用了"+permission)
//                .setContentIntent(pendingIntent)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setPriority(NotificationManager.IMPORTANCE_HIGH)
//                .setFullScreenIntent(pendingIntent, true)
//                .build();
//        manager.notify(1,notification);
//    }
//    }
    private void showNotification(Intent intent) {
        String name = intent.getStringExtra("name");
        String permission = intent.getStringExtra("permission");
        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        //Intent用于指定点击通知后打开的界面
        Intent intent2 = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent2, 0);
        //Intent用于指定点击选项后的操作
        Intent blockIntent = new Intent(context, BlockReceiver.class);
        blockIntent.putExtra("name", name);
        blockIntent.putExtra("permission", permission);
        PendingIntent blockPendingIntent = PendingIntent.getBroadcast(context, 0, blockIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action blockAction = new NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher, "阻止", blockPendingIntent).build();

        Intent agreeIntent = new Intent(context, AgreeReceiver.class);
        agreeIntent.putExtra("name", name);
        agreeIntent.putExtra("permission", permission);
        PendingIntent agreePendingIntent = PendingIntent.getBroadcast(context, 0, agreeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action agreeAction = new NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher, "同意", agreePendingIntent).build();

        Intent interceptIntent = new Intent(context, MainActivity.class);
        interceptIntent.putExtra("name", name);
        interceptIntent.putExtra("permission", permission);
        PendingIntent interceptPendingIntent = PendingIntent.getBroadcast(context, 0, interceptIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action interceptAction = new NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher, "拦截", interceptPendingIntent).build();

        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle("Eunomia")
                .setContentText(name + "使用了" + permission)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setFullScreenIntent(pendingIntent, true)
                .addAction(blockAction)
                .addAction(agreeAction)
                .addAction(interceptAction)
                .build();
        manager.notify(1, notification);
    }
//    private void showNotification(Intent intent)
//    {
//        String name= intent.getStringExtra("name");
//        String permission=intent.getStringExtra("permission");
//        NotificationManager manager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
//        //创建意图，用于点击通知后打开 BlockReceiver
//        Intent intentBlock = new Intent(context, BlockReceiver.class);
//        PendingIntent pendingIntentBlock = PendingIntent.getBroadcast(context, 0, intentBlock, 0);
//        //创建意图，用于点击通知后打开 AgreeReceiver
//        Intent intentAgree = new Intent(context, AgreeReceiver.class);
//        PendingIntent pendingIntentAgree = PendingIntent.getBroadcast(context, 0, intentAgree, 0);
//        //创建意图，用于点击通知后打开 InterceptReceiver
//        Intent intentIntercept = new Intent(context, InterceptReceiver.class);
//        PendingIntent pendingIntentIntercept = PendingIntent.getBroadcast(context, 0, intentIntercept, 0);
//        //当sdk版本小于26
//        Notification notification = new NotificationCompat.Builder(context)
//                .setContentTitle("监控助手")
//                .setContentText(name+"使用了"+permission)
//                .addAction(R.mipmap.ic_launcher, "阻止", pendingIntentBlock)  //添加阻止按钮
//                .addAction(R.mipmap.ic_launcher, "同意", pendingIntentAgree)  //添加同意按钮
//                .addAction(R.mipmap.ic_launcher, "拦截", pendingIntentIntercept)  //添加拦截按钮
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setPriority(NotificationManager.IMPORTANCE_HIGH)
//                .build();
//        manager.notify(1,notification);
//    }


}

