package com.cheersmind.smartbrain.main.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

/**
 * Created by goodm on 2017/4/21.
 */
public class CustomHorizontalScrollView extends HorizontalScrollView {
    private LinearLayout mContainer;
    private boolean flag;
    private int itemWidth;
    // 是否在触摸状态
    private boolean inTouch = false;

    private int lastX = 0;
    private int touchEventId = -9983761;
    private boolean isRightScroll;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            View scroller = (View) msg.obj;
            if (msg.what == touchEventId) {
                if (lastX == scroller.getScrollX()) {
                    handleStop(scroller);
                } else {
                    handler.sendMessageDelayed(handler.obtainMessage(touchEventId, scroller), 5);
                    isRightScroll = lastX < scroller.getScrollX();
                    lastX = scroller.getScrollX();
                }
            }
        }
    };


    public CustomHorizontalScrollView(Context context) {
        super(context);
    }

    public CustomHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void addPage(View page, ViewGroup.LayoutParams params) {
        if(!flag) {
            mContainer = (LinearLayout) getChildAt(0);
            flag = true;
        }
        itemWidth = params.width;
        mContainer.addView(page, params);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        //System.out.println("action "+action);
        switch (action) {
            case MotionEvent.ACTION_UP:
                handler.sendMessageDelayed(handler.obtainMessage(touchEventId, this), 5);
        }
        return super.onTouchEvent(ev);
    }

    private void handleStop(Object view) {
//        int scrollX = getScrollX();
//        int page = Math.round((float) scrollX/itemWidth);
//        if (isRightScroll) {
//            //page = (int)Math.ceil((double) scrollX/itemWidth);
//        }
//        System.out.println("scrollX : "+scrollX+" , itemWidth : "+itemWidth+" , page = "+page+" , "+getWidth()+" , "+mContainer.getLayoutParams().width);
//
//        smoothScrollTo(page*itemWidth,0);
    }
}
