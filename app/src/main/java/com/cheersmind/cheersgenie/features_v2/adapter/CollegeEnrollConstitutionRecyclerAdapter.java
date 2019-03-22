package com.cheersmind.cheersgenie.features_v2.adapter;

import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeEnrollConstitution;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeEnrollConstitutionItem;

import java.util.List;

/**
 * 大学招生章程recycler适配器
 */
public class CollegeEnrollConstitutionRecyclerAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    public CollegeEnrollConstitutionRecyclerAdapter(@Nullable List<MultiItemEntity> data) {
        super(data);
        addItemType(Dictionary.RECYCLER_VIEW_LAYOUT_TYPE_LEVEL0, R.layout.recycleritem_college_enroll_constitution_header);
        addItemType(Dictionary.RECYCLER_VIEW_LAYOUT_TYPE_LEVEL1, R.layout.recycleritem_college_enroll_constitution_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {

        switch (helper.getItemViewType()) {
            case Dictionary.RECYCLER_VIEW_LAYOUT_TYPE_LEVEL0: {
                CollegeEnrollConstitution constitution = (CollegeEnrollConstitution) item;
                if (constitution.isFirst()) {
                    helper.getView(R.id.divider).setVisibility(View.GONE);
                } else {
                    helper.getView(R.id.divider).setVisibility(View.VISIBLE);
                }
                helper.setText(R.id.tv_title, constitution.getTitle());
                break;
            }
            case Dictionary.RECYCLER_VIEW_LAYOUT_TYPE_LEVEL1: {
                CollegeEnrollConstitutionItem constitutionItem = (CollegeEnrollConstitutionItem) item;
                helper.setText(R.id.tv_title, constitutionItem.getName());
                break;
            }
        }

    }

}
