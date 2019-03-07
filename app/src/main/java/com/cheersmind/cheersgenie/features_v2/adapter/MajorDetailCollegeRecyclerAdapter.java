package com.cheersmind.cheersgenie.features_v2.adapter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeEntity;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * 专业的开设院校recycler适配器
 */
public class MajorDetailCollegeRecyclerAdapter extends BaseQuickAdapter<CollegeEntity, BaseViewHolder> {

    public MajorDetailCollegeRecyclerAdapter(Context context, int layoutResId, @Nullable List<CollegeEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CollegeEntity item) {
        //标题
        helper.setText(R.id.tv_title, item.getArticleTitle().length() > 9 ? item.getArticleTitle().substring(0, 10) : item.getArticleTitle());

        //简介
        helper.setText(R.id.tv_desc, item.getSummary());
//        if (!TextUtils.isEmpty(item.getSummary())) {
//            helper.getView(R.id.tv_desc).setVisibility(View.VISIBLE);
//            helper.setText(R.id.tv_desc, item.getSummary());
//        } else {
//            helper.getView(R.id.tv_desc).setVisibility(View.GONE);
//        }

        //主图
        SimpleDraweeView imageView = helper.getView(R.id.iv_main);
        imageView.setImageURI(item.getArticleImg());
    }

}
