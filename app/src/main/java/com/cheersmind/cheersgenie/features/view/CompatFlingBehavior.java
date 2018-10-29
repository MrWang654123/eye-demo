package com.cheersmind.cheersgenie.features.view;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.cheersmind.cheersgenie.BuildConfig;

/**
 * AppBarLayout兼容子view的Fling状态的Behavior
 */
public class CompatFlingBehavior extends AppBarLayout.Behavior {

    private static final int TYPE_FLING = 1;

//    private boolean isFlinging;
//    private boolean shouldBlockNestedScroll;

    //是否往上划
    private boolean isUpSlide = false;

    //按下时的x、y
    private int mPosX;
    private int mPosY;

    //最小滑动距离
    int touchSlop;


    public CompatFlingBehavior() {
    }

    public CompatFlingBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, AppBarLayout child, MotionEvent event) {
        if (BuildConfig.DEBUG) {
            Log.i("CompatFlingBehavior", "onInterceptTouchEvent:" + child.getTotalScrollRange());
        }
//        shouldBlockNestedScroll = false;
//        if (isFlinging) {
//            shouldBlockNestedScroll = true;
//        }

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
                //是否往上划
                isUpSlide = isUpSile(mPosX, mPosY, mCurPosX, mCurPosY);
                if (BuildConfig.DEBUG) {
                    System.out.println(isUpSlide ? "isUpSlide: 向上划" : "isUpSlide: No");
                }

                //水平滑动
                if (isHorizontalSile(mPosX, mPosY, mCurPosX, mCurPosY)) {
                    return false;
                }

                break;
            case MotionEvent.ACTION_UP:
                mCurPosX = (int) event.getX();
                mCurPosY = (int) event.getY();
//                    System.out.println("ACTION_UP mCurPosX = " + mCurPosX + "    mPosX = " + mPosX);

                //ACTION_UP也拦截的话会对子列表的子项的点击事件造成影响
                //左滑
//                    if (isLeftSile(mPosX, mPosY, mCurPosX, mCurPosY)) {
//                        //左滑禁止时的监听
//                        if (leftSildeForbidListener != null) {
//                            leftSildeForbidListener.doingLeftSildeForbid();
//                        }
//
//                        return false;
//                    }
                //水平滑动
                if (isHorizontalSile(mPosX, mPosY, mCurPosX, mCurPosY)) {
                    return false;
                }

                break;
        }

        return super.onInterceptTouchEvent(parent, child, event);
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, int dx, int dy, int[] consumed, int type) {
        //注意看ViewCompat.TYPE_TOUCH
        if (BuildConfig.DEBUG) {
            Log.i("CompatFlingBehavior", "onNestedPreScroll:" + child.getTotalScrollRange() + " ,dx:" + dx + " ,dy:" + dy + " ,type:" + type);
        }

        //返回1时，表示当前target处于非touch的滑动，
        //该bug的引起是因为appbar在滑动时，CoordinatorLayout内的实现NestedScrollingChild2接口的滑动子类还未结束其自身的fling
        //所以这里监听子类的非touch时的滑动，然后block掉滑动事件传递给AppBarLayout
//        if (type == TYPE_FLING) {
//            isFlinging = true;
//        }
//        if (!shouldBlockNestedScroll) {
//            super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
//        }
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, int dxConsumed, int dyConsumed, int
            dxUnconsumed, int dyUnconsumed, int type) {
        if (BuildConfig.DEBUG) {
            Log.i("CompatFlingBehavior", "onNestedScroll: target:" + target.getClass() + " ," + child.getTotalScrollRange() + " ,dxConsumed:"
                    + dxConsumed + " ,dyConsumed:" + dyConsumed + " " + ",type:" + type);
        }
//        if (!shouldBlockNestedScroll) {
//            super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
//        }

        if (target instanceof RecyclerView) {
            //抛掷状态，且消费y消费为0
            if (type == TYPE_FLING && dyConsumed == 0) {
                //往上划
                if (isUpSlide) {
                    ((RecyclerView) target).stopScroll();
                }
            }
        }

        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout abl, View target, int type) {
        super.onStopNestedScroll(coordinatorLayout, abl, target, type);
//        isFlinging = false;
//        shouldBlockNestedScroll = false;
        isUpSlide = false;
    }


    /**
     * 判断是否往上划
     * @param oldPosX 前一个位置x
     * @param oldPosY 前一个位置y
     * @param curPosX 当前位置x
     * @param curPosY 当前位置y
     * @return
     */
    private boolean isUpSile(int oldPosX, int oldPosY, int curPosX, int curPosY) {
        return curPosY < oldPosY && oldPosY - curPosY > touchSlop;
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


