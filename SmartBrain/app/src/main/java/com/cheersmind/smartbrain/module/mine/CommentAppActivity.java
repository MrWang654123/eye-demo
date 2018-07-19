package com.cheersmind.smartbrain.module.mine;

import android.os.Bundle;

import com.cheersmind.smartbrain.main.activity.WebViewBaseActivity;
import com.cheersmind.smartbrain.main.constant.HttpConfig;

/**
 * Created by Administrator on 2017/12/6.
 */

public class CommentAppActivity extends WebViewBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("评价一下");
        webView.loadUrl(HttpConfig.URL_APP_MARKET);
    }
}
