package com.gvbyc.ki41foo.delivery.protocal;

import android.support.v4.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.gvbyc.ki41foo.delivery.BaseApplication;
import com.gvbyc.ki41foo.delivery.LocationHelper;
import com.gvbyc.ki41foo.delivery.model.DeliveryOrderInfo;
import com.gvbyc.ki41foo.delivery.model.MenuAdapterInfo;
import com.gvbyc.ki41foo.delivery.model.RestMenuInfo;
import com.gvbyc.ki41foo.delivery.model.RestaurantInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Created by goodview on 16/03/16.
 */
public class RestaurantProtocol extends BaseProtocol {

    public static final String RESTAURANT_LIST = "delivery/restaurant/enquire/list/";
    public static final String IMG_PATH = "targetimg/deliverymerchant/";

    public static final String DEFAULT = "1";

    public static String sortType = DEFAULT;
    public static int restPage = 1;
    public static boolean isRestLoadMore = false;

    public static String restKeyword = "";
    public static String deliverZone = "1";



    public static final String QUERY_MENU = "delivery/restaurantmenus/enquire/list";
    public static final String MENU_IMG_PATH = "targetimg/deliverymenus/";

    public static final String CREATE_ORDER = "delivery/order/restaurant/create";
    public static final String QUERY_ORDER = "delivery/order/restaurant/enquire/myuorder";


    public static final String MODIFY_CONTACT = "delivery/user/modify/contact";
    public static final String CREATE_CONTACT = "delivery/user/create/contact";

    public  static int myOrderPage = 1;
    public  static boolean myOrderLoadMore = false;


    public static void requestRestaurant(String keyword) {
        LocationHelper.getDefault().request();
        if (keyword != null) restKeyword = keyword;
        restPage = 1;
        HashMap<String, String> map = new HashMap<>();
        map.put("type", sortType);
        map.put("num", restPage + "");
        map.put("key", restKeyword);
        map.put("zone", deliverZone);
        map.put("latitude", LocationHelper.getDefault().getLat() + "");
        map.put("longitude", LocationHelper.getDefault().getLon() + "");

        Http.post(RESTAURANT_LIST, map, new Http.Listener() {
            @Override
            public void onResponse(String result) {
                isRestLoadMore = false;
                RestaurantInfo.RestaurantList restaurantList = new Gson().fromJson(getReturnObject(result), RestaurantInfo.RestaurantList.class);
                EventBus.getDefault().post(restaurantList);
            }
        });
    }

    public static void requestMoreRestaurant() {
        HashMap<String, String> map = new HashMap<>();
        map.put("type", sortType);
        restPage++;
        map.put("num", restPage + "");
        map.put("key", restKeyword);
        map.put("zone", deliverZone);
        map.put("latitude", LocationHelper.getDefault().getLat() + "");
        map.put("longitude", LocationHelper.getDefault().getLon() + "");

        Http.post(RESTAURANT_LIST, map, new Http.Listener() {
            @Override
            public void onResponse(String result) {
                isRestLoadMore = true;
                RestaurantInfo.RestaurantList restaurantList = new Gson().fromJson(getReturnObject(result), RestaurantInfo.RestaurantList.class);
                EventBus.getDefault().post(restaurantList);
            }
        });
    }

    public static void requestMenuList(String ti) {

        HashMap<String, String> map = new HashMap<>();
        map.put("ti", ti);
        Http.post(QUERY_MENU , map, new Http.Listener() {
            @Override
            public void onResponse(String result) {

                RestMenuInfo.RestMenuInfoList list = new Gson().fromJson(getReturnObject(result), RestMenuInfo.RestMenuInfoList.class);
                MenuAdapterInfo menuAdapterInfo = processData(list);
                EventBus.getDefault().post(menuAdapterInfo);
            }
        });
    }

    public static void requestMyOrders() {
        HashMap<String, String> map = new HashMap<>();
        map.put("num", "1");
        Http.post(QUERY_ORDER , map, new Http.Listener() {
            @Override
            public void onResponse(String result) {
                myOrderPage = 1;
                myOrderLoadMore = false;
                DeliveryOrderInfo.DeliveryOrderList list = new Gson().fromJson(getReturnObject(result), DeliveryOrderInfo.DeliveryOrderList.class);
                EventBus.getDefault().post(list);
            }
        });
    }

    public static void requestMyOrdersMore() {
        myOrderPage++;
        HashMap<String, String> map = new HashMap<>();
        map.put("num", myOrderPage+"");
        Http.post(QUERY_ORDER , map, new Http.Listener() {
            @Override
            public void onResponse(String result) {

                myOrderLoadMore = true;
                DeliveryOrderInfo.DeliveryOrderList list = new Gson().fromJson(getReturnObject(result), DeliveryOrderInfo.DeliveryOrderList.class);
                EventBus.getDefault().post(list);
            }
        });
    }

    private static MenuAdapterInfo processData(RestMenuInfo.RestMenuInfoList list) {
        MenuAdapterInfo adapterInfo = new MenuAdapterInfo();
        for (RestMenuInfo info : list.list) {
            if(adapterInfo.headers.contains(info.foodtypename)) {
                int i = adapterInfo.headers.indexOf(info.foodtypename);
                adapterInfo.foods.get(i).add(info);
            } else {
                ArrayList<RestMenuInfo> menuList = new ArrayList<>();
                adapterInfo.headers.add(info.foodtypename);
                menuList.add(info);
                adapterInfo.foods.add(menuList);
            }
        }
        return adapterInfo;
    }
}
