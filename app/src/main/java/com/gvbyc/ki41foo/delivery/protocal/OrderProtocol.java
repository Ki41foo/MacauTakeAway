package com.gvbyc.ki41foo.delivery.protocal;

import com.google.gson.Gson;
import com.gvbyc.ki41foo.delivery.model.DeliveryOrderInfo;
import com.gvbyc.ki41foo.delivery.model.RestaurantInfo;

import java.util.HashMap;

import de.greenrobot.event.EventBus;

/**
 * Created by goodview on 22/02/16.
 */
public class OrderProtocol extends BaseProtocol{

    public static final String PATH_MY_ORDERS = "delivery/order/restaurant/enquire/myuorder";

    public static int orderPage = 1;
    public static boolean isOrderLoadMore = false;

    public static void requestMyOrders() {
        orderPage = 1;

        HashMap<String, String> map = new HashMap<>();
        map.put("num", orderPage + "");
        Http.post(PATH_MY_ORDERS, map, new Http.Listener() {
            @Override
            public void onResponse(String result) {
                isOrderLoadMore = false;
                DeliveryOrderInfo.DeliveryOrderList orderList = new Gson().fromJson(getReturnObject(result), DeliveryOrderInfo.DeliveryOrderList.class);
                EventBus.getDefault().post(orderList);

            }
        });
    }

    public static void requestMore() {
        orderPage++;
        HashMap<String, String> map = new HashMap<>();
        map.put("num", orderPage + "");

        Http.post(PATH_MY_ORDERS, map, new Http.Listener() {
            @Override
            public void onResponse(String result) {
                isOrderLoadMore = true;
                DeliveryOrderInfo.DeliveryOrderList orderList = new Gson().fromJson(getReturnObject(result), DeliveryOrderInfo.DeliveryOrderList.class);
                EventBus.getDefault().post(orderList);

            }
        });
    }
}
