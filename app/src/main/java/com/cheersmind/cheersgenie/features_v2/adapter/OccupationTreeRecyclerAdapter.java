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
import com.cheersmind.cheersgenie.features_v2.entity.OccupationCategory;
import com.cheersmind.cheersgenie.features_v2.entity.OccupationItem;
import com.cheersmind.cheersgenie.features_v2.entity.OccupationRealm;

import java.util.List;

/**
 * 专业树recycler适配器
 */
public class OccupationTreeRecyclerAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    public final static int LAYOUT_TYPE_LEVEL0 = 0;
    public final static int LAYOUT_TYPE_LEVEL1 = 1;
    public final static int LAYOUT_TYPE_LEVEL2 = 2;

    public OccupationTreeRecyclerAdapter(@Nullable List<MultiItemEntity> data) {
        super(data);
        addItemType(LAYOUT_TYPE_LEVEL0, R.layout.recycleritem_major_level0);
        addItemType(LAYOUT_TYPE_LEVEL1, R.layout.recycleritem_major_level1);
        addItemType(LAYOUT_TYPE_LEVEL2, R.layout.recycleritem_major_level2);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {

        switch (helper.getItemViewType()) {
            case LAYOUT_TYPE_LEVEL0: {
                OccupationRealm realm = (OccupationRealm) item;
                helper.setText(R.id.tv_title, realm.getRealm());
                //伸缩按钮
                helper.setImageResource(R.id.iv_expand, realm.isExpanded() ?
                        R.drawable.icon_big_down :
                        R.drawable.icon_big_right);
                break;
            }
            case LAYOUT_TYPE_LEVEL1: {
                OccupationCategory category = (OccupationCategory) item;
                helper.setText(R.id.tv_title, category.getCategory());
                //伸缩按钮
                helper.setImageResource(R.id.iv_expand, category.isExpanded() ?
                        R.drawable.icon_big_down :
                        R.drawable.icon_big_right);
                break;
            }
            case LAYOUT_TYPE_LEVEL2: {
                OccupationItem occupationItem = (OccupationItem) item;
                helper.setText(R.id.tv_title, occupationItem.getOccupation_name());
                //是否是当前兄弟中的最后一个
                if (occupationItem.isLastInMaxLevel()) {
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

}
