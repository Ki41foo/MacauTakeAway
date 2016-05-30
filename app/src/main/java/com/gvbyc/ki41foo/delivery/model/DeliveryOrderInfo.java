package com.gvbyc.ki41foo.delivery.model;

import java.util.ArrayList;

/**
 * Created by goodview on 16/02/16.
 */
public class DeliveryOrderInfo {


    //    prefix", "2");
//            parameters.put("phone", "13692236372");
//    parameters.put("ti", "2");
//    parameters.put("menuslist", menuslist);
//    parameters.put("paytype", "1");
//    parameters.put("payprice", "78");
//    parameters.put("deliverprice", "0");
//    parameters.put("delivertype", "1");
//    parameters.put("receiver", "kimoji");
//    parameters.put("recprefix", "1");
//    parameters.put("rectel", "62782121");
//    parameters.put("address", "宋玉生");
//    parameters.put("ism", "熱奶茶，凍檸樂");
//    parameters.put("paytime", "1234567891234");
//    parameters.put("starttime", "1234567891234");
//    parameters.put("delivertime", "1234567891234");
//    parameters.put("completetime", "1234567891234");
    public RestaurantInfo restinfo;
    public ArrayList<RestMenuInfo> menuslist;
    public String paytype;
    public String payprice;
    public String deliverprice;
    public String delivertype;
    public String receiver;
    public String recprefix;
    public String rectel;
    public String address;
    public String ism = "";
    public String paytime;
    public String starttime;
    public String delivertime;
    public String completetime;

    public String rtstatus;
    public String cancelreason;
    public String comment;
    public String orderno;

    public String discount;

    public class DeliveryOrderList {
        public ArrayList<DeliveryOrderInfo> list;
    }

}
