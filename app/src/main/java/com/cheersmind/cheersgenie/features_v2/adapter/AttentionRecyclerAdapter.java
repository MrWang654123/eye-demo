package com.cheersmind.cheersgenie.features_v2.adapter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features_v2.entity.AttentionEntity;

import java.util.List;

/**
 * 关注列表recycler适配器
 */
public class AttentionRecyclerAdapter extends BaseQuickAdapter<AttentionEntity, BaseViewHolder> {

    public AttentionRecyclerAdapter(Context context, int layoutResId, @Nullable List<AttentionEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AttentionEntity item) {
        helper.setText(R.id.tv_title, item.getTag());

        helper.addOnClickListener(R.id.tv_cancel_attention);
    }

}
