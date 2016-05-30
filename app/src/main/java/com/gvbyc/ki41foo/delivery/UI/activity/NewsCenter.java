package com.gvbyc.ki41foo.delivery.UI.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebViewFragment;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.gvbyc.ki41foo.delivery.R;
import com.gvbyc.ki41foo.delivery.model.NewsInfo;
import com.gvbyc.ki41foo.delivery.protocal.NewsProtocol;
import com.gvbyc.ki41foo.delivery.utils.IntentUtils;
import com.gvbyc.ki41foo.delivery.utils.UIUtils;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class NewsCenter extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        setContentView(R.layout.activity_news);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("消息通知");
        NewsProtocol.requestNews();
    }

    public void onEventMainThread(final NewsInfo.NewsList data) {
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setDividerHeight(2);
        NewsAdapter newsAdapter = new NewsAdapter(data.list);
        listView.setAdapter(newsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                IntentUtils.startActivity(NewsCenter.this, CommonWebPage.class,
                        new BasicNameValuePair(CommonWebPage.URL, data.list.get(position).url),
                        new BasicNameValuePair(CommonWebPage.TITLE, "消息詳情"));
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    class NewsAdapter extends BaseAdapter {
        private ArrayList<NewsInfo> list;

        public NewsAdapter(ArrayList<NewsInfo> list) {

            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
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
            if (convertView == null) {
                convertView = View.inflate(UIUtils.getContext(), android.R.layout.simple_list_item_2, null);
            }

            TextView title = (TextView) convertView.findViewById(android.R.id.text1);
            TextView content = (TextView) convertView.findViewById(android.R.id.text2);

            NewsInfo newsInfo = list.get(position);
            title.setText(newsInfo.title);
            title.setTextColor(Color.BLACK);
            content.setText(newsInfo.content);
            return convertView;
        }
    }
}

