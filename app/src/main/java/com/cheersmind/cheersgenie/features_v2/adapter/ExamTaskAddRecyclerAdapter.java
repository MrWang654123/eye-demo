package com.cheersmind.cheersgenie.features_v2.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features_v2.entity.ExamTaskEntity;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * 新增测评任务recycler适配器
 */
public class ExamTaskAddRecyclerAdapter extends BaseQuickAdapter<ExamTaskEntity, BaseViewHolder> {

    public ExamTaskAddRecyclerAdapter(Context context, int layoutResId, @Nullable List<ExamTaskEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ExamTaskEntity item) {
        //标题
        helper.setText(R.id.tv_title, item.getTask_name());

        //简介
        helper.setText(R.id.tv_desc, item.getDescription());

        //主图
        SimpleDraweeView imageView = helper.getView(R.id.iv_main);
        imageView.setImageURI(item.getTask_icon());

        //选中图标
        if (item.isSelected()) {
            helper.getView(R.id.iv_select).setVisibility(View.VISIBLE);
        } else {
            helper.getView(R.id.iv_select).setVisibility(View.GONE);
        }

    }

}
