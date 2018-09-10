package com.cheersmind.cheersgenie.features.view;


import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.cheersmind.cheersgenie.R;

/**
 * Recyclerview加载更多自定义视图
 */
public final class RecyclerLoadMoreView extends LoadMoreView {

    @Override
    public int getLayoutId() {
        return R.layout.view_load_more;
    }

    @Override
    protected int getLoadingViewId() {
        return R.id.load_more_loading_view;
    }

    @Override
    protected int getLoadFailViewId() {
        return R.id.load_more_load_fail_view;
    }

    @Override
    protected int getLoadEndViewId() {
        return R.id.load_more_load_end_view;
    }
}
