package com.gvbyc.ki41foo.delivery.utils;

import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import com.gvbyc.ki41foo.delivery.AppManager;
import com.gvbyc.ki41foo.delivery.BaseApplication;


/**
 * Created by goodview on 15/01/15.
 */
public class T {
    /**
     * show Toast for short
     *
     * @param msg
     */
    public static void showShort(String msg) {
        Toast.makeText(UIUtils.getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * show Toast for long
     *
     * @param msg
     */
    public static void showLong(String msg) {
        Toast.makeText(BaseApplication.getApplication(), msg, Toast.LENGTH_LONG).show();
    }



    public static void snack(View view, String msg) {
        if(view == null) {
            view = AppManager.getAppManager().currentActivity().getWindow().getDecorView();
        }
        final Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_SHORT);
        //snackbar.getView().setBackgroundColor(UIUtils.getColor(R.color.accent1));
        snackbar.show();
    }

    public static void snackLong(View view, String msg) {
        if(view == null) {
            view = AppManager.getAppManager().currentActivity().getWindow().getDecorView();
        }
        final Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG);
        //snackbar.getView().setBackgroundColor(UIUtils.getColor(R.color.accent1));
        snackbar.show();
    }
}
