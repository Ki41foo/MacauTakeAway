package com.gvbyc.ki41foo.delivery;

/**
 * Created by goodview on 22/01/16.
 */
public class OrderStatus {
    public static final  int  ORDER_UNDEFINED = 0;
    public static final  int  ORDER_NEW = 1;
    public static final  int  ORDER_REJECTED = 2;
    public static final  int  ORDER_PRODUCING = 3;
    public static final  int  ORDER_WAIT_DELIVER  = 4;
    public static final  int  ORDER_DELIVERING = 5;
    public static final  int  ORDER_FINISHED = 6;
    public static final  int  ORDER_REST_TIMEOUT = 7;
}
