package com.gvbyc.ki41foo.delivery.UI.activity;

import android.content.Intent;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.gvbyc.ki41foo.delivery.R;
import com.gvbyc.ki41foo.delivery.UI.fragment.AccountFragment;
import com.gvbyc.ki41foo.delivery.UI.fragment.RestaurantFragment;
import com.gvbyc.ki41foo.delivery.UI.fragment.MyOrderFragment;
import com.gvbyc.ki41foo.delivery.UserAccountManager;
import com.gvbyc.ki41foo.delivery.model.MyAction;
import com.gvbyc.ki41foo.delivery.model.PushExtraInfo;
import com.gvbyc.ki41foo.delivery.protocal.OrderProtocol;
import com.gvbyc.ki41foo.delivery.protocal.RestaurantProtocol;
import com.gvbyc.ki41foo.delivery.utils.UIUtils;
import com.gvbyc.ki41foo.delivery.widget.MyViewPager;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.quinny898.library.persistentsearch.SearchBox;
import com.quinny898.library.persistentsearch.SearchResult;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class Home extends BaseActivity {

    Toolbar toolbar;
    private MyViewPager viewPager;
    private SearchBox search;
    private RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_home);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        setupSearch();
        setupViewPager();
        setupTabs();

    }

    private void handlerPushInfo() {
        PushExtraInfo extraInfo = EventBus.getDefault().removeStickyEvent(PushExtraInfo.class);
        if(extraInfo == null) return;
        if(extraInfo.action.equals(PushExtraInfo.ORDER_PUSH)) {
            OrderProtocol.requestMyOrders();
            EventBus.getDefault().post(new MyAction(MyAction.HOME_TO_PAGE_TWO));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        handlerPushInfo();
    }

    private void setupSearch() {
        search = (SearchBox) findViewById(R.id.searchbox);
        search.enableVoiceRecognition(this);
        search.setLogoText("點我或者麥克風搜尋餐廳..");
        search.setLogoTextColor(UIUtils.getColor(R.color.light_text));
        search.setDrawerLogo(UIUtils.getDrawable(R.drawable.logo_black));
        search.setMenuVisibility(View.INVISIBLE);
        final FrameLayout cover = (FrameLayout)  findViewById(R.id.cover);
        cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(search.getSearchOpen()) {
                    search.toggleSearch();
                }
            }
        });
        search.setMenuListener(new SearchBox.MenuListener(){

            @Override
            public void onMenuClick() {
                search.toggleSearch();
            }

        });
        search.setSearchListener(new SearchBox.SearchListener() {
            @Override
            public void onSearchOpened() {
                cover.setVisibility(View.VISIBLE);
                search.setMenuVisibility(View.VISIBLE);

            }

            @Override
            public void onSearchCleared() {

            }

            @Override
            public void onSearchClosed() {
                cover.setVisibility(View.GONE);
                search.showLoading(false);
                search.setMenuVisibility(View.INVISIBLE);
            }

            @Override
            public void onSearchTermChanged(String s) {
                if(TextUtils.isEmpty(s)) {
                    search.setLogoTextColor(UIUtils.getColor(R.color.light_text));
                } else {
                    search.setLogoTextColor(UIUtils.getColor(R.color.colorPrimary));
                }
            }

            @Override
            public void onSearch(String s) {
                search.showLoading(true);
                RestaurantProtocol.requestRestaurant(s.trim());

            }

            @Override
            public void onResultClick(SearchResult searchResult) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SearchBox.VOICE_RECOGNITION_CODE && resultCode == this.RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            search.populateEditText(matches.get(0));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setupTabs() {
        StateListDrawable icon1 = new StateListDrawable();
        StateListDrawable icon2 = new StateListDrawable();
        StateListDrawable icon3 = new StateListDrawable();
        icon1.addState(new int[]{android.R.attr.state_checked},  new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_restaurant_menu).sizeDp(20).colorRes(R.color.colorPrimary));
        icon1.addState(new int[]{},  new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_restaurant_menu).sizeDp(20).colorRes(R.color.light_text));
        icon2.addState(new int[]{android.R.attr.state_checked},  new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_assignment).sizeDp(20).colorRes(R.color.colorPrimary));
        icon2.addState(new int[]{},  new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_assignment).sizeDp(20).colorRes(R.color.light_text));
        icon3.addState(new int[]{android.R.attr.state_checked},  new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_account_circle).sizeDp(20).colorRes(R.color.colorPrimary));
        icon3.addState(new int[]{},  new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_account_circle).sizeDp(20).colorRes(R.color.light_text));

        RadioButton tab1 = (RadioButton) findViewById(R.id.tab1);
        RadioButton tab2 = (RadioButton) findViewById(R.id.tab2);
        if(!UserAccountManager.getInstance().isLogin()) tab2.setVisibility(View.GONE);
        RadioButton tab3 = (RadioButton) findViewById(R.id.tab3);
        tab1.setCompoundDrawablesWithIntrinsicBounds(null,icon1,null,null);
        tab2.setCompoundDrawablesWithIntrinsicBounds(null,icon2,null,null);
        tab3.setCompoundDrawablesWithIntrinsicBounds(null,icon3,null,null);

        radioGroup = (RadioGroup) findViewById(R.id.ll_tabs);
        radioGroup.check(R.id.tab1);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.tab1:
                        viewPager.setCurrentItem(0,false);
                        search.setVisibility(View.VISIBLE);
                        toolbar.setVisibility(View.GONE);
                        toolbar.setTitle("");
                        break;
                    case R.id.tab2:
                        viewPager.setCurrentItem(1,false);
                        search.setVisibility(View.GONE);
                        toolbar.setVisibility(View.VISIBLE);
                        toolbar.setTitle("我的訂單");
                        OrderProtocol.requestMyOrders();
                        break;
                    case R.id.tab3:
                        viewPager.setCurrentItem(2,false);
                        search.setVisibility(View.GONE);
                        toolbar.setVisibility(View.VISIBLE);
                        toolbar.setTitle("我的帳戶");
                        break;
                }
            }
        });
    }

    private void setupViewPager() {
        viewPager = (MyViewPager) findViewById(R.id.viewpager);
        viewPager.setPagingEnabled(false);
        viewPager.setOffscreenPageLimit(2);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new RestaurantFragment(), "外賣");
        if(UserAccountManager.getInstance().isLogin()) adapter.addFragment(new MyOrderFragment(), "訂單");
        adapter.addFragment(new AccountFragment(), "我的帳戶");
        viewPager.setAdapter(adapter);

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void clearSearch() {
        if(TextUtils.isEmpty(search.getSearchText())) return;
        search.populateEditText("");
    }

    public void onEventMainThread(MyAction action) {
        int event = action.getEvent();
        switch (event) {
            case MyAction.HOME_TO_PAGE_TWO:
                radioGroup.check(R.id.tab2);
                break;
            case MyAction.HOME_REFRESH:
                finish();
                startActivity(getIntent());
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
