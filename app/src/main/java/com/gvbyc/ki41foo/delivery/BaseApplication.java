package com.gvbyc.ki41foo.delivery;

import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

import com.baidu.mapapi.SDKInitializer;
import com.gvbyc.ki41foo.delivery.utils.CommonUtils;
import com.gvbyc.ki41foo.delivery.utils.LogUtils;
import com.gvbyc.ki41foo.delivery.utils.PackageUtils;
import com.gvbyc.ki41foo.delivery.utils.SystemUtils;
import com.gvbyc.ki41foo.delivery.utils.Utility;
import com.umeng.analytics.MobclickAgent;

import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

public class BaseApplication extends Application {
    //获取到主线程的上下文
    private static BaseApplication mContext;
    //获取到主线程的handler
    private static Handler mMainThreadHandler;
    //获取到主线程轮询器
    private static Looper mMainThreadLooper;
    //获取到主线程的Thread
    private static Thread mMainThread;
    //获取到主线程的id
    private static int mMainThreadId;

    public static SharedPreferences sp;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }



    @Override
    public void onCreate() {
        super.onCreate();

        BaseApplication.mContext = this;
        this.mMainThreadHandler = new Handler();
        this.mMainThreadLooper = getMainLooper();
        this.mMainThread = Thread.currentThread();
        this.mMainThreadId = android.os.Process.myTid();

        this.sp = getSharedPreferences("ki", MODE_PRIVATE);

        //push service SDK init
        JPushInterface.init(this);
        JPushInterface.setDebugMode(BuildConfig.DEBUG);
        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(this);
        builder.statusBarDrawable = R.mipmap.ic_launcher;
        builder.notificationFlags = Notification.FLAG_AUTO_CANCEL;
        builder.notificationDefaults = Notification.DEFAULT_VIBRATE|Notification.DEFAULT_LIGHTS;  // 设置为铃声与震动都要
        JPushInterface.setPushNotificationBuilder(1, builder);

        //app analysis SDK init
        MobclickAgent.setDebugMode(BuildConfig.DEBUG);
        //map SDK init
        SDKInitializer.initialize(this);
    }


    //对外暴露一个上下文
    public static BaseApplication getApplication() {
        return mContext;
    }

    //对外暴露一个主线程的handler
    public static Handler getMainThreadHandler() {
        return mMainThreadHandler;
    }

    //对外暴露一个主线程的Looper
    public static Looper getMainThreadLooper() {
        return mMainThreadLooper;
    }

    //对外暴露一个主线程
    public static Thread getMainThread() {
        return mMainThread;
    }

    //对外暴露一个主线程ID
    public static int getMainThreadId() {
        return mMainThreadId;
    }

    public static void putSp(String key, String val) {
        sp.edit().putString(key, val).apply();
    }

    public static String getSpString(String key) {
        return sp.getString(key, null);
    }

    public static void putSp(String key, boolean val) {
        sp.edit().putBoolean(key, val).apply();
    }

    public static boolean getSpBoolean(String key) {
        return sp.getBoolean(key, false);
    }

}
