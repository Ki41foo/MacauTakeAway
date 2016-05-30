package com.gvbyc.ki41foo.delivery.model;

import java.util.ArrayList;

/**
 * Created by goodview on 29/04/16.
 */
public class NewsInfo {

    public String title;
    public String content;
    public String url;
    public String time;

    public class NewsList {
        public ArrayList<NewsInfo> list;
    }
}
