package com.gvbyc.ki41foo.delivery.protocal;

import com.google.gson.Gson;
import com.gvbyc.ki41foo.delivery.model.NewsInfo;

import java.util.HashMap;

import de.greenrobot.event.EventBus;

/**
 * Created by goodview on 22/02/16.
 */
public class NewsProtocol extends BaseProtocol{

    public static final String PATH = "messagecenter/enquire/user";


    public static void requestNews() {
        HashMap<String, String> map = new HashMap<>();
        Http.post(PATH, map, new Http.Listener() {
            @Override
            public void onResponse(String result) {
                NewsInfo.NewsList data = new Gson().fromJson(getReturnObject(result), (NewsInfo.NewsList.class));
                EventBus.getDefault().post(data);
            }
        });
    }
}
