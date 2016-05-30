package com.gvbyc.ki41foo.delivery.model;

/**
 * Created by goodview on 23/02/16.
 */
public class MyAction {
    public final static int HOME_TO_PAGE_TWO = 100;
    public final static int HOME_REFRESH = 101;
    public final static int NO_UPDATE_AVAILABLE = 103;
    public final static int NETWORK_ERROR = 104;
    public final static int LOC_READY = 105;
    private int event;

    public MyAction(int event) {
        this.event = event;
    }

    public int getEvent() {
        return event;
    }
}
