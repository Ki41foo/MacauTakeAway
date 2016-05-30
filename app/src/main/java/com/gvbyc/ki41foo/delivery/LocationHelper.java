package com.gvbyc.ki41foo.delivery;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.gvbyc.ki41foo.delivery.model.MyAction;
import com.gvbyc.ki41foo.delivery.utils.LogUtils;
import com.gvbyc.ki41foo.delivery.utils.UIUtils;

import de.greenrobot.event.EventBus;

/**
 * Created by goodview on 19/04/16.
 */
public class LocationHelper {

    private  LocationClient locationClient;

    private  double lat;
    private  double lon;

    private static LocationHelper helper;

    private LocationHelper() {

    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public static LocationHelper getDefault() {
        if (null == helper) {
            helper = new LocationHelper();
        }
        return helper;
    }

    public void init() {


        //location client config
        locationClient = new LocationClient(UIUtils.getContext());

        LocationListener locationListener = new LocationListener();
        locationClient.registerLocationListener(locationListener);

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置定位模式
        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
        //option.setIsNeedAddress(true);//返回的定位结果包含地址信息
        option.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向
        option.setOpenGps(true);
        option.setProdName("P00003-Delivery");
        locationClient.setLocOption(option);

        NotifyListener notifyListener = new NotifyListener();
        locationClient.registerNotify(notifyListener);

        locationClient.start();
    }

    public  void request() {
        if(locationClient == null) {
            init();
        } else {
            locationClient.start();
        }
    }

     class NotifyListener extends BDNotifyListener {
        @Override
        public void onNotify(BDLocation bdLocation, float v) {
            super.onNotify(bdLocation, v);
        }
    }


     class LocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            lat = location.getLatitude();
            lon =  location.getLongitude();
            if(lat == 4.9E-324) return;
            EventBus.getDefault().post(new MyAction(MyAction.LOC_READY));
            locationClient.stop();
        }
    }
}
