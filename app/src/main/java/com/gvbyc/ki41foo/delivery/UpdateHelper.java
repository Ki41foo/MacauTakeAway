package com.gvbyc.ki41foo.delivery;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.gvbyc.ki41foo.delivery.model.MyAction;
import com.gvbyc.ki41foo.delivery.protocal.BaseProtocol;
import com.gvbyc.ki41foo.delivery.protocal.Http;
import com.gvbyc.ki41foo.delivery.utils.LogUtils;
import com.gvbyc.ki41foo.delivery.utils.PackageUtils;

import de.greenrobot.event.EventBus;


/**
 * Created by goodview on 11/04/16.
 */
public class UpdateHelper {
    public static final String UPDATE_URL = Http.SERVER + "appver/delivery/user";
    private Activity activity;

    public UpdateHelper(final Activity activity) {

        this.activity = activity;
    }

    public void check() {
        Http.post(UPDATE_URL, null, new Http.Listener() {
            @Override
            public void onResponse(String result) {
                if (BaseProtocol.getMsg(result).equals("msg17")) {
                    JsonElement returnObject = BaseProtocol.getReturnObject(result).get("object");
                    UpdateInfo updateInfo = new Gson().fromJson(returnObject, UpdateInfo.class);

                    String ver = PackageUtils.getVersionName();
                    //no update available
                    if (updateInfo.lastandroidver.equals(ver)) {
                        EventBus.getDefault().post(new MyAction(MyAction.NO_UPDATE_AVAILABLE));
                        return;
                    }

                    int verInt = Integer.parseInt(ver.replace(".", ""));
                    int supInt = Integer.parseInt(updateInfo.androidsupver.replace(".", ""));
                    showDialog(supInt > verInt);
                }
            }
        });
    }

    private void showDialog(boolean forced) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle)
                .setTitle("發現新版本")
                .setPositiveButton("去更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String appPackageName = activity.getPackageName();
                        try {
                            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                            activity.finish();
                        } catch (android.content.ActivityNotFoundException anfe) {
                            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                            activity.finish();
                        }
                    }
                }).setCancelable(!forced);

        if (!forced) {
            builder.setNegativeButton("暫不更新",null);
        }
        builder.show();

    }

    class UpdateInfo {
        String lastandroidver;
        String androidsupver;
        String andriodlink;
    }


}
