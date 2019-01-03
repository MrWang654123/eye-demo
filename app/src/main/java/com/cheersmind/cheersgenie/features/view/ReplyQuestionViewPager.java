package com.cheersmind.cheersgenie.features.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Scroller;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.main.util.LogUtils;
import com.cheersmind.cheersgenie.main.util.RepetitionClickUtil;
import com.cheersmind.cheersgenie.main.util.ToastUtil;

import java.lang.reflect.Field;

/**
 * 回答问题的ReplyQuestionViewpager（存在一个最后可左滑索引）
 */
public class ReplyQuestionViewPager extends ViewPager {

    private Context context;

    //最后可显示的页索引
    private int lastCanShowPageIndex;
    //按下时的x、y
    private int mPosX;
    private int mPosY;
    //当前的x、y
    private int mCurPosX;
    private int mCurPosY;

    private long lastClickTime;

    //左滑禁止时的监听
    private LeftSlideForbidListener leftSildeForbidListener;
    //点击太快的监听
    private TooFastClickListener tooFastClickListener;

    public ReplyQuestionViewPager(Context context) {
        super(context);
        this.context = context;
        //设置滚动器
        setMyScroller();
    }

    public ReplyQuestionViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        //设置滚动器
        setMyScroller();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (getCurrentItem() == lastCanShowPageIndex) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mPosX = (int) event.getX();
                    mPosY = (int) event.getY();
//                    System.out.println("ACTION_DOWN mPosX = " + mPosX);

                    //点击太快的过滤
                    long curClickTime = System.currentTimeMillis();
                    if ((curClickTime - lastClickTime) < RepetitionClickUtil.MIN_CLICK_DELAY_TIME_LONG) {
                        LogUtils.w("答题页面ViewPager", "点击太快");
                        if (tooFastClickListener != null) {
                            tooFastClickListener.onTooFastClick();
                        }
                        return false;
                    }
                    lastClickTime = curClickTime;

                    break;
                case MotionEvent.ACTION_MOVE:
                    mCurPosX = (int) event.getX();
                    mCurPosY = (int) event.getY();
//                    System.out.println("mCurPosX = " + mCurPosX + "    mPosX = " + mPosX);
                    //左滑
                    if (isLeftSile(mPosX, mPosY, mCurPosX, mCurPosY)) {
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

                    break;
            }
        }

//        return true;
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //子类中有listview，会影响事件的捕获，所以放在dispatchTouchEvent中处理

        return super.onTouchEvent(event);
    }


    /**
     * 判断是否右滑
     * @param oldPosX 前一个位置x
     * @param oldPosY 前一个位置y
     * @param curPosX 当前位置x
     * @param curPosY 当前位置y
     * @return
     */
    private boolean isLeftSile(int oldPosX, int oldPosY, int curPosX, int curPosY) {
//         && (Math.abs(curPosX - oldPosX) > 25)
        if (curPosX < oldPosX
//                && Math.abs(curPosX - oldPosX) > DensityUtil.dip2px(context, 1)
//                && Math.abs(curPosY - oldPosY) < DensityUtil.dip2px(context, 6)
                ) {
//            LogUtils.w("Pos ReplyQuestionViewpager", "左滑");
            return true;
        }

        return false;
    }

    /**
     * 获取最后能显示的页索引
     * @return
     */
    public int getLastCanShowPageIndex() {
        return lastCanShowPageIndex;
    }

    /**
     * 设置最后能显示的页索引
     * @param lastCanShowPageIndex
     */
    public void setLastCanShowPageIndex(int lastCanShowPageIndex) {
        this.lastCanShowPageIndex = lastCanShowPageIndex;
    }

    /**
     * 左滑禁止时的监听
     */
    public interface LeftSlideForbidListener {
        //当左滑被禁止时
        void doingLeftSlideForbid();
    }

    /**
     * 设置左滑禁止被触发时的监听
     * @param leftSlideForbidListener
     */
    public void addLeftSlideForbidListener(LeftSlideForbidListener leftSlideForbidListener) {
        this.leftSildeForbidListener = leftSildeForbidListener;
    }

    /**
     * 点击太快的监听
     */
    public interface TooFastClickListener {
        //点击太快
        void onTooFastClick();
    }

    /**
     * 设置点击太快的监听
     * @param tooFastClickListener
     */
    public void setTooFastClickListener(TooFastClickListener tooFastClickListener) {
        this.tooFastClickListener = tooFastClickListener;
    }

    /**
     * 设置滚动器
     */
    private void setMyScroller() {
        try {
            Class<?> viewpager = ViewPager.class;
            Field scroller = viewpager.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            scroller.set(this, new MyScroller(getContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 滚动器
     */
    public class MyScroller extends Scroller {
        public MyScroller(Context context) {
            //减速
//            super(context, new DecelerateInterpolator());
            //先加速后减速
//            super(context, new AccelerateDecelerateInterpolator());
            //匀速
//            super(context, new LinearInterpolator());
            //加速
//            super(context, new AccelerateInterpolator());
            super(context);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            //200的时候就是setCurrentItem（dx是屏幕宽度），否则是手动滑动
            if (duration == 200) {
//                super.startScroll(startX, startY, dx, dy, 1000);
                super.startScroll(startX, startY, dx, dy, 800);
            } else {
                super.startScroll(startX, startY, dx, dy, duration);
            }
        }
    }

}
