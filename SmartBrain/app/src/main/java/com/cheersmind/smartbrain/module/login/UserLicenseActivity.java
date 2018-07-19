package com.cheersmind.smartbrain.module.login;

import android.os.Bundle;
import android.webkit.WebView;

import com.cheersmind.smartbrain.R;
import com.cheersmind.smartbrain.main.activity.BaseActivity;
import com.cheersmind.smartbrain.main.constant.HttpConfig;

/**
 * Created by gwb on 2017/9/25.
 */

public class UserLicenseActivity extends BaseActivity {

    private WebView wvLicense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_license);
        setTitle("用户许可及服务协议");

        wvLicense = (WebView)findViewById(R.id.wv_license);
        wvLicense.loadUrl(HttpConfig.USER_LICENSE);
    }
}
