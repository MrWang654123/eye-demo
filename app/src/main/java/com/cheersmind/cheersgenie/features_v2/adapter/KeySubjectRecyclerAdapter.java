package com.cheersmind.cheersgenie.features_v2.adapter;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features_v2.entity.KeySubject;
import com.cheersmind.cheersgenie.features_v2.entity.KeySubjectItem;

import java.util.List;

/**
 * 院校的重点学科recycler适配器
 */
public class KeySubjectRecyclerAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    public KeySubjectRecyclerAdapter(List<MultiItemEntity> data) {
        super(data);
        addItemType(Dictionary.RECYCLER_VIEW_LAYOUT_TYPE_LEVEL0, R.layout.recycleritem_key_subject_header);
        addItemType(Dictionary.RECYCLER_VIEW_LAYOUT_TYPE_LEVEL1, R.layout.recycleritem_key_subject_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {

        switch (helper.getItemViewType()) {
            case Dictionary.RECYCLER_VIEW_LAYOUT_TYPE_LEVEL0: {
                KeySubject subject = (KeySubject) item;
                helper.setText(R.id.tv_title, subject.getName());
                break;
            }
            case Dictionary.RECYCLER_VIEW_LAYOUT_TYPE_LEVEL1: {
                KeySubjectItem subjectItem = (KeySubjectItem) item;
                helper.setText(R.id.tv_title, subjectItem.getName());
                break;
            }
        }

    }

}
