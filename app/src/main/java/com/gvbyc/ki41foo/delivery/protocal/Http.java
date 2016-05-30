package com.gvbyc.ki41foo.delivery.protocal;

import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.gvbyc.ki41foo.delivery.Consts;
import com.gvbyc.ki41foo.delivery.MyHurlStack;
import com.gvbyc.ki41foo.delivery.UserAccountManager;
import com.gvbyc.ki41foo.delivery.model.MyAction;
import com.gvbyc.ki41foo.delivery.utils.Installation;
import com.gvbyc.ki41foo.delivery.utils.LogUtils;
import com.gvbyc.ki41foo.delivery.utils.MD5;
import com.gvbyc.ki41foo.delivery.utils.T;
import com.gvbyc.ki41foo.delivery.utils.UIUtils;

import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;


/**
 * Created by goodview on 30/01/15.
 */
public class Http {

    //private static final String HOST = "192.168.1.131:9999"; //local
    //private static final String HOST = "112.74.104.67"; //寫路
    //private static final String HOST = "120.25.150.132";//ali
    private static final String HOST = "www.gvbyc.com";
    public static final String SERVER = "http://" + HOST + "/gvdelivery/";
    public static final String IMG_SERVER = "http://" + HOST + "/happymacau/image/";
    public static final String FILE_SERVER = "http://" + HOST + "/happymacau/file/";


    static RequestQueue queue = null;


    public static RequestQueue getQueue() {
        if(queue == null) queue = Volley.newRequestQueue(UIUtils.getContext(),new MyHurlStack());
        return queue;
    }

    public static void post(String url, final Object jsonObject, final Listener listener) {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url.startsWith("http") ? url : SERVER + url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                LogUtils.i("response ====== " + response);

                if(TextUtils.isEmpty(response)) {
                    EventBus.getDefault().post(new MyAction(MyAction.NETWORK_ERROR));
                    return;
                }
                if(response.startsWith("<")) {
                    EventBus.getDefault().post(new MyAction(MyAction.NETWORK_ERROR));
                    T.showShort("天氣不好，阿裏雲罷工了");
                    return;
                }


                if (new JsonParser().parse(response).getAsJsonObject().get("msg").getAsString().endsWith("msg21")) {
                    T.showShort("程序出錯啦, msg21");
                } else {
                    listener.onResponse(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                EventBus.getDefault().post(new MyAction(MyAction.NETWORK_ERROR));
                if (null == error.networkResponse) {
                    T.showShort("失去連接，請檢查網絡設置");
                    LogUtils.i("response error =======> 404 ===");


                } else {
                    T.showShort("未知網絡錯誤，請聯繫開發人員");
                    LogUtils.i("error status code ====> " + error.networkResponse.statusCode + "");
                }
                error.printStackTrace();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Object o = jsonObject;
                String json = o == null ? "{}" : new Gson().toJson(o);
                String signature = Consts.SIGNATURE;
                String timestamp = String.valueOf(System.currentTimeMillis());
                String terminalId = Installation.uuid(UIUtils.getContext());
                String ucode = UserAccountManager.getInstance().isLogin() ? UserAccountManager.getInstance().getUserInfo().code : "0";

                Map map = new LinkedHashMap<>();
                map.put("json", json);
                map.put("signature", signature);
                map.put("timestamp", timestamp);
                map.put("terminalId", terminalId);
                map.put("uCode", ucode);

                return map;
            }
        };
        try {
            byte[] body = stringRequest.getBody();

            LogUtils.i("Request<" + SERVER + url + ">==========" + URLDecoder.decode(new String(body)).toString());
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getQueue().add(stringRequest);
    }



    public interface Listener {
        void onResponse(String result);
    }


}
