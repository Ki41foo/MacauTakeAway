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
import com.gvbyc.ki41foo.delivery.UI.holder.RestaurantHolder;
import com.gvbyc.ki41foo.delivery.adapter.MyBaseAdapter;
import com.gvbyc.ki41foo.delivery.model.RestaurantInfo;
import com.gvbyc.ki41foo.delivery.protocal.RestaurantProtocol;
import com.gvbyc.ki41foo.delivery.utils.UIUtils;
import com.gvbyc.ki41foo.delivery.widget.MyRecyclerView;
import com.gvbyc.ki41foo.delivery.widget.ScrollingLinearLayoutManager;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by goodview on 16/03/16.
 */
public class RestaurantFragment extends BaseFragment  {


    private MyRecyclerView recyclerView;
    private RestaurantAdapter restaurantAdapter;
    private SwipeRefreshLayout srl;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate( R.layout.srl_list , null);
        recyclerView = (MyRecyclerView) rootView.findViewById(R.id.recyclerView);
        srl = (SwipeRefreshLayout) rootView;
        init();
        return rootView;
    }

    protected void init() {
        ScrollingLinearLayoutManager llm = new ScrollingLinearLayoutManager(UIUtils.getContext());
        recyclerView.setLayoutManager(llm);
        restaurantAdapter = new RestaurantAdapter(recyclerView);
        restaurantAdapter.setLoadMoreListener(new MyBaseAdapter.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                RestaurantProtocol.requestMoreRestaurant();
            }
        });
        recyclerView.setAdapter(restaurantAdapter);

        RestaurantProtocol.requestRestaurant("");
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RestaurantProtocol.requestRestaurant("");
                ((Home) getActivity()).clearSearch();
            }
        });

    }


    public void onEventMainThread(RestaurantInfo.RestaurantList obj) {

        srl.setRefreshing(false);

        final ArrayList<RestaurantInfo> list = obj.list;

        if (RestaurantProtocol.isRestLoadMore) {
            restaurantAdapter.addData(list);
        } else {
            restaurantAdapter.setData(list);

        }
    }


    class RestaurantAdapter extends MyBaseAdapter<RestaurantInfo> {

        public RestaurantAdapter(MyRecyclerView recyclerView) {
            super(recyclerView);
        }

        @Override
        public void onBindHolder(RecyclerView.ViewHolder holder, int position) {
            ((RestaurantHolder) holder).setData(data.get(position));
        }

        @Override
        public RecyclerView.ViewHolder onCreateHolder(ViewGroup parent, int viewType) {
            return RestaurantHolder.newInstance(getContext());
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
}
