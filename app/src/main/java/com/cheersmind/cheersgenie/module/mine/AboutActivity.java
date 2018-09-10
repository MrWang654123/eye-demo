package com.cheersmind.cheersgenie.module.mine;

import com.cheersmind.cheersgenie.features.modules.base.activity.XWebViewBaseActivity;
import com.cheersmind.cheersgenie.main.constant.HttpConfig;

/**
 * 关于我们
 */
public class AboutActivity extends XWebViewBaseActivity {

    @Override
    protected String settingTitle() {
        return "关于我们";
    }

    @Override
    protected void onInitView() {
        webView.loadUrl(HttpConfig.URL_ABOUT_US);
    }

}
