package com.cheersmind.cheersgenie.features_v2.adapter;

import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features_v2.entity.MajorCategory;
import com.cheersmind.cheersgenie.features_v2.entity.MajorItem;
import com.cheersmind.cheersgenie.features_v2.entity.MajorSubject;

import java.util.List;

/**
 * 专业树recycler适配器
 */
public class MajorTreeRecyclerAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    public final static int LAYOUT_TYPE_LEVEL0 = 0;
    public final static int LAYOUT_TYPE_LEVEL1 = 1;
    public final static int LAYOUT_TYPE_LEVEL2 = 2;

    public MajorTreeRecyclerAdapter(@Nullable List<MultiItemEntity> data) {
        super(data);
        addItemType(LAYOUT_TYPE_LEVEL0, R.layout.recycleritem_major_level0);
        addItemType(LAYOUT_TYPE_LEVEL1, R.layout.recycleritem_major_level1);
        addItemType(LAYOUT_TYPE_LEVEL2, R.layout.recycleritem_major_level2);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {

        switch (helper.getItemViewType()) {
            case LAYOUT_TYPE_LEVEL0: {
                MajorSubject majorSubject = (MajorSubject) item;
                helper.setText(R.id.tv_title, majorSubject.getSubject());
                //伸缩按钮
                helper.setImageResource(R.id.iv_expand, majorSubject.isExpanded() ?
                        R.drawable.icon_big_down :
                        R.drawable.icon_big_right);
                break;
            }
            case LAYOUT_TYPE_LEVEL1: {
                MajorCategory majorCategory = (MajorCategory) item;
                helper.setText(R.id.tv_title, majorCategory.getCategory());
                //伸缩按钮
                helper.setImageResource(R.id.iv_expand, majorCategory.isExpanded() ?
                        R.drawable.icon_big_down :
                        R.drawable.icon_big_right);
                break;
            }
            case LAYOUT_TYPE_LEVEL2: {
                MajorItem majorItem = (MajorItem) item;
                helper.setText(R.id.tv_title, majorItem.getMajor_name());
                //是否是当前兄弟中的最后一个
                if (majorItem.isLastInMaxLevel()) {
                    helper.getView(R.id.divider_small).setVisibility(View.GONE);
                    helper.getView(R.id.divider_big).setVisibility(View.VISIBLE);
                } else {
                    helper.getView(R.id.divider_small).setVisibility(View.VISIBLE);
                    helper.getView(R.id.divider_big).setVisibility(View.GONE);
                }
                break;
            }
        }

    }


//    @Override
//    protected void convert(BaseViewHolder helper, ExamTaskEntity item) {
//        //标题
//        helper.setText(R.id.tv_title, item.getArticleTitle().length() > 9 ? item.getArticleTitle().substring(0, 10) : item.getArticleTitle());
//
//        //简介
//        helper.setText(R.id.tv_desc, item.getSummary());
////        if (!TextUtils.isEmpty(item.getSummary())) {
////            helper.getView(R.id.tv_desc).setVisibility(View.VISIBLE);
////            helper.setText(R.id.tv_desc, item.getSummary());
////        } else {
////            helper.getView(R.id.tv_desc).setVisibility(View.GONE);
////        }
//
//        //主图
//        SimpleDraweeView imageView = helper.getView(R.id.iv_main);
//        imageView.setImageURI(item.getArticleImg());
//    }

}
