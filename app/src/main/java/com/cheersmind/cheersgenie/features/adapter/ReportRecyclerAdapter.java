package com.cheersmind.cheersgenie.features.adapter;


import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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
            //结论的简单文本的前缀语
            if (!TextUtils.isEmpty(reportResult.getHeader())) {
                helper.getView(R.id.tv_result_simple_prefix).setVisibility(View.VISIBLE);
                helper.setText(R.id.tv_result_simple_prefix, reportResult.getHeader());
            } else {
                helper.getView(R.id.tv_result_simple_prefix).setVisibility(View.GONE);
            }
            //结论的简单文本
            if (!TextUtils.isEmpty(reportResult.getTitle())) {
                helper.setText(R.id.tv_result_simple, Html.fromHtml(reportResult.getTitle()));
            } else {
                helper.getView(R.id.tv_result_simple).setVisibility(View.GONE);
            }
            try {
                //使用服务端的返回的颜色
                if (!TextUtils.isEmpty(reportResult.getColor())) {
                    ((TextView) helper.getView(R.id.tv_result_simple)).setTextColor(Color.parseColor(reportResult.getColor()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //结论脚布局以及内容
            helper.getView(R.id.ll_result_footer).setVisibility(View.VISIBLE);
            //结论的长文本描述（评价）
            if (!TextUtils.isEmpty(reportResult.getContent())) {
                helper.setText(R.id.tv_result_desc, Html.fromHtml(reportResult.getContent()));
            } else {
                //隐藏结论的长文本描述（评价）
                helper.getView(R.id.ll_result_desc).setVisibility(View.GONE);
            }

            //建议（目前隐藏）
            helper.getView(R.id.ll_result_suggest).setVisibility(View.GONE);

        } else {
            //结论头布局
            helper.getView(R.id.ll_result_header).setVisibility(View.GONE);
            //结论脚布局
            helper.getView(R.id.ll_result_footer).setVisibility(View.GONE);
        }


    }
}
