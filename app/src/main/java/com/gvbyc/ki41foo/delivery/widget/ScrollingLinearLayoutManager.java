package com.gvbyc.ki41foo.delivery.widget;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by goodview on 29/04/15.
 */

public class ScrollingLinearLayoutManager extends LinearLayoutManager {
    private int duration;
    private boolean cancelDes = false;

    public ScrollingLinearLayoutManager(Context context) {
        super(context, VERTICAL, false);
        this.duration = 500;
    }


    public void cancelDesCompute() {
        cancelDes = true;
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state,
                                       int position) {
        View firstVisibleChild = recyclerView.getChildAt(0);
        int itemHeight = firstVisibleChild.getHeight();
        int currentPosition = recyclerView.getChildPosition(firstVisibleChild);
        int distanceInPixels = Math.abs((currentPosition - position) * itemHeight);
        if (distanceInPixels == 0) {
            distanceInPixels = (int) Math.abs(firstVisibleChild.getY());
        }
        RecyclerView.SmoothScroller smoothScroller = new SmoothScroller(recyclerView.getContext(), distanceInPixels, duration);

        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }


    private class SmoothScroller extends LinearSmoothScroller {
        private static final int TARGET_SEEK_SCROLL_DISTANCE_PX = 10000;
        private static final int TARGET_DURATION_OFFSET = 100;
        private final float distanceInPixels;
        private final float duration;

        public SmoothScroller(Context context, int distanceInPixels, int duration) {
            super(context);
            this.distanceInPixels = distanceInPixels;

            float theta = ((float) (duration - TARGET_DURATION_OFFSET)) / TARGET_SEEK_SCROLL_DISTANCE_PX;


            if (cancelDes) {
                this.duration = duration;
                cancelDes = false;
            } else {
                this.duration = distanceInPixels < TARGET_SEEK_SCROLL_DISTANCE_PX ?
                        ((Math.abs(distanceInPixels) * theta + TARGET_DURATION_OFFSET)) : duration;
            }
        }


        @Override
        public PointF computeScrollVectorForPosition(int targetPosition) {
            return ScrollingLinearLayoutManager.this
                    .computeScrollVectorForPosition(targetPosition);
        }

        @Override
        protected int calculateTimeForScrolling(int dx) {
            float proportion = (float) dx / distanceInPixels;
            return (int) (duration * proportion);
        }


    }
}
