package com.cheersmind.cheersgenie.features.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import java.lang.reflect.Field;

/**
 * 捕获水平滑动的ViewPager
 */
public class HorizontalViewPager extends ViewPager {

    private Context context;

    //按下时的x、y
    private int mPosX;
    private int mPosY;

    //最小滑动距离
    int touchSlop;


    public HorizontalViewPager(Context context) {
        super(context);
        this.context = context;
    }

    public HorizontalViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        int mCurPosX = 0;
        int mCurPosY = 0;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPosX = (int) event.getX();
                mPosY = (int) event.getY();
//                    System.out.println("ACTION_DOWN mPosX = " + mPosX);
                break;
            case MotionEvent.ACTION_MOVE:
                mCurPosX = (int) event.getX();
                mCurPosY = (int) event.getY();
//                    System.out.println("mCurPosX = " + mCurPosX + "    mPosX = " + mPosX);
                //水平滑动
                if (isHorizontalSile(mPosX, mPosY, mCurPosX, mCurPosY)) {
//                    requestDisallowInterceptTouchEvent(true);
                        return true;
                }

                break;
            case MotionEvent.ACTION_UP:
                mCurPosX = (int) event.getX();
                mCurPosY = (int) event.getY();
//                    System.out.println("ACTION_UP mCurPosX = " + mCurPosX + "    mPosX = " + mPosX);

                //水平滑动
                if (isHorizontalSile(mPosX, mPosY, mCurPosX, mCurPosY)) {
//                    requestDisallowInterceptTouchEvent(true);
                    return true;
                }

                break;
        }

        return super.onInterceptTouchEvent(event);
    }


    /**
     * 判断是否水平滑动
     * @param oldPosX 前一个位置x
     * @param oldPosY 前一个位置y
     * @param curPosX 当前位置x
     * @param curPosY 当前位置y
     * @return
     */
    private boolean isHorizontalSile(int oldPosX, int oldPosY, int curPosX, int curPosY) {
        return Math.abs(curPosX - oldPosX) > touchSlop && Math.abs(curPosX - oldPosX) > Math.abs(curPosY - oldPosY);
    }


}
