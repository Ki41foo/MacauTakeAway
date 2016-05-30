package com.gvbyc.ki41foo.delivery.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gvbyc.ki41foo.delivery.R;
import com.gvbyc.ki41foo.delivery.model.RestMenuInfo;
import com.gvbyc.ki41foo.delivery.utils.UIUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;


/**
 * Created by goodview on 11/02/16.
 */
public class DeliveryMenuOrderAdapter extends BaseAdapter {

    LinkedHashMap<String,RestMenuInfo> orderMap;

    public void updateData(RestMenuInfo info) {
        if(info.count.equals("0")) {
            orderMap.remove(info.foodno);
        } else {
            this.orderMap.put(info.foodno,info);
        }
        notifyDataSetChanged();
    }


    public DeliveryMenuOrderAdapter(LinkedHashMap<String,RestMenuInfo> orderMap ) {
        this.orderMap = orderMap;
    }

    public ArrayList<RestMenuInfo> getOrderList() {
        ArrayList<RestMenuInfo> list = new ArrayList<>();
        Iterator<RestMenuInfo> iterator = orderMap.values().iterator();
        for (int i = 0; i < orderMap.size(); i++) {
            if(iterator.hasNext()) {
                list.add(iterator.next());
            }
        }
        return list;
    }

    @Override
    public void notifyDataSetChanged() {


        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return orderMap == null ? 0 : orderMap.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = View.inflate(UIUtils.getContext(), R.layout.delivery_order_menu_item, null);
        }

        TextView tv_name = (TextView) convertView.findViewById(R.id.name);
        TextView tv_price = (TextView) convertView.findViewById(R.id.price);
        TextView tv_count = (TextView) convertView.findViewById(R.id.count);

        Iterator<RestMenuInfo> iterator = orderMap.values().iterator();
        RestMenuInfo next = null;
        for (int i = 0; i < orderMap.size(); i++) {
            if(iterator.hasNext()) {
                next = iterator.next();
            }
            if(i == position) break;
        }
        tv_name.setText(next.foodname);
        tv_count.setText(next.count + "份");
        tv_price.setText("$" + next.foodprice+ "*" +next.count);
        return convertView;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}
