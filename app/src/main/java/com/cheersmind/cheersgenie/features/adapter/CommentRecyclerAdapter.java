package com.cheersmind.cheersgenie.features.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cheersmind.cheersgenie.R;

import java.util.List;

/**
 * 评论recycler适配器
 */
public class CommentRecyclerAdapter  extends BaseQuickAdapter<Object, BaseViewHolder> {

    public CommentRecyclerAdapter(int layoutResId, @Nullable List<Object> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Object item) {
        int position = helper.getLayoutPosition();
//        helper.setText(R.id.tv_from, (position + 1) + "-奇思火眼");
    }
}

