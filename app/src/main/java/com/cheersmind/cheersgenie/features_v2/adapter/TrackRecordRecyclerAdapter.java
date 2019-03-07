package com.cheersmind.cheersgenie.features_v2.adapter;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features_v2.entity.ExamTaskItemEntity;
import com.cheersmind.cheersgenie.features_v2.entity.TrackRecordEntity;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * 发展档案recycler适配器
 */
public class TrackRecordRecyclerAdapter extends BaseMultiItemQuickAdapter<TrackRecordEntity, BaseViewHolder> {

    public TrackRecordRecyclerAdapter(List<TrackRecordEntity> data) {
        super(data);
        addItemType(TrackRecordEntity.TYPE_SUMMARY, R.layout.recycleritem_track_record_common_item);
        addItemType(TrackRecordEntity.TYPE_COMMON_ITEM, R.layout.recycleritem_track_record_common_item);
        addItemType(TrackRecordEntity.TYPE_NO_DATA_RECOMMEND, R.layout.recycleritem_track_record_no_data_recommend);
    }

    @Override
    protected void convert(BaseViewHolder helper, TrackRecordEntity item) {

        switch (helper.getItemViewType()) {
            //概要
            case TrackRecordEntity.TYPE_SUMMARY: {
                break;
            }
            //普通项
            case TrackRecordEntity.TYPE_COMMON_ITEM: {
                helper.setText(R.id.tv_title, item.getArticleTitle().length() > 2 ? item.getArticleTitle().substring(0,2) + "力" : "XXXXX力");
                if (TextUtils.isEmpty(item.getSummary())) {
                    helper.setText(R.id.tv_item_content, "--");
                } else {
                    helper.setText(R.id.tv_item_content, item.getSummary());
                }
                if (TextUtils.isEmpty(item.getSummary())) {
                    helper.setText(R.id.tv_other_content, "--");
                } else {
                    helper.setText(R.id.tv_other_content, item.getSummary());
                }
                break;
            }
            //无数据推荐项
            case TrackRecordEntity.TYPE_NO_DATA_RECOMMEND: {
                helper.setText(R.id.tv_title, item.getArticleTitle().length() > 2 ? item.getArticleTitle().substring(0,2) + "力" : "XXXXX力");
                break;
            }
        }

    }

}
