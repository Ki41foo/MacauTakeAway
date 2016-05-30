package com.gvbyc.ki41foo.delivery.UI.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gvbyc.ki41foo.delivery.R;
import com.gvbyc.ki41foo.delivery.utils.LogUtils;
import com.gvbyc.ki41foo.delivery.utils.UIUtils;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;



public class CommonWebPage extends BaseActivity {

    public final static String URL = "url";
    public final static String TITLE = "title";
    public final static String HIDE_HEADER = "hide_header";

    protected WebView webView;
    protected ImageView refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commom_web_page);

        findViewById(R.id.exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonWebPage.super.onBackPressed();
            }
        });


        String url = getIntent().getStringExtra(URL);
        String title = getIntent().getStringExtra(TITLE);
        String hide_header = getIntent().getStringExtra(HIDE_HEADER);
        if(!TextUtils.isEmpty(hide_header)) {
            findViewById(R.id.header).setVisibility(View.GONE);
        }

        webView = (WebView) findViewById(R.id.webview);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.pb);

        final TextView tv_title = (TextView) findViewById(R.id.title);
        tv_title.setText(title == null ? "" : title);

        refresh = (ImageView) findViewById(R.id.refresh);
        refresh.setImageDrawable(new IconicsDrawable(UIUtils.getContext(), GoogleMaterial.Icon.gmd_refresh).color(Color.WHITE).sizeDp(16));
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.reload();
            }
        });



        // Enable Javascript
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        //webView.setInitialScale(120);
        webSettings.setSupportZoom(true);
//        webSettings.setDisplayZoomControls(false);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                view.clearCache(true);

            }

        });

        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                AlertDialog dialog = new AlertDialog.Builder(CommonWebPage.this, R.style.AppCompatAlertDialogStyle)
                        .setMessage(message)
                        .setTitle("溫馨提示")
                        .setPositiveButton("知道了",null).create();
                //dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                return true;
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress < 100 && progressBar.getVisibility() == View.GONE) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                progressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                }
            }

        });

        if (url.toLowerCase().startsWith("http://") || url.toLowerCase().startsWith("https://")) {
            webView.loadUrl(url);
        } else {
            webView.loadUrl("http://" + url);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            webView.onPause();
            super.onBackPressed();
        }

    }


}
