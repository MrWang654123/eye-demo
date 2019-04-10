package com.cheersmind.cheersgenie.features_v2.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features_v2.dto.CollegeRankDto;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeEntity;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * 院校排名recycler适配器
 */
public class CollegeRankRecyclerAdapter extends BaseQuickAdapter<CollegeEntity, BaseViewHolder> {

    //最大的质量标签数量
    private static final int MAX_QUALITY_TAG_COUNT = 3;

    //院校排名dto
    CollegeRankDto dto;

    public CollegeRankRecyclerAdapter(Context context, int layoutResId, @Nullable List<CollegeEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CollegeEntity item) {
        //中文名称
        helper.setText(R.id.tv_title, item.getCn_name());

        //Logo
        SimpleDraweeView imageView = helper.getView(R.id.iv_main);
        imageView.setImageURI(item.getLogo_url());

        //标签
        if (item.getBasicInfo().getInstitute_quality() == null
                || item.getBasicInfo().getInstitute_quality().size() == 0) {
            helper.getView(R.id.tv_tag0).setVisibility(View.GONE);
            helper.getView(R.id.tv_tag1).setVisibility(View.GONE);
            helper.getView(R.id.tv_tag2).setVisibility(View.GONE);
        } else {
            //设置标签
            int tagLength = item.getBasicInfo().getInstitute_quality().size();
            for (int i=0; i<tagLength; i++) {
                String tag = item.getBasicInfo().getInstitute_quality().get(i);

                if (i == 0) {
                    helper.getView(R.id.tv_tag0).setVisibility(View.VISIBLE);
                    helper.setText(R.id.tv_tag0, tag);
                } else if (i == 1) {
                    helper.getView(R.id.tv_tag1).setVisibility(View.VISIBLE);
                    helper.setText(R.id.tv_tag1, tag);
                } else if (i == 2) {
                    helper.getView(R.id.tv_tag2).setVisibility(View.VISIBLE);
                    helper.setText(R.id.tv_tag2, tag);
                }
            }

            //剩余未被占用的TextView数量
            int remainSize = MAX_QUALITY_TAG_COUNT - tagLength;
            if (remainSize > 0) {
                for (int i=remainSize-1; i>=0; i--) {
                    if (i == 0) {
                        helper.getView(R.id.tv_tag0).setVisibility(View.GONE);
                    } else if (i == 1) {
                        helper.getView(R.id.tv_tag1).setVisibility(View.GONE);
                    } else if (i == 2) {
                        helper.getView(R.id.tv_tag2).setVisibility(View.GONE);
                    }
                }
            }
        }

        //院校类别
        helper.setText(R.id.tv_category, item.getBasicInfo().getInstitute_type());

        //公立私立
        if ("public".equals(item.getBasicInfo().getPublic_or_private())) {
            helper.setText(R.id.tv_public, "公立");
        } else {
            helper.setText(R.id.tv_public, "私立");
        }

        //排名
        if (dto != null && dto.getRankItem() != null
                && !TextUtils.isEmpty(dto.getRankItem().getCode()) && item.getMapRank() != null) {
            Double rankVal = (Double) item.getMapRank().get(dto.getRankItem().getCode());

            if (rankVal != null && rankVal.intValue() > 0) {
                helper.getView(R.id.tv_rank).setVisibility(View.VISIBLE);
                helper.setText(R.id.tv_rank, String.valueOf(rankVal.intValue()));
            } else {
                helper.getView(R.id.tv_rank).setVisibility(View.GONE);
            }

        } else {
            helper.getView(R.id.tv_rank).setVisibility(View.GONE);
        }

    }

    public void setDto(CollegeRankDto dto) {
        this.dto = dto;
    }

}
