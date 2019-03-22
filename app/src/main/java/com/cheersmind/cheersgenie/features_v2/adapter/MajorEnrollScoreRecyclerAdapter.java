package com.cheersmind.cheersgenie.features_v2.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeEnrollScoreItemEntity;
import com.cheersmind.cheersgenie.features_v2.entity.MajorEnrollScoreItemEntity;

import java.util.List;

/**
 * 专业录取分数recycler适配器
 */
public class MajorEnrollScoreRecyclerAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    public final static int LAYOUT_TYPE_HEADER = 0;
    public final static int LAYOUT_TYPE_ITEM1 = 1;
    public final static int LAYOUT_TYPE_ITEM2 = 2;

    public MajorEnrollScoreRecyclerAdapter(@Nullable List<MultiItemEntity> data) {
        super(data);
        addItemType(LAYOUT_TYPE_HEADER, R.layout.recycleritem_major_enroll_score_header);
        addItemType(LAYOUT_TYPE_ITEM1, R.layout.recycleritem_major_enroll_score_item1);
        addItemType(LAYOUT_TYPE_ITEM2, R.layout.recycleritem_major_enroll_score_item2);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {

        switch (helper.getItemViewType()) {
            case LAYOUT_TYPE_HEADER: {
                break;
            }
            case LAYOUT_TYPE_ITEM1:
            case LAYOUT_TYPE_ITEM2: {
                MajorEnrollScoreItemEntity scoreItem = (MajorEnrollScoreItemEntity) item;
                //专业名称
                helper.setText(R.id.tv_major, scoreItem.getMajor());

                //批次
                if (!TextUtils.isEmpty(scoreItem.getBkcc()) && !TextUtils.isEmpty(scoreItem.getBatch())) {
                    helper.setText(R.id.tv_batch, scoreItem.getBkcc() + scoreItem.getBatch());
                } else {
                    helper.setText(R.id.tv_batch, "--");
                }

                //最低分和排名
                if (scoreItem.getLow_score() > 0) {
                    if (scoreItem.getLow_wc() > 0) {
                        helper.setText(R.id.tv_min_score, String.valueOf(scoreItem.getLow_score()) + "/" + String.valueOf(scoreItem.getLow_wc()));
                    } else {
                        helper.setText(R.id.tv_min_score, String.valueOf(scoreItem.getLow_score()) + "/--");
                    }
                } else {
                    helper.setText(R.id.tv_min_score,"--/--");
                }

                //录取数
                if (scoreItem.getLuqu_num() > 0) {
                    helper.setText(R.id.tv_count, String.valueOf(scoreItem.getLuqu_num()));
                } else {
                    helper.setText(R.id.tv_count, "--");
                }

                break;
            }
        }

    }

}
