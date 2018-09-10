package com.cheersmind.cheersgenie.features.adapter;


import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.main.entity.ReportItemEntity;
import com.cheersmind.cheersgenie.main.entity.ReportResultEntity;
import com.cheersmind.cheersgenie.main.helper.MPChartViewHelper;

import java.util.List;

/**
 * 报告recyclerView适配器
 */
public class ReportRecyclerAdapter extends BaseQuickAdapter<List<ReportItemEntity>, BaseViewHolder> {

    private Context context;

    public ReportRecyclerAdapter(Context context, int layoutResId, @Nullable List<List<ReportItemEntity>> data) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, List<ReportItemEntity> item) {
        //标题布局
        ReportItemEntity reportItem = item.get(0);
        //话题
        if (reportItem.getTopic()) {
            helper.getView(R.id.rl_dimension_title).setVisibility(View.GONE);
            helper.getView(R.id.rl_topic_title).setVisibility(View.VISIBLE);
            //标题
            helper.setText(R.id.tv_topic_title, reportItem.getChartItemName());

        } else {
            //量表
            helper.getView(R.id.rl_topic_title).setVisibility(View.GONE);
            helper.getView(R.id.rl_dimension_title).setVisibility(View.VISIBLE);
            //标题
            helper.setText(R.id.tv_dimension_title, reportItem.getChartItemName());
        }

        //生成图表
        LinearLayout llChart = helper.getView(R.id.ll_chart);
        if (llChart.getChildCount() == 0) {
//            llChart.removeAllViews();
            MPChartViewHelper.addMpChartView(context, llChart, item);
        }

        //报告结论
        ReportResultEntity reportResult = reportItem.getReportResult();
        if (reportResult != null) {
            //结论头布局以及内容
            helper.getView(R.id.ll_result_header).setVisibility(View.VISIBLE);
            //结论的简单文本
            helper.setText(R.id.tv_result_simple, reportResult.getTitle());

            //结论脚布局以及内容
            helper.getView(R.id.ll_result_footer).setVisibility(View.VISIBLE);
            //结论的长文本描述（评价）
            helper.setText(R.id.tv_result_desc, reportResult.getContent());

        } else {
            //结论头布局
            helper.getView(R.id.ll_result_header).setVisibility(View.GONE);
            //结论脚布局
            helper.getView(R.id.ll_result_footer).setVisibility(View.GONE);
        }


    }
}
