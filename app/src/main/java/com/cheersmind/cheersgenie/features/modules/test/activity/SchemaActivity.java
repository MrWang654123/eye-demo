package com.cheersmind.cheersgenie.features.modules.test.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.main.util.ToastUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 通过schema唤起应用指定页面的测试
 */
public class SchemaActivity extends Activity {

    @BindView(R.id.btn_start_third_page)
    Button btnStartThirdPage;

    String uriStr = "snssdk141://detail?flags=65536&item_id=6545380538201408007&aggr_type=2&gd_ext_json=%7B%27log_pb%27%3A+%7B%27impr_id%27%3A+%2720180825105120010008062071652947%27%7D%7D&gd_label=click_pgc&groupid=6545380538201408007";
//    String uriStr = "snssdk141://detail";
    String urlpage = "<html xmlns=\"http://www.w3.org/1999/xhtml\" >\n" +
        " <head>\n" +
        "     <title>通过URL Scheme启动Android应用</title>\n" +
        " </head>\n" +
        " <body>\n" +
        "     <form>\n" +
        "       <a href=\"snssdk141://detail?flags=65536&item_id=6545380538201408007&aggr_type=2&gd_ext_json=%7B%27log_pb%27%3A+%7B%27impr_id%27%3A+%2720180825105120010008062071652947%27%7D%7D&gd_label=click_pgc&groupid=6545380538201408007\">启动</a>\n" +
        "     </form>\n" +
        " </body>\n" +
        "</html>";

    @BindView(R.id.webview_third_page)
    WebView webviewThirdPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schema);
        ButterKnife.bind(this);

        initWebView();
    }

    @OnClick(R.id.btn_start_third_page)
    public void onViewClicked() {
//        Intent intent = new Intent();
//        intent.setData(Uri.parse(uriStr));
//        startActivity(intent);


//        Uri uri=Uri.parse(uriStr);
//        Intent intent=new Intent(Intent.ACTION_VIEW,uri);
//        startActivity(intent);

//        Intent intent = new Intent();
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setAction(Intent.ACTION_VIEW);
//        intent.addCategory(Intent.CATEGORY_DEFAULT);// 应用过滤条件
//        intent.setData(Uri.parse(uriStr));
//        startActivity(intent);// 现在只能跳转到APP2的入口界面

//        if (isUriValid(uriStr)) {
//            ToastUtil.showShort(getApplicationContext(), "启动第三方页面");
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriStr));
//            startActivity(intent);
//        } else {
//            ToastUtil.showShort(getApplicationContext(), "无效的uri");
//        }

        if (isUriValid(uriStr)) {
            ToastUtil.showShort(getApplicationContext(), "启动第三方页面");

        } else {
            ToastUtil.showShort(getApplicationContext(), "无效的uri");
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriStr));
        startActivity(intent);

//        WebView.loadUrl("artist://first/enter");
//        webviewThirdPage.loadUrl(uriStr);

//        mWebView.loadDataWithBaseURL(null, s, "text/html; charset=UTF-8", "UTF-8", null);
//        webviewThirdPage.loadDataWithBaseURL(null, urlpage, "text/html; charset=UTF-8", "UTF-8", null);
    }


    private void initWebView() {
        WebSettings settings = webviewThirdPage.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
//        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setPluginState(WebSettings.PluginState.ON);
        // settings.setPluginsEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setLoadWithOverviewMode(true);

        webviewThirdPage.setWebChromeClient(new MyWebChromeClient());
        webviewThirdPage.setWebViewClient(new MyWebViewClient());
    }

    class MyWebChromeClient extends WebChromeClient {

    }

    class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            view.loadUrl(url);
//            return super.shouldOverrideUrlLoading(view, url);
//            return true;
            return false;
        }

    }

    /**
     * uri是否有效
     * @param uri
     * @return
     */
    public boolean isUriValid(String uri) {
        PackageManager packageManager = getPackageManager();
        Uri parse = Uri.parse(uri);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
        boolean isValid = !activities.isEmpty();
//        if (isValid) {
//            startActivity(intent);
//        }

        return isValid;
    }

}
