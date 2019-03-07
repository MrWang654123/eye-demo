package com.cheersmind.cheersgenie.features_v2.adapter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features_v2.entity.ExamTaskEntity;
import com.cheersmind.cheersgenie.features_v2.entity.MedalEntity;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * 我的勋章recycler适配器
 */
public class MineMedalRecyclerAdapter extends BaseQuickAdapter<MedalEntity, BaseViewHolder> {

    public MineMedalRecyclerAdapter(Context context, int layoutResId, @Nullable List<MedalEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MedalEntity item) {
        //标题
        helper.setText(R.id.tv_title, item.getArticleTitle().length() > 4 ? item.getArticleTitle().substring(0, 4) : item.getArticleTitle());

        //主图
        SimpleDraweeView imageView = helper.getView(R.id.iv_main);
        imageView.setImageURI(item.getArticleImg());
    }

}
