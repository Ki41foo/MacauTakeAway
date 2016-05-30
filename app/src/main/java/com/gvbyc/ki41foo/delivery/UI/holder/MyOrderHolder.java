package com.gvbyc.ki41foo.delivery.UI.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gvbyc.ki41foo.delivery.AppManager;
import com.gvbyc.ki41foo.delivery.ImageLoader;
import com.gvbyc.ki41foo.delivery.OrderStatus;
import com.gvbyc.ki41foo.delivery.R;
import com.gvbyc.ki41foo.delivery.UI.activity.DeliveryOrder;
import com.gvbyc.ki41foo.delivery.UI.activity.Login;
import com.gvbyc.ki41foo.delivery.UI.activity.RestMenu;
import com.gvbyc.ki41foo.delivery.UserAccountManager;
import com.gvbyc.ki41foo.delivery.model.DeliveryOrderInfo;
import com.gvbyc.ki41foo.delivery.model.RestaurantInfo;
import com.gvbyc.ki41foo.delivery.protocal.Http;
import com.gvbyc.ki41foo.delivery.protocal.RestaurantProtocol;
import com.gvbyc.ki41foo.delivery.utils.DateUtil;
import com.gvbyc.ki41foo.delivery.utils.IntentUtils;
import com.gvbyc.ki41foo.delivery.utils.UIUtils;
import com.gvbyc.ki41foo.delivery.utils.Utils;

import de.greenrobot.event.EventBus;

/**
 * Created by goodview on 11/03/15.
 */
public class MyOrderHolder extends RecyclerView.ViewHolder{


    private DeliveryOrderInfo info;
    private ImageView icon;
    private TextView tv_state;
    private TextView price;
    private TextView name;
    private TextView time;

    public MyOrderHolder(View itemView) {
        super(itemView);

        initView();
    }

    private void initView() {
        itemView.findViewById(R.id.cardview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().postSticky(info);
                IntentUtils.startActivity(AppManager.getAppManager().currentActivity(), DeliveryOrder.class);
            }
        });

        icon = (ImageView) itemView.findViewById(R.id.icon);
        name = (TextView) itemView.findViewById(R.id.name);
        tv_state = (TextView) itemView.findViewById(R.id.tv_2);
        price = (TextView) itemView.findViewById(R.id.tv_3);
        time = (TextView) itemView.findViewById(R.id.time);


    }

    public void setData(DeliveryOrderInfo info) {
        this.info = info;

        String imgUrl = Http.IMG_SERVER + RestaurantProtocol.IMG_PATH + info.restinfo.logoimage + ".jpg";
        ImageLoader.loadCenterCorp(icon,imgUrl);

        name.setText(info.restinfo.name);
        time.setText("下單時間：" + DateUtil.getDateTime(Long.parseLong(info.starttime)));
        price.setText("總計：$" + info.payprice);

        int i = Integer.parseInt(info.rtstatus);
        switch (i) {
            case OrderStatus.ORDER_NEW:
                tv_state.setText("等待接單");
                tv_state.setBackgroundColor(UIUtils.getColor(R.color.order_status_new));
                break;

            case OrderStatus.ORDER_REJECTED:
                tv_state.setText("被拒絕");
                tv_state.setBackgroundColor(UIUtils.getColor(R.color.order_status_rejected));
                break;

            case OrderStatus.ORDER_PRODUCING:
                tv_state.setText("已接單");
                tv_state.setBackgroundColor(UIUtils.getColor(R.color.order_status_produce));
                break;

            case OrderStatus.ORDER_REST_TIMEOUT:
                tv_state.setText("漏接單");
                tv_state.setBackgroundColor(UIUtils.getColor(R.color.order_status_timeout));
                break;

            case OrderStatus.ORDER_FINISHED:
                tv_state.setText("已完成");
                tv_state.setBackgroundColor(UIUtils.getColor(R.color.order_status_finished));
                break;
        }

    }

    public static MyOrderHolder newInstance(Context context) {

        return new MyOrderHolder(View.inflate(context, R.layout.order_info,null));
    }
}
