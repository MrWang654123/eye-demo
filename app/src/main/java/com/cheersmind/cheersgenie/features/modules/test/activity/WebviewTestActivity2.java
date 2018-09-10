package com.cheersmind.cheersgenie.features.modules.test.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
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
public class WebviewTestActivity2 extends Activity {

    private FrameLayout mFrameLayout;
    private WebView mWebView;
    private InsideWebChromeClient mInsideWebChromeClient;
    //private JavascriptInterface javascriptInterface;
    private String URL = "<!DOCTYPE html>\n" +
            "<html lang=\"en\"><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
            "    \n" +
            "    <title i18n-content=\"new_tab_title\">新标签页</title>\n" +
            "    \n" +
            "<body>\n" +
            "\n" +
            "\t<embed src='http://player.youku.com/player.php/sid/XMzc3ODA5NDIzNg==/v.swf' allowFullScreen='true' quality='high' width='480' height='400' align='middle' allowScriptAccess='always' type='application/x-shockwave-flash'></embed>\n" +
            "\n" +
            "</body>\n" +
            "\n" +
            "</html>";

    private String s = "<html><head><meta charset=\"utf-8\" /><title>swf</title></head><body bgcolor=\"#000000\">"
            + "<embed src='http://player.youku.com/player.php/sid/XMzc3ODA5NDIzNg==/v.swf' allowFullScreen='true' quality='high' width='480' height='400' align='middle' allowScriptAccess='always' type='application/x-shockwave-flash'></embed></body></html>";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview_test);

        // 1
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 2
//        mFrameLayout = (FrameLayout) findViewById(R.id.mFrameLayout);
//        mWebView = (WebView) findViewById(R.id.mWebView);
        // 3
        initWebView();
//        mWebView.loadUrl(URL);
        mWebView.loadData(s, "text/html; charset=UTF-8", null);
    }

    private void initWebView() {
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        //settings.setPluginsEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        mInsideWebChromeClient = new InsideWebChromeClient();
        InsideWebViewClient mInsideWebViewClient = new InsideWebViewClient();
        //javascriptInterface = new JavascriptInterface();
        //mWebView.addJavascriptInterface(javascriptInterface, "java2js_laole918");
        mWebView.setWebChromeClient(mInsideWebChromeClient);
        mWebView.setWebViewClient(mInsideWebViewClient);
    }


    private class InsideWebChromeClient extends WebChromeClient {
        private View mCustomView;
        private CustomViewCallback mCustomViewCallback;

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            super.onShowCustomView(view, callback);
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            mCustomView = view;
            mFrameLayout.addView(mCustomView);
            mCustomViewCallback = callback;
            mWebView.setVisibility(View.GONE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        public void onHideCustomView() {
            mWebView.setVisibility(View.VISIBLE);
            if (mCustomView == null) {
                return;
            }
            mCustomView.setVisibility(View.GONE);
            mFrameLayout.removeView(mCustomView);
            mCustomViewCallback.onCustomViewHidden();
            mCustomView = null;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            super.onHideCustomView();
        }
    }

    private class InsideWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            //mWebView.loadUrl(javascript);
        }

    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        switch (config.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        mWebView.destroy();
        super.onDestroy();
    }


}
