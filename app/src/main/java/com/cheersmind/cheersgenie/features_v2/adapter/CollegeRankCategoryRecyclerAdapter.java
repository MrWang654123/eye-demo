package com.cheersmind.cheersgenie.features_v2.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeRank;

import java.util.List;

/**
 * 院校排名的院校类别recycler适配器
 */
public class CollegeRankCategoryRecyclerAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    private Context context;
    //排名数据
    private CollegeRank collegeRank;

    public CollegeRankCategoryRecyclerAdapter(Context context, int layoutResId, @Nullable List<String> data, CollegeRank collegeRank) {
        super(layoutResId, data);
        this.context = context;
        this.collegeRank = collegeRank;
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        ((TextView)helper.getView(R.id.tv_title)).setTextColor(
                ContextCompat.getColor(context, R.color.color_555555));
        //选中变色
        if (collegeRank != null
                && collegeRank.getCollegeRankCategory() != null
                && !TextUtils.isEmpty(collegeRank.getCollegeRankCategory().getCategoryName())) {
            if (collegeRank.getCollegeRankCategory().getCategoryName().equals(item)) {
                ((TextView) helper.getView(R.id.tv_title)).setTextColor(
                        ContextCompat.getColor(context, R.color.color_e46c3e));
            }
        }
        helper.setText(R.id.tv_title, item);
    }

}
