package com.gvbyc.ki41foo.delivery.UI.holder;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.gvbyc.ki41foo.delivery.AppManager;
import com.gvbyc.ki41foo.delivery.ImageLoader;
import com.gvbyc.ki41foo.delivery.LocationHelper;
import com.gvbyc.ki41foo.delivery.R;
import com.gvbyc.ki41foo.delivery.UI.activity.Login;
import com.gvbyc.ki41foo.delivery.UI.activity.RestMenu;
import com.gvbyc.ki41foo.delivery.UserAccountManager;
import com.gvbyc.ki41foo.delivery.model.RestaurantInfo;
import com.gvbyc.ki41foo.delivery.protocal.Http;
import com.gvbyc.ki41foo.delivery.protocal.RestaurantProtocol;
import com.gvbyc.ki41foo.delivery.utils.IntentUtils;
import com.gvbyc.ki41foo.delivery.utils.LogUtils;
import com.gvbyc.ki41foo.delivery.utils.T;
import com.gvbyc.ki41foo.delivery.utils.UIUtils;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import de.greenrobot.event.EventBus;

/**
 * Created by goodview on 11/03/15.
 */
public class RestaurantHolder extends RecyclerView.ViewHolder{


    private ImageView icon;
    private TextView minimum;
    private TextView discount;
    private TextView name;
    private TextView distance;
    private TextView status;
    private View card;

    public RestaurantHolder(View itemView) {
        super(itemView);
        initView();
    }

    private void initView() {
        card = itemView.findViewById(R.id.cardview);
        icon = (ImageView) itemView.findViewById(R.id.icon);
        name = (TextView) itemView.findViewById(R.id.name);
        minimum = (TextView) itemView.findViewById(R.id.tv_2);
        discount = (TextView) itemView.findViewById(R.id.tv_3);
        status = (TextView) itemView.findViewById(R.id.status);
        distance = (TextView) itemView.findViewById(R.id.distance);
        IconicsDrawable compass = new IconicsDrawable(UIUtils.getContext())
                .icon(GoogleMaterial.Icon.gmd_near_me)
                .color(UIUtils.getColor(R.color.light_text))
                .sizeDp(12)
                .paddingDp(0);
        distance.setCompoundDrawablesWithIntrinsicBounds(compass,null,null,null);

    }

    public void setData(final RestaurantInfo info) {

        String imgUrl =  Http.IMG_SERVER + RestaurantProtocol.IMG_PATH + info.logoimage + ".jpg";
        ImageLoader.loadCenterCorp(icon,imgUrl);

        name.setText(info.name);
        minimum.setText("$" + info.mindl);


        if (!info.discount.equals("100")) {
            discount.setVisibility(View.VISIBLE);
            discount.setText("全店" + info.discount.replace("0","")+"折");
        } else {
            discount.setVisibility(View.GONE);
        }

        status.setVisibility(View.GONE);
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserAccountManager.getInstance().isLogin()) {
                    EventBus.getDefault().postSticky(info);
                    IntentUtils.startActivity(AppManager.getAppManager().currentActivity(), RestMenu.class);
                } else {
                    IntentUtils.startActivity(AppManager.getAppManager().currentActivity(), Login.class);
                }
            }
        });

//        if(info.closed.equals("0") && info.isclose.equals("0") && info.isoutrange.equals("0")) {
//            status.setVisibility(View.GONE);
//            card.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (UserAccountManager.getInstance().isLogin()) {
//                        EventBus.getDefault().postSticky(info);
//                        IntentUtils.startActivity(AppManager.getAppManager().currentActivity(), RestMenu.class);
//                    } else {
//                        IntentUtils.startActivity(AppManager.getAppManager().currentActivity(), Login.class);
//                    }
//                }
//            });
//        } else if (info.isoutrange.equals("1")) {
//            status.setVisibility(View.VISIBLE);
//            status.setText("超出配送範圍");
//            card.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    T.showShort("太遠了，商家送唔到");
//                }
//            });
//        } else {
//            status.setVisibility(View.VISIBLE);
//            status.setText("休息中");
//            card.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    T.showShort("商家休息了，暫時唔外送");
//                }
//            });
//        }

        double d = DistanceUtil.getDistance(new LatLng(LocationHelper.getDefault().getLat(),LocationHelper.getDefault().getLon()), new LatLng(Double.parseDouble(info.latitude), Double.parseDouble(info.longitude)));
        this.distance.setText(" " + (int)d + "m");
    }



    public static RestaurantHolder newInstance(Context context) {

        return new RestaurantHolder(View.inflate(context, R.layout.rest_info,null));
    }
}
