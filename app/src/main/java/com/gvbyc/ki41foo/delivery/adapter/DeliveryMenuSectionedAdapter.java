package com.gvbyc.ki41foo.delivery.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gvbyc.ki41foo.delivery.AppManager;
import com.gvbyc.ki41foo.delivery.ImageLoader;
import com.gvbyc.ki41foo.delivery.R;
import com.gvbyc.ki41foo.delivery.ThreadManager;
import com.gvbyc.ki41foo.delivery.model.RestMenuInfo;
import com.gvbyc.ki41foo.delivery.protocal.Http;
import com.gvbyc.ki41foo.delivery.protocal.RestaurantProtocol;
import com.gvbyc.ki41foo.delivery.utils.UIUtils;
import com.gvbyc.ki41foo.delivery.utils.ViewUtils;
import com.gvbyc.ki41foo.delivery.widget.SectionedBaseAdapter;

import java.util.ArrayList;
import java.util.HashMap;


public class DeliveryMenuSectionedAdapter extends SectionedBaseAdapter {

    private Context mContext;
    ArrayList<ArrayList<RestMenuInfo>> foods;
    private PlusMinusBtnListener listener;
    int[] cart_locs = new int[2];

    public DeliveryMenuSectionedAdapter(Context context, ArrayList<ArrayList<RestMenuInfo>> foods, PlusMinusBtnListener listener) {
        this.mContext = context;
        this.foods = foods;
        this.listener = listener;

        //get cart icon location on screen
        UIUtils.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((Activity)mContext).findViewById(R.id.iv_cart).getLocationOnScreen(cart_locs);
                cart_locs[0] += UIUtils.dip2px(16);
            }
        },200);
    }

    @Override
    public Object getItem(int section, int position) {

        return foods.get(position);
    }

    @Override
    public long getItemId(int section, int position) {
        return position;
    }

    @Override
    public int getSectionCount() {
        return foods.size();
    }

    @Override
    public int getCountForSection(int section) {
        return foods.get(section).size();
    }

    @Override
    public View getItemView(final int section, final int position, View convertView, ViewGroup parent) {
        MenuItemHolder holder = new MenuItemHolder(mContext, convertView);
        holder.setInfo(foods.get(section).get(position));

        return holder.getView();
    }

    @Override
    public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
        View layout = null;
        if (convertView == null) {
            LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = inflator.inflate(R.layout.delivery_header_item, null);
        } else {
            layout = convertView;
        }
        layout.setClickable(false);
        ((TextView) layout.findViewById(R.id.name)).setText(foods.get(section).get(0).foodtypename);
        return layout;
    }


    class MenuItemHolder {

        private final View rootView;
        private final Button btn_plus;
        private final TextView timeout;
        private final Button btn_minus;
        private final TextView tv_count;
        private final TextView tv_name;
        private final TextView tv_price;
        private final ImageView icon;
        private int[] bullet_locs;

        public MenuItemHolder(Context context, View convertView) {
            if(convertView == null) {
                rootView = View.inflate(context, R.layout.delivery_menu_item, null);
            } else {
                rootView = convertView;
            }

            btn_plus = (Button)rootView.findViewById(R.id.btn_plus);
            timeout = (TextView)rootView.findViewById(R.id.timeout);
            btn_minus = (Button)rootView.findViewById(R.id.btn_minus);
            tv_count = (TextView)rootView.findViewById(R.id.count);
            tv_name = (TextView)rootView.findViewById(R.id.name);
            tv_price = (TextView)rootView.findViewById(R.id.price);
            icon = (ImageView)rootView.findViewById(R.id.img);

        }

        View getView() {
            return rootView;
        }

        public void setInfo(final RestMenuInfo info) {
            tv_count.setText(info.count);
            tv_name.setText(info.foodname);
            tv_price.setText("$" + info.foodprice + "/份");
            if (info.isot.equals("1")) {
                btn_plus.setVisibility(View.INVISIBLE);
                timeout.setVisibility(View.VISIBLE);
            } else {
                btn_plus.setVisibility(View.VISIBLE);
                timeout.setVisibility(View.INVISIBLE);
            }

            ImageLoader.loadCenterCorp(icon, Http.IMG_SERVER + RestaurantProtocol.MENU_IMG_PATH + info.image + ".jpg");

            if(info.count.equals("0")) {
                btn_minus.setVisibility(View.INVISIBLE);
                tv_count.setVisibility(View.INVISIBLE);
            } else {
                btn_minus.setVisibility(View.VISIBLE);
                tv_count.setVisibility(View.VISIBLE);
            }

            btn_minus.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer count_int = Integer.valueOf(info.count);
                    count_int --;
                    info.count = count_int+"";

                    tv_count.setText(String.valueOf(info.count));
                    if(info.count.equals("0")) {
                        btn_minus.setVisibility(View.INVISIBLE);
                        tv_count.setVisibility(View.INVISIBLE);
                    } else {
                        btn_minus.setVisibility(View.VISIBLE);
                        tv_count.setVisibility(View.VISIBLE);
                    }
                    listener.onMinus(info);

                }
            });

            btn_plus.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer count_int = Integer.valueOf(info.count);
                    count_int ++;
                    info.count = count_int+"";

                    tv_count.setText(info.count);
                    if(info.count.equals("0")) {
                        btn_minus.setVisibility(View.INVISIBLE);
                        tv_count.setVisibility(View.INVISIBLE);
                    } else {
                        btn_minus.setVisibility(View.VISIBLE);
                        tv_count.setVisibility(View.VISIBLE);
                    }

                    listener.onPlus(info);

                    shootBulletAnimation(v);
                }
            });


        }

        private void shootBulletAnimation(View v) {
            bullet_locs = new int[2];
            btn_plus.getLocationOnScreen(bullet_locs);

            TranslateAnimation trans = new TranslateAnimation(
                    Animation.ABSOLUTE, bullet_locs[0],
                    Animation.ABSOLUTE, cart_locs[0],
                    Animation.ABSOLUTE, bullet_locs[1] - UIUtils.dip2px(12),
                    Animation.ABSOLUTE, cart_locs[1]);
            trans.setDuration(1000);
            trans.setInterpolator(PathInterpolatorCompat.create(0.4f, -0.4f, 0.9f, 1));

            ScaleAnimation scale = new ScaleAnimation(1, 2,
                    1, 2,
                    Animation.ABSOLUTE, bullet_locs[0] + UIUtils.dip2px(64),
                    Animation.ABSOLUTE, bullet_locs[1] +UIUtils.dip2px(64));
            scale.setRepeatCount(1);
            scale.setRepeatMode(Animation.REVERSE);
            scale.setDuration(500);

            AnimationSet set = new AnimationSet(false);
            set.addAnimation(trans);
            set.addAnimation(scale);

            final View bullet = new View(mContext);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.height = UIUtils.dip2px(12);
            params.width = UIUtils.dip2px(12);
            bullet.setLayoutParams(params);
            bullet.setBackgroundDrawable(UIUtils.getDrawable(R.drawable.round_red_gradient));
            AppManager.getAppManager().currentActivity().addContentView(bullet,params);
            bullet.startAnimation(set);
            bullet.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ViewUtils.removeSelfFromParent(bullet);
                }
            },1000);
        }
    }

    public interface PlusMinusBtnListener {
        void onPlus(RestMenuInfo info);
        void onMinus(RestMenuInfo info);
    }

}
