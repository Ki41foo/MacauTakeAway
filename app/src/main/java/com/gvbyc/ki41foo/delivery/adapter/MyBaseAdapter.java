package com.gvbyc.ki41foo.delivery.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.gvbyc.ki41foo.delivery.R;
import com.gvbyc.ki41foo.delivery.utils.UIUtils;
import com.gvbyc.ki41foo.delivery.widget.MyRecyclerView;
import com.gvbyc.ki41foo.delivery.widget.ScrollingLinearLayoutManager;

import java.util.List;


/**
 * Created by goodview on 23/02/16.
 */
public abstract class MyBaseAdapter<T> extends RecyclerView.Adapter{
    boolean hasLoadMore = false;
    public static final int LOAD_TYPE = 999;
    public static final int ORDER_TYPE = 1;
    public static final int TIP_TYPE = 0;
    private MyRecyclerView recyclerView;
    LoadMoreListener loadMoreListener;
    public boolean noMoreContent = false;
    public static int PAGE_COUNT = 30;

    public MyBaseAdapter(final MyRecyclerView recyclerView) {

        this.recyclerView = recyclerView;
        this.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (hasLoadMore) {
                    // load more
                    LinearLayoutManager mLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                    int totalItemCount = mLayoutManager.getItemCount();
                    if (!noMoreContent && totalItemCount > 9) {
                        int lastCompletelyVisibleItemPosition = mLayoutManager.findLastCompletelyVisibleItemPosition();

                        if (lastCompletelyVisibleItemPosition == totalItemCount - 1) {
                            loadMoreListener.onLoadMore();
                        }
                    }
                }
            }
        });
    }

    public interface LoadMoreListener {
        void onLoadMore();
    }
    public void setLoadMoreListener(LoadMoreListener loadMoreListener) {
        hasLoadMore = true;
        this.loadMoreListener = loadMoreListener;
    }


    protected List<T> data;


    public List<T> getData() {
        return this.data;
    }

    public void setData(List<T> data) {
        if (data == null) return;
        this.data = data;
        notifyDataSetChanged();
        recyclerView.scrollToPosition(0);
        noMoreContent =  data.size() < PAGE_COUNT;
    }

    public void addData(List<T> extra) {
        if (data == null) return;
        if (extra.size() == 0) {
            noMoreContent = true;
            notifyItemChanged(getItemCount() -1);
        } else {
            data.addAll(extra);
            notifyItemInserted(getItemCount() - 1);

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == LOAD_TYPE) {
            return new LoadMoreHolder(View.inflate(UIUtils.getContext(), R.layout.load_more_item,null));
        }
        return onCreateHolder(parent,viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(hasLoadMore && holder.getItemViewType() == LOAD_TYPE) {
            View loading = holder.itemView.findViewById(R.id.loading);
            View msg = holder.itemView.findViewById(R.id.msg);
            if(noMoreContent) {
                loading.setVisibility(View.INVISIBLE);
                msg.setVisibility(View.VISIBLE);
            } else {
                loading.setVisibility(View.VISIBLE);
                msg.setVisibility(View.INVISIBLE);
            }
            return;
        }
        onBindHolder(holder,position);
    }

    @Override
    public int getItemCount() {
        if(data == null) return 0;
        return hasLoadMore ? data.size() + 1 : data.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(hasLoadMore && position == getItemCount() -1) {
            return LOAD_TYPE;
        }
        return getItemType(position);
    }

    public int getItemType(int position) {
        return super.getItemViewType(position);
    }

    public abstract void onBindHolder(RecyclerView.ViewHolder holder, int position);
    public abstract RecyclerView.ViewHolder onCreateHolder(ViewGroup parent, int viewType);


    static class LoadMoreHolder extends RecyclerView.ViewHolder {

        public LoadMoreHolder(View itemView) {
            super(itemView);
        }
    }

}
