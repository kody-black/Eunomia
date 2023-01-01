package com.example.eunomia;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.ContextThemeWrapper;

import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyHook implements IXposedHookLoadPackage {
    private Context context = null;

    public static synchronized String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        //第一个钩子，负责获取当前应用的Context，从而利用当前应用的Context向我们的应用发送广播
        XposedHelpers.findAndHookMethod(ContextThemeWrapper.class, "attachBaseContext", Context.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                context = (Context) param.args[0];
            }
        });

        //对权限申请函数的hook
        XposedHelpers.findAndHookMethod(
                "android.content.ContextWrapper",//要hook的类
                ClassLoader.getSystemClassLoader(),//获取classLoader
                "checkSelfPermission",//要hook的方法（函数）checkPermission(String var1, int var2, int var3);
                //    Context.class,
                String.class,
                new XC_MethodHook() {
                    //这里是hook回调函数
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("检查权限:" + param.args[0]);
                        //当权限涉及位置信息的时候，向我们的监控app发送广播
                        if (param.args[0].toString().contains("LOCATION")) {
                            if (context != null) {
                                //获取用户的选择
                                SharedPreferences sp = context.getSharedPreferences("UserChoice", Context.MODE_PRIVATE);
                                XposedBridge.log(sp.getAll().toString());
                                String choice = sp.getString("choice","null");
                                XposedBridge.log(choice);
                                if (choice.equals("agree")) {
                                //允许获取位置权限
                                    param.setResult(PackageManager.PERMISSION_GRANTED);
                                    XposedBridge.log("同意权限:" + param.args[0]);
                                } else if (choice.equals("block")) {
                                //阻止获取位置权限
                                    param.setResult(PackageManager.PERMISSION_DENIED);
                                    XposedBridge.log("阻止权限:" + param.args[0]);
                                } else {
                                //发送广播，让用户选
                                    param.setResult(PackageManager.PERMISSION_DENIED);
                                    Intent intent = new Intent();
                                    intent.setAction("com.example.sec.BroadcastReceiverTest");
                                    intent.setComponent(new ComponentName("com.example.eunomia",
                                            "com.example.eunomia.MyReceiver"));
                                    intent.putExtra("name", getAppName(context));
                                    intent.putExtra("permission", "位置");
                                    context.sendBroadcast(intent);
                                }

//                                param.setResult(PackageManager.PERMISSION_DENIED); //拒绝使用权限
//                                Intent intent = new Intent();
//                                intent.setAction("com.example.sec.BroadcastReceiverTest");
//                                intent.setComponent(new ComponentName("com.example.eunomia",
//                                        "com.example.eunomia.MyReceiver"));
//                                intent.putExtra("name", getAppName(context));
//                                intent.putExtra("permission", "位置，并已经被拒绝");
//                                context.sendBroadcast(intent);

                            }
                        }
                        if (param.args[0].toString().contains("WIFI")) {
                            if (context != null) {

                                Intent intent = new Intent();
                                intent.setAction("com.example.sec.BroadcastReceiverTest");
                                intent.setComponent(new ComponentName("com.example.eunomia",
                                        "com.example.eunomia.MyReceiver"));
                                intent.putExtra("name", getAppName(context));
                                intent.putExtra("permission", "WIFI");
                                context.sendBroadcast(intent);
                            }
                        }
                        if (param.args[0].toString().contains("UPDATE_DEVICE_STATS")) {
                            if (context != null) {

                                Intent intent = new Intent();
                                intent.setAction("com.example.sec.BroadcastReceiverTest");
                                intent.setComponent(new ComponentName("com.example.eunomia",
                                        "com.example.eunomia.MyReceiver"));
                                intent.putExtra("name", getAppName(context));
                                intent.putExtra("permission", "更新设备信息");
                                context.sendBroadcast(intent);
                            }
                        }
                        if (param.args[0].toString().contains("WRITE_EXTERNAL_STORAGE")) {
                            if (context != null) {

                                Intent intent = new Intent();
                                intent.setAction("com.example.sec.BroadcastReceiverTest");
                                intent.setComponent(new ComponentName("com.example.eunomia",
                                        "com.example.eunomia.MyReceiver"));
                                intent.putExtra("name", getAppName(context));
                                intent.putExtra("permission", "写文件");
                                context.sendBroadcast(intent);
                            }
                        }
                        if (param.args[0].toString().contains("RECORD_AUDIO")) {
                            if (context != null) {

                                Intent intent = new Intent();
                                intent.setAction("com.example.sec.BroadcastReceiverTest");
                                intent.setComponent(new ComponentName("com.example.eunomia",
                                        "com.example.eunomia.MyReceiver"));
                                intent.putExtra("name", getAppName(context));
                                intent.putExtra("permission", "录音");
                                context.sendBroadcast(intent);
                            }
                        }

                    }

                }
        );
    }
}


//        android.permission.RECORD_AUDIO
//        XposedHelpers.findAndHookMethod(
//                "android.content.ContextWrapper",//要hook的类
//                ClassLoader.getSystemClassLoader(),//获取classLoader
//                "checkSelfPermission",//要hook的方法（函数）checkPermission(String var1, int var2, int var3);
//                new XC_MethodHook() {
//                    //这里是hook回调函数
//                    @Override
//                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                        XposedBridge.log("正在使用录音权限");
//                        if (context != null) {
//
//                            Intent intent = new Intent();
//                            intent.setAction("com.example.sec.BroadcastReceiverTest");
//                            intent.setComponent(new ComponentName("com.example.eunomia",
//                                    "com.example.eunomia.MyReceiver"));
//                            intent.putExtra("name", getAppName(context));
//                            intent.putExtra("permission", "录音");
//                            context.sendBroadcast(intent);
//                        }
//                    }
//                }
//        );
//
//
//
//        //监控应用对摄像机硬件的使用
//        XposedHelpers.findAndHookMethod(
//                "android.hardware.Camera",//要hook的类
//                ClassLoader.getSystemClassLoader(),//获取classLoader
//                "open",
//                String.class,
//                new XC_MethodHook() {
//                    //这里是hook回调函数
//                    @Override
//                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                        XposedBridge.log("检查权限3:" + param.args[0]);
//                        if (param.args[0].toString().contains("CAMERA")) {
//                            if (context != null) {
//                                Intent intent = new Intent();
//                                intent.setAction("com.example.sec.BroadcastReceiverTest");
//                                intent.setComponent(new ComponentName("com.example.eunomia",
//                                        "com.example.eunomia.MyReceiver"));
//                                intent.putExtra("name", getAppName(context));
//                                intent.putExtra("permission", "相机");
//                                context.sendBroadcast(intent);
//                            }
//                        }
//                    }
//                }
//        );



