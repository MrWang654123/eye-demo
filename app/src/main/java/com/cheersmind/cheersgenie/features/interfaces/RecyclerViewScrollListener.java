package com.cheersmind.cheersgenie.features.interfaces;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.cheersmind.cheersgenie.main.QSApplication;
import com.cheersmind.cheersgenie.main.util.DensityUtil;

/**
 * RecyclerView关联置顶FloatingActionButton的滚动监听
 */
public class RecyclerViewScrollListener extends RecyclerView.OnScrollListener {

    //置顶按钮
    private FloatingActionButton fabGotoTop;

    //开始控制置顶按钮显隐的位置
    private int controlPosition;

    //空视图
    private View emptyView;
    //阈值比例
    private static final float THRESHOLD_RATIO = 1.1f;
    //阈值
    private int thresholdValue = 0;

    public RecyclerViewScrollListener(Context context, final FloatingActionButton fabGotoTop) throws Exception {
        if (fabGotoTop == null) {
            throw new Exception("置顶按钮不能为空");
        }
        this.fabGotoTop = fabGotoTop;
        emptyView = new View(context);
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
    }

    @Override
    public void onScrolled(final RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        emptyView.scrollBy(0,dy);

        int recyclerViewHeight = recyclerView.getMeasuredHeight();
        int scrollY = emptyView.getScrollY();
        //确定阈值
        if (thresholdValue == 0) {
            thresholdValue = (int) (recyclerViewHeight * THRESHOLD_RATIO);
            //置顶按钮点击监听
            this.fabGotoTop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //置顶
                    recyclerView.smoothScrollToPosition(0);
                }
            });
        }
        //向上滑
        if (scrollY > thresholdValue && dy > 0) {
            if (fabGotoTop.getVisibility() != View.VISIBLE) {
//                                fabGotoTop.setVisibility(View.VISIBLE);
                fabGotoTop.show();
            }
        }

        //向下滑
        if (scrollY < thresholdValue && dy < 0) {
            if (fabGotoTop.getVisibility() == View.VISIBLE) {
//                                fabGotoTop.setVisibility(View.INVISIBLE);
                fabGotoTop.hide();
            }
        }

    }



    /*@Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
//                        int firstVisibleItemPosition = linearManager.findFirstVisibleItemPosition();
            int lastVisibleItemPosition = linearManager.findLastVisibleItemPosition();

            //开始控制显隐的位置为0，且列表有数据，才进行计算controlPosition
            if (controlPosition == 0 && recyclerView.getAdapter() != null && recyclerView.getAdapter().getItemCount() > 1) {
                View viewByPosition = linearManager.findViewByPosition(lastVisibleItemPosition);
                int itemAndDividerHeight = viewByPosition.getMeasuredHeight();
                //屏幕高度
//                int screenHeight = QSApplication.getMetrics().heightPixels;
//                int statusBarHeight = QSApplication.getStatusBarHeight();
//                int otherViewHeight = DensityUtil.dip2px(QSApplication.getContext(), 92 + 55);
//                int recyclerViewHeight = screenHeight - statusBarHeight - otherViewHeight;
                int recyclerViewHeight = recyclerView.getMeasuredHeight();
                controlPosition = recyclerViewHeight / itemAndDividerHeight + 2;
            }

            //向上滑
            if (lastVisibleItemPosition == controlPosition && dy > 0) {
                if (fabGotoTop.getVisibility() != View.VISIBLE) {
//                                fabGotoTop.setVisibility(View.VISIBLE);
                    fabGotoTop.show();
                }
            }

            //向下滑
            if (lastVisibleItemPosition == controlPosition && dy < 0) {
                if (fabGotoTop.getVisibility() == View.VISIBLE) {
//                                fabGotoTop.setVisibility(View.INVISIBLE);
                    fabGotoTop.hide();
                }
            }

        }

    }*/


}
