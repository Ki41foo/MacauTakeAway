package com.gvbyc.ki41foo.delivery.UI.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.baidu.mapapi.model.LatLng;
import com.gvbyc.ki41foo.delivery.LocationHelper;
import com.gvbyc.ki41foo.delivery.R;
import com.gvbyc.ki41foo.delivery.UpdateHelper;
import com.gvbyc.ki41foo.delivery.model.MyAction;
import com.gvbyc.ki41foo.delivery.utils.CommonUtils;
import com.gvbyc.ki41foo.delivery.utils.T;
import com.gvbyc.ki41foo.delivery.utils.UIUtils;

import de.greenrobot.event.EventBus;

/**
 * Created by goodview on 31/03/16.
 */
public class Splash extends BaseActivity {

    int permsRequestCode = 200;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == permsRequestCode) {
            boolean locationGranted = checkSelfPermission("android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED;
            if(locationGranted) {
                goNext(0);
            } else {
                String[] perms = {"android.permission.ACCESS_FINE_LOCATION"};
                requestPermissions(perms,permsRequestCode);
                T.showLong("請同意<餸上門>獲取你的位置，否則無法為你找到附近餐廳！");
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        EventBus.getDefault().register(this);

        showAnimation();
        init();
    }

    private void showAnimation() {
        View logo = findViewById(R.id.logo);
        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                1,
                Animation.RELATIVE_TO_SELF,
                0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);

        AnimationSet set = new AnimationSet(true);
        set.setInterpolator(new DecelerateInterpolator(2));
        set.setDuration(700);
        set.addAnimation(scaleAnimation);
        set.addAnimation(translateAnimation);

        logo.setAnimation(set);
        set.start();
    }


    private void init() {
        new UpdateHelper(this).check();
        LocationHelper.getDefault().init();
    }

    private void goNext(int delay) {
        UIUtils.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isLocReady) {
                    startActivity(new Intent(Splash.this,Home.class));
                    finish();
                } else {
                    LocationHelper.getDefault().request();
                    goNext(1000);
                }
            }
        },delay);
    }

    boolean isLocReady = false;


    public void onEventMainThread(MyAction action) {

        switch (action.getEvent()) {
            case MyAction.NO_UPDATE_AVAILABLE:

                //support version 6
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                    UIUtils.postDelayed(new Runnable() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void run() {
                            String[] perms = {"android.permission.ACCESS_FINE_LOCATION","android.permission.CALL_PHONE"};
                            requestPermissions(perms,permsRequestCode);
                        }
                    },1200);
                } else {
                    goNext(1200);
                }
                break;

            case MyAction.NETWORK_ERROR:
                final View retry = findViewById(R.id.retry);
                retry.setVisibility(View.VISIBLE);
                retry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        init();
                        retry.setVisibility(View.INVISIBLE);
                    }
                });
                break;

            case MyAction.LOC_READY:
                isLocReady = true;
                break;
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        CommonUtils.hideStatusBar(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
