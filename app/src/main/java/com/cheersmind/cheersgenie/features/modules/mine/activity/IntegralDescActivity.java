package com.cheersmind.cheersgenie.features.modules.mine.activity;

import com.cheersmind.cheersgenie.features.modules.base.activity.XWebViewBaseActivity;
import com.cheersmind.cheersgenie.main.constant.HttpConfig;

/**
 * 积分说明
 */
public class IntegralDescActivity extends XWebViewBaseActivity {

    @Override
    protected String settingTitle() {
        return "积分说明";
    }

    @Override
    protected void onInitView() {
        webView.loadUrl(HttpConfig.URL_INTEGRAL_DESC);
    }

}
