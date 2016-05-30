package com.gvbyc.ki41foo.delivery.UI.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gvbyc.ki41foo.delivery.R;
import com.gvbyc.ki41foo.delivery.UI.activity.Home;
import com.gvbyc.ki41foo.delivery.UI.holder.MyOrderHolder;
import com.gvbyc.ki41foo.delivery.UI.holder.RestaurantHolder;
import com.gvbyc.ki41foo.delivery.UserAccountManager;
import com.gvbyc.ki41foo.delivery.adapter.MyBaseAdapter;
import com.gvbyc.ki41foo.delivery.model.DeliveryOrderInfo;
import com.gvbyc.ki41foo.delivery.model.RestaurantInfo;
import com.gvbyc.ki41foo.delivery.protocal.OrderProtocol;
import com.gvbyc.ki41foo.delivery.protocal.RestaurantProtocol;
import com.gvbyc.ki41foo.delivery.utils.UIUtils;
import com.gvbyc.ki41foo.delivery.widget.MyRecyclerView;
import com.gvbyc.ki41foo.delivery.widget.ScrollingLinearLayoutManager;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by goodview on 16/03/16.
 */
public class MyOrderFragment  extends BaseFragment  {
    private MyRecyclerView recyclerView;
    private MyOrderAdapter myOrderAdapter;
    private SwipeRefreshLayout srl;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView;
        if (UserAccountManager.getInstance().isLogin()) {
            rootView = inflater.inflate(R.layout.srl_list, null);
            recyclerView = (MyRecyclerView) rootView.findViewById(R.id.recyclerView);
            srl = (SwipeRefreshLayout) rootView;
            initView();
            initData();
        } else {
            rootView = super.onCreateView(inflater,container, savedInstanceState);
        }
        return rootView;
    }

    private void initView() {
        ScrollingLinearLayoutManager llm = new ScrollingLinearLayoutManager(UIUtils.getContext());
        recyclerView.setLayoutManager(llm);
        myOrderAdapter = new MyOrderAdapter(recyclerView);
        myOrderAdapter.setLoadMoreListener(new MyBaseAdapter.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                OrderProtocol.requestMore();
            }
        });
        recyclerView.setAdapter(myOrderAdapter);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                OrderProtocol.requestMyOrders();
            }
        });
    }

    private void initData() {
        OrderProtocol.requestMyOrders();
        srl.post(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(true);
            }
        });
    }

    public void onEventMainThread(DeliveryOrderInfo.DeliveryOrderList data) {
        srl.setRefreshing(false);
        final ArrayList<DeliveryOrderInfo> list = data.list;
        if (OrderProtocol.isOrderLoadMore) {
            myOrderAdapter.addData(list);
        } else {
            myOrderAdapter.setData(list);

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    class MyOrderAdapter extends MyBaseAdapter<DeliveryOrderInfo> {

        public MyOrderAdapter(MyRecyclerView recyclerView) {
            super(recyclerView);
        }

        @Override
        public void onBindHolder(RecyclerView.ViewHolder holder, int position) {
            ((MyOrderHolder) holder).setData(data.get(position));
        }

        @Override
        public RecyclerView.ViewHolder onCreateHolder(ViewGroup parent, int viewType) {
            return MyOrderHolder.newInstance(getContext());
        }
    }
}
