package com.gvbyc.ki41foo.delivery.UI.activity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.SimpleArrayMap;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gvbyc.ki41foo.delivery.OrderStatus;
import com.gvbyc.ki41foo.delivery.R;
import com.gvbyc.ki41foo.delivery.adapter.DeliveryMenuOrderAdapter;
import com.gvbyc.ki41foo.delivery.adapter.DeliveryMenuSectionedAdapter;
import com.gvbyc.ki41foo.delivery.model.DeliveryOrderInfo;
import com.gvbyc.ki41foo.delivery.model.MenuAdapterInfo;
import com.gvbyc.ki41foo.delivery.model.RestMenuInfo;
import com.gvbyc.ki41foo.delivery.model.RestaurantInfo;
import com.gvbyc.ki41foo.delivery.protocal.RestaurantProtocol;
import com.gvbyc.ki41foo.delivery.utils.CommonUtils;
import com.gvbyc.ki41foo.delivery.utils.LogUtils;
import com.gvbyc.ki41foo.delivery.utils.T;
import com.gvbyc.ki41foo.delivery.utils.UIUtils;
import com.gvbyc.ki41foo.delivery.utils.Utils;
import com.gvbyc.ki41foo.delivery.widget.PinnedHeaderListView;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.SortedMap;
import java.util.TreeMap;

import de.greenrobot.event.EventBus;
public class RestMenu extends BaseActivity {

    private boolean isScroll = true;

    private ListView left_listView;

    private DeliveryMenuOrderAdapter tempListAdapter;
    private RestaurantInfo restInfo;
    private TextView tv_total;
    private TextView tv_total2;
    private Button btn_confirm;
    private PinnedHeaderListView right_listview;
    private DeliveryMenuSectionedAdapter sectionedAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_menu);
        EventBus.getDefault().register(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        restInfo = EventBus.getDefault().removeStickyEvent(RestaurantInfo.class);
        getSupportActionBar().setTitle(restInfo.name);

        RestaurantProtocol.requestMenuList(restInfo.ti);
    }

    public void onEventMainThread(MenuAdapterInfo adapterInfo) {
        findViewById(R.id.loading).setVisibility(View.GONE);

        setUpCart();
        setUpTempOrderList();
        setUpRightList(adapterInfo.foods);
        setUpLeftList(adapterInfo.headers);
        setUpNoticeBanner();
    }

    private void setUpNoticeBanner() {
        if (!TextUtils.isEmpty(restInfo.notice)) {
            TextView tv_notice = (TextView) findViewById(R.id.notice);
            tv_notice.setVisibility(View.VISIBLE);

            IconicsDrawable icon = new IconicsDrawable(this)
                    .icon(GoogleMaterial.Icon.gmd_volume_up)
                    .color(Color.RED)
                    .sizeDp(12)
                    .paddingDp(0);
            IconicsDrawable icon2 = new IconicsDrawable(this)
                    .icon(GoogleMaterial.Icon.gmd_navigate_next)
                    .color(UIUtils.getColor(R.color.colorAccent))
                    .sizeDp(12)
                    .paddingDp(0);
            tv_notice.setCompoundDrawables(icon,null,icon2,null);
            tv_notice.setText(" " + restInfo.notice);
        }
    }

    private void setUpRightList(ArrayList<ArrayList<RestMenuInfo>> foods) {
        right_listview = (PinnedHeaderListView) findViewById(R.id.pinnedListView);
        right_listview.setVerticalScrollBarEnabled(false);
        right_listview.setDividerHeight(2);
        final int discount = Integer.parseInt(restInfo.discount);

        sectionedAdapter = new DeliveryMenuSectionedAdapter(this, foods, new DeliveryMenuSectionedAdapter.PlusMinusBtnListener() {
            @Override
            public void onPlus(RestMenuInfo info) {
                tempListAdapter.updateData(info);
                total += Integer.valueOf(info.foodprice);
                setTotal();
                if(total >= Integer.parseInt(restInfo.mindl)) {
                    btn_confirm.setEnabled(true);
                    btn_confirm.setText("去結算");
                }

            }

            @Override
            public void onMinus(RestMenuInfo info) {
                tempListAdapter.updateData(info);
                total -= Integer.valueOf(info.foodprice);
                setTotal();
                if(total < Integer.parseInt(restInfo.mindl)) {
                    btn_confirm.setEnabled(false);
                    btn_confirm.setText("$" +restInfo.mindl + "起送");
                }
            }

            private void setTotal() {
                if(discount!= 100) {
                    if(total == 0) {
                        tv_total.setPaintFlags(0);
                        tv_total.setText("購物車是空的");
                        tv_total2.setText("");
                    } else {
                        tv_total.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                        tv_total.setText("原價" +total);
                        tv_total2.setText("折後 $" + Utils.calculateDiscount(discount,total+""));
                    }
                } else {
                    tv_total2.setText("");
                    tv_total.setText(total == 0 ? "購物車是空的" :"總計：MOP " +total);
                }
            }
        });
        right_listview.setAdapter(sectionedAdapter);
    }

    private void setUpLeftList(ArrayList<String> headers) {

        left_listView = (ListView) findViewById(R.id.left_listview);
        left_listView.setDividerHeight(2);

        left_listView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, headers));

        left_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position,
                                    long arg3) {
                isScroll = false;

                for (int i = 0; i < left_listView.getChildCount(); i++) {
                    if (i == position) {
                        left_listView.getChildAt(i).setBackgroundColor(Color.rgb(255, 255, 255));
                    } else {
                        left_listView.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                    }
                }

                int rightSection = 0;
                for (int i = 0; i < position; i++) {
                    rightSection += sectionedAdapter.getCountForSection(i) + 1;
                }
                right_listview.setSelection(rightSection);

            }

        });

        right_listview.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView arg0, int arg1) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (isScroll) {
                    for (int i = 0; i < left_listView.getChildCount(); i++) {

                        if (i == sectionedAdapter.getSectionForPosition(firstVisibleItem)) {
                            left_listView.getChildAt(i).setBackgroundColor(
                                    Color.rgb(255, 255, 255));
                        } else {
                            left_listView.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);

                        }
                    }

                } else {
                    isScroll = true;
                }
            }
        });
    }


    private void setUpTempOrderList() {
        ListView tempOrderList = (ListView) findViewById(R.id.temp_order);
        tempListAdapter = new DeliveryMenuOrderAdapter(new LinkedHashMap<String,RestMenuInfo>());
        tempOrderList.setAdapter(tempListAdapter);
        tempOrderList.setDivider(null);

    }


    private View cover;
    private View cartContainer;
    //private ValueAnimator animator;

    int total;

    private void setUpCart() {
        cover = findViewById(R.id.cover);
        cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleCart();
            }
        });
        cartContainer = findViewById(R.id.rl_cart);
        tv_total = (TextView) findViewById(R.id.total);
        tv_total2 = (TextView) findViewById(R.id.total2);
        IconicsDrawable icon = new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_shopping_cart).colorRes(R.color.white).sizeDp(20);
        Button cart = (Button) findViewById(R.id.iv_cart);
        cart.setCompoundDrawablesWithIntrinsicBounds(null,icon,null,null);

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleCart();
            }
        });


        btn_confirm = (Button) findViewById(R.id.btn_right);
        btn_confirm.setText("$" +restInfo.mindl + "起送");
        btn_confirm.setEnabled(false);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<RestMenuInfo> orderList = tempListAdapter.getOrderList();
                if(orderList.size() == 0) {
                    T.showShort("你還沒有點餐");
                    return;
                }
                DeliveryOrderInfo orderInfo = new DeliveryOrderInfo();
                orderInfo.menuslist = orderList;
                orderInfo.restinfo = restInfo;
                orderInfo.discount = restInfo.discount;
                orderInfo.rtstatus = OrderStatus.ORDER_UNDEFINED+"";
                orderInfo.payprice = total+"";
                EventBus.getDefault().postSticky(orderInfo);
                startActivity(new Intent(RestMenu.this, DeliveryOrder.class));

            }
        });

    }

    boolean isCartOpen = false;

    private void toggleCart() {
        //open
        if(cartContainer.getTranslationY() > 0) {
            cartContainer.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).setDuration(450);
            cover.setVisibility(View.VISIBLE);
            isCartOpen = true;
        } else {
            cartContainer.animate().translationY(CommonUtils.dip2px(200)).setDuration(300);
            cover.setVisibility(View.GONE);
            isCartOpen = false;

        }
    }

    @Override
    public void onBackPressed() {
        if(isCartOpen)
            toggleCart();
        else
            super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
