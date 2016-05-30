package com.gvbyc.ki41foo.delivery.utils;

import android.app.Activity;
import android.content.Intent;

import com.gvbyc.ki41foo.delivery.R;

import org.apache.http.message.BasicNameValuePair;


public class IntentUtils {
    public static void startActivity(Activity activity, Class<?> cls, BasicNameValuePair... name) {
        Intent intent = new Intent();
        intent.setClass(activity, cls);
        for (int i = 0; i < name.length; i++) {
            intent.putExtra(name[i].getName(), name[i].getValue());
        }
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.forward_in,R.anim.forward_out);
    }

    public static void startActivity(Activity activity, Class<?> cls, String name, int value) {
        Intent intent = new Intent();
        intent.setClass(activity, cls);
        intent.putExtra(name, value);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.forward_in,R.anim.forward_out);
    }

    public static void startActivity(Activity activity, Class<?> cls, String name, String value) {
        Intent intent = new Intent();
        intent.setClass(activity, cls);
        intent.putExtra(name, value);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.forward_in,R.anim.forward_out);
    }

    public static void startActivityAndFinish(Activity activity, Class<?> cls, BasicNameValuePair... name) {
        Intent intent = new Intent();
        intent.setClass(activity, cls);
        for (int i = 0; i < name.length; i++) {
            intent.putExtra(name[i].getName(), name[i].getValue());
        }
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.forward_in,R.anim.forward_out);
        //activity.finish();
    }


}
