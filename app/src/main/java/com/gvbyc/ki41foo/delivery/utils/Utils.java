package com.gvbyc.ki41foo.delivery.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.text.format.DateUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.gvbyc.ki41foo.delivery.AppManager;

import java.text.SimpleDateFormat;



/**
 * Created by goodview on 24/03/15.
 */
public class Utils {
    public static void makePhoneCall(String pre,String tel) {

        if (ActivityCompat.checkSelfPermission(AppManager.getAppManager().currentActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            T.showLong("請在權限管理中開啟不夜城外賣撥號權限！");
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if(pre.equals("1")) {
            tel = "+853" + tel;
        }
        if(pre.equals("2")) {
            tel = "+86" + tel;
        }

        if(pre.equals("3")) {
            tel = "+852" + tel;
        }
        Intent phoneCallIntent = new Intent(Intent.ACTION_CALL);
        phoneCallIntent.setData(Uri.parse("tel:" + tel));
        AppManager.getAppManager().currentActivity().startActivity(phoneCallIntent);
    }

    public static String formatTimeShort(String time) {
        SimpleDateFormat df = new SimpleDateFormat();

        if (DateUtils.isToday(Long.valueOf(time))) {
            df.applyPattern("HH:mm");
            return df.format(Long.valueOf(time));
        } else if (DateUtils.isToday(Long.valueOf(time) + 24l * 60 * 60 * 1000)) {
            return "昨日";
        } else if (DateUtils.isToday(Long.valueOf(time) + 24l * 60 * 60 * 1000 * 2)) {
            return "前日";
        } else {
            df.applyPattern("MM月dd日");
            return df.format(Long.valueOf(time));
        }

    }


    public static void hideSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) UIUtils.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showSoftInput(final View view) {
        view.postDelayed(new Runnable() {
            public void run() {
                view.requestFocusFromTouch();
                InputMethodManager lManager = (InputMethodManager) UIUtils.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                lManager.restartInput(view);
                lManager.showSoftInput(view, 0);
            }
        },200);
    }

    public static String encryptPwd(String o) {
        String md5 = MD5.encode(o);
        String sha1 = SHA1.encode(o);
        return MD5.encode(md5.substring(0, 5) + sha1.substring(12, 22));
    }

    public static int calculateDiscount(int discount, String payprice) {
        return (int) Math.ceil(Double.parseDouble(payprice) * discount / 100);
    }

}
