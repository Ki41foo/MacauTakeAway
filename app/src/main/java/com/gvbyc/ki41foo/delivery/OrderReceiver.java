package com.gvbyc.ki41foo.delivery;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Bundle;

import com.google.gson.Gson;
import com.gvbyc.ki41foo.delivery.model.PushExtraInfo;
import com.gvbyc.ki41foo.delivery.protocal.OrderProtocol;
import com.gvbyc.ki41foo.delivery.utils.LogUtils;

import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;
import de.greenrobot.event.EventBus;

/**
 * Created by goodview on 18/02/16.
 */
public class OrderReceiver extends BroadcastReceiver{

    private NotificationManager nm;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null == nm) {
            nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        Bundle bundle = intent.getExtras();
        LogUtils.d( "onReceive - " + intent.getAction() + ", extras: " );

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            receivingNotification(context,bundle);

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            openNotification(context,bundle);
        } else {
            LogUtils.d("Unhandled intent - " + intent.getAction());
        }
    }

    private void receivingNotification(Context context, Bundle bundle){
        Ringtone r = RingtoneManager.getRingtone(context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        r.play();

        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        String myValue = "";
        try {
            JSONObject extrasJson = new JSONObject(extras);
            myValue = extrasJson.optString("json");
            LogUtils.i("myValue ===== " + myValue);
            PushExtraInfo extraInfo = new Gson().fromJson(myValue, PushExtraInfo.class);
            if(extraInfo.action.equals(PushExtraInfo.ORDER_PUSH)) {
                OrderProtocol.requestMyOrders();
            }
        } catch (Exception e) {
            LogUtils.w( "Unexpected: extras is not a valid json", e);
            return;
        }
    }

    private void openNotification(Context context, Bundle bundle){
        //open app
        Intent i = new Intent();
        i.setClassName("com.gvbyc.ki41foo.delivery", "com.gvbyc.ki41foo.delivery.UI.activity.Home");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);

        //post push info
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        String myValue = "";
        try {
            JSONObject extrasJson = new JSONObject(extras);
            myValue = extrasJson.optString("json");
            LogUtils.i("myValue ===== " + myValue);
            PushExtraInfo extraInfo = new Gson().fromJson(myValue, PushExtraInfo.class);
            EventBus.getDefault().postSticky(extraInfo);

        } catch (Exception e) {
            LogUtils.w( "Unexpected: extras is not a valid json", e);
            return;
        }
    }
}
