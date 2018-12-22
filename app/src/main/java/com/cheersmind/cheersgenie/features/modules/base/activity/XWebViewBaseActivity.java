package com.cheersmind.cheersgenie.features.modules.base.activity;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.main.view.CustomDialog;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;

/**
 * WebView的页面基础类
 */
public class XWebViewBaseActivity extends BaseActivity {

    @BindView(R.id.webView)
    protected WebView webView;
    protected JSHook jsHook;
    private int colorStyle;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {// 定义一个Handler，用于处理下载线程与UI间通讯
            if (!Thread.currentThread().isInterrupted()) {
                switch (msg.what) {
                    case 0:
                        //pd.show();// 显示进度对话框
                        break;
                    case 1:
                        //pd.hide();// 隐藏进度对话框，不可使用dismiss()、cancel(),否则再次调用show()时，显示的对话框小圆圈不会动。
                        break;
                }
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected int setContentView() {
        return R.layout.activity_xwebview_base;
    }


    @Override
    protected String settingTitle() {
        return "";
    }

    @Override
    protected void onInitView() {
        //初始化webView
        initWebView();
    }

    @Override
    protected void onInitData() {

    }

    /**
     * 初始化webView
     */
    protected void initWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //不使用缓存：
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        //webSettings.setDefaultTextEncodingName("UTF-8");//设置字符编码
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDatabasePath(getApplicationContext().getCacheDir().getAbsolutePath());
        //打开页面时， 自适应屏幕
        webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放
        webSettings.setLoadWithOverviewMode(true);

        //方便页面支持缩放;
        //webSettings.setBuiltInZoomControls(true);
        //webSettings.setSupportZoom(true);

        jsHook = new JSHook();
        webView.addJavascriptInterface(jsHook, "jsHook"); //在JSHook类里实现javascript想调用的方法，并将其实例化传入webview, "hello"这个字串告诉javascript调用哪个实例的方法

        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                view.stopLoading();
                //view.clearView();
                //Toast.makeText(ReportActivity.this,"面面加载失败",Toast.LENGTH_SHORT).show();
                //用javascript隐藏系统定义的404页面信息
                //String data = "Page NO FOUND！";
                //view.loadUrl("javascript:document.body.innerHTML=\"" + data + "\"");
                XWebViewBaseActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(XWebViewBaseActivity.this, "页面加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        //主要处理js对话框、图标、页面标题等
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {// 载入进度改变而触发
                if (progress == 100) {
                    handler.sendEmptyMessage(1);// 如果全部载入,隐藏进度对话框
                }
                super.onProgressChanged(view, progress);
            }
        });

        //设置支持获取手势焦点
        webView.requestFocusFromTouch();
    }

    public void showDialog(String msg, int colorStyle) {//弹出第一个对话框
        CustomDialog.Builder builder = new CustomDialog.Builder(this, colorStyle);

        CustomDialog dialog = builder.setTitle("温馨提示")
                .setMessage(msg)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                    }
                }).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
//    //按返回键时， 不退出程序而是返回上一浏览页面
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//
//        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
//            webView.goBack();
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    private void showExitDialog(String msg, int colorStyle) {
        CustomDialog.Builder builder = new CustomDialog.Builder(this, colorStyle);

        CustomDialog dialog = builder.setTitle("温馨提示")
                .setMessage(msg != null ? msg : "正在测评过程中,确定要退出?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        XWebViewBaseActivity.this.finish();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                    }
                }).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public class JSHook {

        @JavascriptInterface
        public String callJavaMethod(String p) {
            System.out.println("FAQActivity callJavaMethod : " + p);
            return "TODO来自android的返回值";
        }

        @JavascriptInterface
        public void callJS(final String method) {

            webView.post(new Runnable() {
                @Override
                public void run() {
                    String info = "{'rule':'我来自android的调用'}";
                    //webView.loadUrl("javascript:quitPlayer()");

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        webView.evaluateJavascript("quitPlayer()", new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {
                                System.out.println("quitPlayer ----- value : " + value);
                                try {
                                    JSONObject obj = new JSONObject(value);
                                    int flag = obj.getInt("flag");
                                    if (flag == 0) {
                                    } else if (flag == 1) {
                                        JSONObject dataJSONObj = obj.getJSONObject("data");
                                        int finishedCount = dataJSONObj.getInt("finishedCount");
                                        int total = dataJSONObj.getInt("total");
                                        String msg = "你已完成" + finishedCount + "/" + total + "题，进度已保存，确认退出吗？";
                                        showExitDialog(msg, colorStyle);
                                    } else {
                                        finish();
                                    }
                                } catch (JSONException e) {
                                    finish();
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        finish();
                    }
                }
            });

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    public int getColorStyle() {
        return colorStyle;
    }

    public void setColorStyle(int colorStyle) {
        this.colorStyle = colorStyle;
    }

    @Override
    protected void onResume() {
        super.onResume();
//        //统计：页面埋点
//        MANService manService = MANServiceProvider.getService();
//        manService.getMANPageHitHelper().pageAppear(this);

        //友盟统计
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        //统计：页面埋点
//        MANService manService = MANServiceProvider.getService();
//        manService.getMANPageHitHelper().pageDisAppear(this);

        //友盟统计
        MobclickAgent.onPause(this);
    }
}

