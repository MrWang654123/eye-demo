package com.cheersmind.cheersgenie.features.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * 能够监听显示结束的WebView
 */
public class DisplayFinishWebView extends WebView {

    private DisplayFinishListener listener;

    public DisplayFinishWebView(Context context) {
        super(context);
    }

    public DisplayFinishWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //onDraw表示显示完毕
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (listener != null) {
            listener.onDisplayFinish();
        }
    }

    /**
     * 显示结束监听
     */
    public interface DisplayFinishListener {
        void onDisplayFinish();
    }

    /**
     * 添加显示结束监听器
     * @param listener
     */
    public void setListener(DisplayFinishListener listener) {
        this.listener = listener;
    }

}

