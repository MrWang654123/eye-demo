package com.cheersmind.cheersgenie.features_v2.modules.base.activity;

import android.content.Context;
import android.content.Intent;

import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.modules.base.activity.XWebViewBaseActivity;
import com.cheersmind.cheersgenie.features_v2.modules.trackRecord.activity.TrackRecordActivity;

/**
 * 通用WebView页面
 */
public class CommonWebViewActivity extends XWebViewBaseActivity {

    /**
     * 启动通用WebView页面
     * @param context 上下文
     * @param title 标题
     * @param url 地址
     */
    public static void startCommonWebViewActivity(Context context, String title, String url) {
        Intent intent = new Intent(context, CommonWebViewActivity.class);
        intent.putExtra(DtoKey.TITLE, title);
        intent.putExtra(DtoKey.URL, url);
        context.startActivity(intent);
    }

    @Override
    protected String settingTitle() {
        return "信息";
    }

    @Override
    protected void onInitView() {

        String title = getIntent().getStringExtra(DtoKey.TITLE);
        String url = getIntent().getStringExtra(DtoKey.URL);

        //设置title
        if (tvToolbarTitle != null) {
            tvToolbarTitle.setText(title);
        }

        webView.loadUrl(url);
    }

}
