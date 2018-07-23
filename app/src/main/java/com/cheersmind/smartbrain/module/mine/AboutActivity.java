package com.cheersmind.smartbrain.module.mine;

import android.os.Bundle;

import com.cheersmind.smartbrain.main.activity.WebViewBaseActivity;
import com.cheersmind.smartbrain.main.constant.HttpConfig;

public class AboutActivity extends WebViewBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_about);
        setTitle("关于我们");
        webView.loadUrl(HttpConfig.URL_ABOUT_US);
    }
}
