package com.cheersmind.cheersgenie.module.login;

import com.cheersmind.cheersgenie.features.modules.base.activity.XWebViewBaseActivity;
import com.cheersmind.cheersgenie.main.constant.HttpConfig;


/**
 * 用户许可协议
 */
public class UserLicenseActivity extends XWebViewBaseActivity {

    @Override
    protected String settingTitle() {
        return "用户许可及服务协议";
    }

    @Override
    protected void onInitView() {
        webView.loadUrl(HttpConfig.USER_LICENSE);
    }

}
