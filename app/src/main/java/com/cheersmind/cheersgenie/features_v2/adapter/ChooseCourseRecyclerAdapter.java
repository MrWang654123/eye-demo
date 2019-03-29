package com.cheersmind.cheersgenie.features_v2.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features_v2.entity.ChooseCourseEntity;

import java.util.List;

/**
 * 确认选课recycler适配器
 */
public class ChooseCourseRecyclerAdapter extends BaseQuickAdapter<ChooseCourseEntity, BaseViewHolder> {

    public ChooseCourseRecyclerAdapter(Context context, int layoutResId, @Nullable List<ChooseCourseEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ChooseCourseEntity item) {
        //标题
        helper.setText(R.id.tv_title, item.getSubject_name());

        //主图
//        SimpleDraweeView imageView = helper.getView(R.id.iv_main);
//        imageView.setImageURI(item.getArticleImg());

        //选中图标
        if (item.isSelected()) {
            helper.getView(R.id.iv_select).setVisibility(View.VISIBLE);
        } else {
            helper.getView(R.id.iv_select).setVisibility(View.GONE);
        }

    }

}
