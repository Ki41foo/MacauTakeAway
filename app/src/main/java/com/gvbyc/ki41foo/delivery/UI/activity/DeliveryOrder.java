package com.gvbyc.ki41foo.delivery.UI.activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gvbyc.ki41foo.delivery.AppManager;
import com.gvbyc.ki41foo.delivery.OrderStatus;
import com.gvbyc.ki41foo.delivery.R;
import com.gvbyc.ki41foo.delivery.UI.holder.MyOrderHolder;
import com.gvbyc.ki41foo.delivery.UserAccountManager;
import com.gvbyc.ki41foo.delivery.adapter.DeliveryMenuOrderAdapter;
import com.gvbyc.ki41foo.delivery.model.DeliveryOrderInfo;
import com.gvbyc.ki41foo.delivery.model.MyAction;
import com.gvbyc.ki41foo.delivery.model.RestMenuInfo;
import com.gvbyc.ki41foo.delivery.model.UserInfo;
import com.gvbyc.ki41foo.delivery.protocal.BaseProtocol;
import com.gvbyc.ki41foo.delivery.protocal.Http;
import com.gvbyc.ki41foo.delivery.protocal.OrderProtocol;
import com.gvbyc.ki41foo.delivery.protocal.RestaurantProtocol;
import com.gvbyc.ki41foo.delivery.utils.CommonUtils;
import com.gvbyc.ki41foo.delivery.utils.DateUtil;
import com.gvbyc.ki41foo.delivery.utils.T;
import com.gvbyc.ki41foo.delivery.utils.UIUtils;
import com.gvbyc.ki41foo.delivery.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import de.greenrobot.event.EventBus;

public class DeliveryOrder extends BaseActivity {


    private TextView tv_remark;
    private TextView tv_name;
    private TextView tv_phone;
    private TextView tv_address;
    private TextView tv_status;
    private String name;
    private String address;
    private DeliveryOrderInfo orderInfo;
    private Toolbar toolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_delivery_order);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        orderInfo = EventBus.getDefault().removeStickyEvent(DeliveryOrderInfo.class);
        getSupportActionBar().setTitle(Integer.valueOf(orderInfo.rtstatus) == OrderStatus.ORDER_UNDEFINED ? "確認訂單" : "訂單詳情");

        initView();
    }


    protected void initView() {

        setupContactMenu();
        setUpOrderList();

        TextView tv_rest = (TextView) findViewById(R.id.tv_rest);
        tv_rest.setText(orderInfo.restinfo.name);

        //total
        TextView tv_total = (TextView) findViewById(R.id.total);
        if(orderInfo.discount.equals("100")) {
            tv_total.setText("總計：MOP " + orderInfo.payprice);
        } else {
            tv_total.setText("共$" + orderInfo.payprice + " x " + orderInfo.discount.replace("0","") + "折 = 應付MOP " + Utils.calculateDiscount(Integer.parseInt(orderInfo.discount),orderInfo.payprice));
        }

        //remark
        tv_remark = (TextView) findViewById(R.id.remark);

        //delivery contact info input
        tv_status = (TextView) findViewById(R.id.tv_status);
        tv_name = (TextView) findViewById(R.id.name);
        tv_phone = (TextView) findViewById(R.id.contact);
        tv_phone.setText(UserAccountManager.getInstance().getUserInfo().phone);

        tv_address = (TextView) findViewById(R.id.address);


        Integer status = Integer.valueOf(orderInfo.rtstatus);
        switch (status) {
            case OrderStatus.ORDER_UNDEFINED:
                tv_phone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        T.showShort("目前只支持本人電話點餐，多謝！");
                    }
                });
                tv_status.setVisibility(View.INVISIBLE);
                tv_remark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showEditRemarkDialog();
                    }
                });


                ArrayList<UserInfo.ContactList> contactList = UserAccountManager.getInstance().getUserInfo().contactlist;
                if(contactList.size() == 0) {

                } else {
                    UserInfo.ContactList contactInfo = contactList.get(0);
                    tv_name.setText(contactInfo.name);
                    tv_address.setText(contactInfo.address);
                }


                //confirm order
                findViewById(R.id.btn_right).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(checkContactNotNull()) {
                            final AlertDialog dialog = new AlertDialog.Builder(DeliveryOrder.this, R.style.AppCompatAlertDialogStyle)
                                    .setTitle("確認提交訂單")
                                    .setMessage("商家將接收到你的訂單，請確保聯繫人信息準確")
                                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            createOrder();
                                        }
                                    })
                                    .setNegativeButton("返回修改",null)
                                    .create();
                            dialog.show();
                        }

                    }
                });

                break;

            case OrderStatus.ORDER_NEW:
                tv_status.setText("訂單狀態：等待商家接單");
                fillInCommonInfo();
                findViewById(R.id.btn_right).setVisibility(View.GONE);
                break;

            case OrderStatus.ORDER_REJECTED:
                tv_status.setText("訂單狀態：商家拒絕接單");
                fillInCommonInfo();
                findViewById(R.id.btn_right).setVisibility(View.GONE);
                TextView reject = (TextView) findViewById(R.id.reject);
                reject.setVisibility(View.VISIBLE);
                reject.setText("拒絕原因：" + orderInfo.cancelreason);

                break;
            case OrderStatus.ORDER_FINISHED:
                tv_status.setText("訂單狀態：已完成");
                fillInCommonInfo();
                findViewById(R.id.btn_right).setVisibility(View.GONE);
                break;

            case OrderStatus.ORDER_PRODUCING:
                tv_status.setText("訂單狀態：商家已接單,請等待");
                fillInCommonInfo();
                Button btn_right = (Button) findViewById(R.id.btn_right);
                btn_right.setText("確認收貨");
                btn_right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDeliveryConfirmDialog();
                    }
                });
                break;
            case OrderStatus.ORDER_DELIVERING:
                tv_status.setText("訂單狀態：正在配送");
                fillInCommonInfo();
                break;

            case OrderStatus.ORDER_REST_TIMEOUT:
                fillInCommonInfo();
                tv_status.setText("訂單狀態：商家長時間未接單,已關閉");
                findViewById(R.id.btn_right).setVisibility(View.GONE);
                break;
        }

    }

    private void setupContactMenu() {
        android.support.v7.widget.Toolbar.LayoutParams params = new android.support.v7.widget.Toolbar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.RIGHT;
        TextView btn = new TextView(this);
        btn.setTextColor(Color.WHITE);
        btn.setPadding(0,0, CommonUtils.dip2px(12),0);
        toolbar.addView(btn, params);
        btn.setText("聯絡商家");
        btn.setVisibility(View.VISIBLE);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog dialog = new AlertDialog.Builder(DeliveryOrder.this, R.style.AppCompatAlertDialogStyle)
                        .setTitle("聯絡商家")
                        .setMessage("撥打 " + orderInfo.restinfo.tel)
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Utils.makePhoneCall(orderInfo.restinfo.prefix,orderInfo.restinfo.tel);
                            }
                        })
                        .setNegativeButton("取消",null)
                        .create();
                dialog.show();
            }
        });
    }

    private void showDeliveryConfirmDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(DeliveryOrder.this, R.style.AppCompatAlertDialogStyle)
                .setTitle("確認收貨")
                .setMessage("外賣已經送到你手中？")
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final HashMap<String, String> map = new HashMap<>();
                        map.put("orderno", orderInfo.orderno);
                        map.put("rtstatus",orderInfo.rtstatus);
                        map.put("newrtstatus",OrderStatus.ORDER_FINISHED +"");
                        Http.post("delivery/order/restaurant/push/uorder", map, new Http.Listener() {
                            @Override
                            public void onResponse(String result) {
                                String msg = BaseProtocol.getMsg(result);
                                if(msg.equals("msg14")) {
                                    T.showShort("操作失敗，請稍後再試");
                                }
                                if(msg.equals("msg13")) {
                                    T.showLong("收貨成功");
                                    onBackPressed();
                                    RestaurantProtocol.requestMyOrders();
                                }
                            }
                        });
                    }
                })
                .setNegativeButton("取消",null)
                .create();
        dialog.show();
    }

    private void fillInCommonInfo() {
        if(TextUtils.isEmpty(orderInfo.ism)) {
            tv_remark.setVisibility(View.GONE);
        } else {
            tv_remark.setEnabled(false);
            tv_remark.setText("備注：" + orderInfo.ism);
        }

        tv_name.setText(orderInfo.receiver);
        tv_name.setEnabled(false);

        tv_address.setText(orderInfo.address);
        tv_address.setEnabled(false);

        TextView tv_time = (TextView) findViewById(R.id.order_time);
        TextView tv_no = (TextView) findViewById(R.id.order_no);

        tv_time.setVisibility(View.VISIBLE);
                tv_time.setText("下單時間：" + DateUtil.getDateTime(Long.parseLong(orderInfo.starttime)));

        tv_no.setVisibility(View.VISIBLE);
        tv_no.setText("訂單編號：" + orderInfo.orderno);
    }

    private void createOrder() {
        findViewById(R.id.btn_right).setEnabled(false);

        HashMap parameters = new HashMap();
        parameters.put("ti", orderInfo.restinfo.ti);
        parameters.put("menuslist", orderInfo.menuslist);
        parameters.put("paytype", "1");
        parameters.put("payprice",orderInfo.payprice);
        parameters.put("deliverprice", "0");
        parameters.put("receiver", name);
        parameters.put("recprefix", UserAccountManager.getInstance().getUserInfo().prefix);
        parameters.put("rectel", UserAccountManager.getInstance().getUserInfo().phone);
        parameters.put("address", address);
        parameters.put("ism", orderInfo.ism);

        Http.post(RestaurantProtocol.CREATE_ORDER, parameters, new Http.Listener() {
            @Override
            public void onResponse(String result) {
                String msg = BaseProtocol.getMsg(result);
                if(msg.equals("msg11")) {
                    T.showLong("已向餐廳落單，餐廳接單後你將會收到通知！");
                    modifyContact();
                    AppManager.getAppManager().finishActivity(RestMenu.class);
                    finish();
                    EventBus.getDefault().post(new MyAction(MyAction.HOME_TO_PAGE_TWO));
                    OrderProtocol.requestMyOrders();
                } else {
                    T.showShort("訂單提交失敗，" + msg);
                }
            }
        });
    }

    private void modifyContact() {
        ArrayList<UserInfo.ContactList> contactList = UserAccountManager.getInstance().getUserInfo().contactlist;
        if(contactList.size() == 0) {
            HashMap parameters = new HashMap();
            parameters.put("name", name);
            parameters.put("prefix", UserAccountManager.getInstance().getUserInfo().prefix);
            parameters.put("phone",UserAccountManager.getInstance().getUserInfo().phone);
            parameters.put("address",address);

            Http.post(RestaurantProtocol.CREATE_CONTACT, parameters, new Http.Listener() {
                @Override
                public void onResponse(String result) {
                    if (BaseProtocol.getMsg(result).equals("msg11")) {
                        UserInfo.ContactList contact = new Gson().fromJson(BaseProtocol.getReturnObject(result).get("object").getAsJsonObject(), UserInfo.ContactList.class);
                        UserInfo userInfo = UserAccountManager.getInstance().getUserInfo();
                        contact.address = address;
                        contact.name = name;
                        contact.prefix = UserAccountManager.getInstance().getUserInfo().prefix;
                        contact.phone = UserAccountManager.getInstance().getUserInfo().phone;
                        userInfo.contactlist.add(contact);
                        UserAccountManager.getInstance().updateUserInfo(userInfo);
                    }
                }
            });
        } else {
            UserInfo.ContactList contactInfo = contactList.get(0);
            if(!name.equals(contactInfo.name) || !address.equals(contactInfo.address)) {
                HashMap parameters = new HashMap();
                parameters.put("cid",contactInfo.cid);
                parameters.put("name", name);
                parameters.put("prefix", UserAccountManager.getInstance().getUserInfo().prefix);
                parameters.put("phone",UserAccountManager.getInstance().getUserInfo().phone);
                parameters.put("address",address);

                Http.post(RestaurantProtocol.MODIFY_CONTACT, parameters, new Http.Listener() {
                    @Override
                    public void onResponse(String result) {
                        if (BaseProtocol.getMsg(result).equals("msg13")) {
                            UserInfo userInfo = UserAccountManager.getInstance().getUserInfo();
                            UserInfo.ContactList contact = userInfo.contactlist.get(0);
                            contact.name = name;
                            contact.address = address;
                            UserAccountManager.getInstance().updateUserInfo(userInfo);
                        }
                    }
                });
            }

        }
    }

    private boolean checkContactNotNull() {
        name = tv_name.getText().toString().trim();
        if(TextUtils.isEmpty(name)) {
            T.showShort("聯繫人稱呼不能空");
            return false;
        }

        address = tv_address.getText().toString().trim();
        if(TextUtils.isEmpty(address)) {
            T.showShort("送貨地址不能空");
            return false;
        }

        return true;
    }



    private void showEditRemarkDialog() {
        final AppCompatEditText editText = new AppCompatEditText(this);
        int padding = UIUtils.getDimens(R.dimen.abc_dialog_padding_material);
        editText.setText(tv_remark.getText().toString().replace("備註：", ""));
        editText.setSelection(editText.getText().length());

        final AlertDialog dialog = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle)
                .setTitle("添加備註")
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String msg = editText.getText().toString().trim();
                        tv_remark.setText(TextUtils.isEmpty(msg) ? "" : "備註：" + msg);
                        orderInfo.ism = msg;
                    }
                }).setNegativeButton("取消", null).setView(editText, padding, padding, padding, 0).create();
        dialog.show();

        Utils.showSoftInput(editText);
    }

    private void setUpOrderList() {
        ListView listView = (ListView) findViewById(R.id.menu_list);
        LinkedHashMap<String, RestMenuInfo> map = new LinkedHashMap<>();
        ArrayList<RestMenuInfo> menuslist = orderInfo.menuslist;
        for (int i = 0; i < menuslist.size(); i++) {
            RestMenuInfo next = menuslist.get(i);
            map.put(i+"",next);
        }
        listView.setAdapter(new DeliveryMenuOrderAdapter(map));

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.abc_popup_enter, R.anim.abc_popup_exit);
    }
}
