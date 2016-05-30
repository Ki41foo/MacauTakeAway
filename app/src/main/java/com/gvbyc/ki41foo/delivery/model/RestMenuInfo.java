package com.gvbyc.ki41foo.delivery.model;

import java.util.ArrayList;

/**
 * Created by goodview on 04/05/15.
 */
public class RestMenuInfo {

//    "foodno": “菜編號”,  string
//    "foodname": “名字”,  string
//    "foodtype": “菜式類型”,int
//    "foodprice": 價錢,int
//    "packprice": 打包費,int
//    "foodspicy": 辣的程度,int
//    "ish": “是否熱的”,int
//    "isg": 是否推薦,int
//    "gnum": 點讚次數,int
//    "moncount": “月銷量”,int
//    "tolcount": “總銷量”,   int
//    "image": “圖片”,string
//    "ism": “備註”,string


    public String foodno;
    public String foodname;
    public String foodtype;
    public String foodtypename;
    public String foodprice;
    public String packprice;
    public String foodspicy;
    public String ish;
    public String isg;
    public String gnum;
    public String moncount;
    public String tolcount;
    public String image;
    public String ism;
    public String issout;
    public String menuno;
    public String isot;

    public String count = "0";




    public class RestMenuInfoList {
        public ArrayList<RestMenuInfo> list;
    }
}
