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
    //显隐指定按钮的阈值
    private int thresholdValue = 0;
    //置顶时是否smooth的阈值
    private int smoothThresholdValue = 0;

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
            smoothThresholdValue = thresholdValue * 2;
            //置顶按钮点击监听
            this.fabGotoTop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //置顶
                    int scrollY = emptyView.getScrollY();
                    if (scrollY < smoothThresholdValue) {
                        recyclerView.smoothScrollToPosition(0);
                    } else {
                        recyclerView.scrollToPosition(0);
                        clearScrollYData();
                    }
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


    /**
     * 清除滚动数据
     */
    public void clearScrollYData() {
        emptyView.scrollBy(0,-emptyView.getScrollY());
        fabGotoTop.hide();
    }


}
