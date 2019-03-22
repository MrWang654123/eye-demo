package com.cheersmind.cheersgenie.features_v2.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features_v2.dto.CollegeRankDto;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeEduLevel;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeRank;

import java.util.List;

/**
 * 院校排名的学历层次recycler适配器
 */
public class CollegeRankLevelRecyclerAdapter extends BaseQuickAdapter<CollegeEduLevel, BaseViewHolder> {

    private Context context;
    //排名数据
    private CollegeRankDto collegeRankDto;

    public CollegeRankLevelRecyclerAdapter(Context context, int layoutResId, @Nullable List<CollegeEduLevel> data, CollegeRankDto collegeRankDto) {
        super(layoutResId, data);
        this.context = context;
        this.collegeRankDto = collegeRankDto;
    }

    @Override
    protected void convert(BaseViewHolder helper, CollegeEduLevel item) {
        ((TextView)helper.getView(R.id.tv_title)).setTextColor(
                ContextCompat.getColor(context, R.color.color_555555));
        //选中变色
        if (collegeRankDto != null
                && collegeRankDto.getEduLevel() != null
                && !TextUtils.isEmpty(collegeRankDto.getEduLevel().getName())) {
            if (collegeRankDto.getEduLevel().getName().equals(item.getName())) {
                ((TextView) helper.getView(R.id.tv_title)).setTextColor(
                        ContextCompat.getColor(context, R.color.color_e46c3e));
            }
        }
        helper.setText(R.id.tv_title, item.getName());
    }

}
