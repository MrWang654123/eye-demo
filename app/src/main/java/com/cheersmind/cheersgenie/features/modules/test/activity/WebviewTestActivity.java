package com.cheersmind.cheersgenie.features.modules.test.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.cheersmind.cheersgenie.R;

/**
 * Webview测试页面
 */
public class WebviewTestActivity extends Activity {

    private FrameLayout mFullscreenContainer;
    private FrameLayout mContentView;
    private View mCustomView = null;
    private WebView mWebView;
    private String s = "" +
            "<html lang=\"en\"><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
            "    \n" +
            "    <title i18n-content=\"new_tab_title\">新标签页</title>\n" +
            "    \n" +
            "<body>\n" +
            "\n" +
            "\n" +
            "\t<embed src=\"https://imgcache.qq.com/tencentvideo_v1/playerv3/TPout.swf?max_age=86400&v=20161117&vid=a076235kwcp&auto=0\" allowFullScreen=\"true\" quality=\"high\" width=\"480\" height=\"400\" align=\"middle\" allowScriptAccess=\"always\" type=\"application/x-shockwave-flash\"></embed>\n" +
            "\n" +
            "</body>\n" +
            "\n" +
            "</html>";



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview_test);

        initViews();
        initWebView();

        if (getPhoneAndroidSDK() >= 14) {// 4.0 需打开硬件加速
            getWindow().setFlags(0x1000000, 0x1000000);
        }

//        mWebView.loadData(s, "text/html; charset=UTF-8", null);
        mWebView.loadDataWithBaseURL(null, s, "text/html; charset=UTF-8", "UTF-8", null);
//  mWebView.loadUrl("file:///android_asset/1234.html");
    }

    private void initViews() {
        mFullscreenContainer = (FrameLayout) findViewById(R.id.fullscreen_custom_content);
        mContentView = (FrameLayout) findViewById(R.id.main_content);
        mWebView = (WebView) findViewById(R.id.webview_player);

    }

    private void initWebView() {
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
//        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setPluginState(WebSettings.PluginState.ON);
        // settings.setPluginsEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setLoadWithOverviewMode(true);

        mWebView.setWebChromeClient(new MyWebChromeClient());
        mWebView.setWebViewClient(new MyWebViewClient());
    }

    class MyWebChromeClient extends WebChromeClient {

        private CustomViewCallback mCustomViewCallback;
        private int mOriginalOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            onShowCustomView(view, mOriginalOrientation, callback);
            super.onShowCustomView(view, callback);

        }

        public void onShowCustomView(View view, int requestedOrientation,
                                     WebChromeClient.CustomViewCallback callback) {
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            if (getPhoneAndroidSDK() >= 14) {
                mFullscreenContainer.addView(view);
                mCustomView = view;
                mCustomViewCallback = callback;
                mOriginalOrientation = getRequestedOrientation();
                mContentView.setVisibility(View.INVISIBLE);
                mFullscreenContainer.setVisibility(View.VISIBLE);
                mFullscreenContainer.bringToFront();

                setRequestedOrientation(mOriginalOrientation);
            }

        }

        public void onHideCustomView() {
            mContentView.setVisibility(View.VISIBLE);
            if (mCustomView == null) {
                return;
            }
            mCustomView.setVisibility(View.GONE);
            mFullscreenContainer.removeView(mCustomView);
            mCustomView = null;
            mFullscreenContainer.setVisibility(View.GONE);
            try {
                mCustomViewCallback.onCustomViewHidden();
            } catch (Exception e) {
            }

            setRequestedOrientation(mOriginalOrientation);
        }

    }

    class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
//            return super.shouldOverrideUrlLoading(view, url);
            return true;
        }

    }

    public static int getPhoneAndroidSDK() {
        int version = 0;
        try {
            version = Integer.valueOf(android.os.Build.VERSION.SDK);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return version;

    }

    @Override
    public void onPause() {// 继承自Activity
        super.onPause();
        mWebView.onPause();
    }

    @Override
    public void onResume() {// 继承自Activity
        super.onResume();
        mWebView.onResume();
    }


}
