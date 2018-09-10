package com.cheersmind.cheersgenie.features.view;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;

/**
 * 空数据布局
 */
public class XEmptyLayout extends LinearLayout implements View.OnClickListener {
    //加载成功 隐藏emptylayout了
    public static final int HIDE_LAYOUT = 4;
    //网络连接有误，或者请求失败
    public static final int NETWORK_ERROR = 1;
    //正在加载数据
    public static final int NETWORK_LOADING = 2;
    //没有数据
    public static final int NODATA = 3;
    //没有数据可以点击刷新
    public static final int NODATA_ENABLE_CLICK = 5;

    private ProgressBar animProgress;
    private boolean clickEnable = true;
    private final Context context;
    public ImageView img;
    private OnClickListener listener;
    private int mErrorState;
    private String strNoDataContent = "";
    private TextView tv;

    public XEmptyLayout(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public XEmptyLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        View view = View.inflate(context, R.layout.layout_xempty, null);
        img = (ImageView) view.findViewById(R.id.img_error_layout);
        tv = (TextView) view.findViewById(R.id.tv_error_layout);
        RelativeLayout mLayout = (RelativeLayout) view.findViewById(R.id.pageerrLayout);
        animProgress = (ProgressBar) view.findViewById(R.id.animProgress);
        setBackgroundColor(-1);
        setOnClickListener(this);
        img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickEnable) {
                    // setErrorType(NETWORK_LOADING);
                    if (listener != null)
                        listener.onClick(v);
                }
            }
        });
        addView(view);
        changeErrorLayoutBgMode(context);
    }

    public void changeErrorLayoutBgMode(Context context1) {
        // mLayout.setBackgroundColor(SkinsUtil.getColor(context1,
        // "bgcolor01"));
        // tv.setTextColor(SkinsUtil.getColor(context1, "textcolor05"));
    }

    public void dismiss() {
        mErrorState = HIDE_LAYOUT;
        setVisibility(View.GONE);
    }

    public int getErrorState() {
        return mErrorState;
    }

    public boolean isLoadError() {
        return mErrorState == NETWORK_ERROR;
    }

    public boolean isLoading() {
        return mErrorState == NETWORK_LOADING;
    }

    @Override
    public void onClick(View v) {
        if (clickEnable) {
            // setErrorType(NETWORK_LOADING);
            if (listener != null)
                listener.onClick(v);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        onSkinChanged();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void onSkinChanged() {
    }

    public void setErrorMessage(String msg) {
        tv.setText(msg);
    }

    /**
     * 新添设置背景
     * @param imgResource 图片的id
     * @param msg  图片下面的textView显示的文字
     *
     */
    public void setErrorImag(int imgResource,String msg) {
        try {
            img.setBackgroundResource(imgResource);
            tv.setText(msg);
        } catch (Exception e) {
        }
    }

    public void setErrorType(int i) {
        setVisibility(View.VISIBLE);
        switch (i) {
            //网络错误
            case NETWORK_ERROR: {
                mErrorState = NETWORK_ERROR;
                if (isConnectivity(context)) {
                    tv.setText(R.string.error_view_load_error_click_to_refresh);
                    img.setBackgroundResource(R.mipmap.pagefailed_bg);
                } else {
                    tv.setText(R.string.error_view_network_error_click_to_refresh);
                    img.setBackgroundResource(R.mipmap.page_icon_network);
                }
                img.setVisibility(View.VISIBLE);
                animProgress.setVisibility(View.GONE);
                clickEnable = true;
                break;
            }
            //正在加载
            case NETWORK_LOADING: {
                mErrorState = NETWORK_LOADING;
                animProgress.setVisibility(View.VISIBLE);
                img.setVisibility(View.GONE);
                tv.setText(R.string.error_view_loading);
                clickEnable = false;
                break;
            }
            //无数据
            case NODATA: {
                mErrorState = NODATA;
                img.setBackgroundResource(R.mipmap.page_icon_empty);
                img.setVisibility(View.VISIBLE);
                animProgress.setVisibility(View.GONE);
                setNoDataContent("未找到相关数据");
                setTvNoDataContent();
//                clickEnable = true;
                break;
            }
            //隐藏
            case HIDE_LAYOUT: {
                setVisibility(View.GONE);
                break;
            }
            //无数据，可点击重载
            case NODATA_ENABLE_CLICK: {
                mErrorState = NODATA_ENABLE_CLICK;
                img.setBackgroundResource(R.mipmap.page_icon_empty);
                img.setVisibility(View.VISIBLE);
                animProgress.setVisibility(View.GONE);
                setNoDataContent("未加载成功，可点击重试");
                setTvNoDataContent();
                clickEnable = true;
                break;
            }
            default:
                break;
        }
    }

    public void setNoDataContent(String noDataContent) {
        strNoDataContent = noDataContent;
    }

    public void setOnLayoutClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    public void setTvNoDataContent() {
        if (!strNoDataContent.equals(""))
            tv.setText(strNoDataContent);
        else
            tv.setText(R.string.error_view_no_data);
    }

    @Override
    public void setVisibility(int visibility) {
        if (visibility == View.GONE)
            mErrorState = HIDE_LAYOUT;
        super.setVisibility(visibility);
    }




    /**
     *
     * 描述：是否有网络连接.androidbase中AbWifiUtil中的方法
     * @param context
     * @return
     */
    public static boolean isConnectivity(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return ((connectivityManager.getActiveNetworkInfo() != null && connectivityManager
                .getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || telephonyManager
                .getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
    }

}
