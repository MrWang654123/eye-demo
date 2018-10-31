package com.cheersmind.cheersgenie.features.adapter;


import android.graphics.Color;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.entity.ChartItem;
import com.cheersmind.cheersgenie.features.entity.RadarChartItem;
import com.cheersmind.cheersgenie.main.entity.ReportItemEntity;
import com.cheersmind.cheersgenie.main.entity.ReportResultEntity;

import java.util.List;

import static com.cheersmind.cheersgenie.features.constant.Dictionary.CHART_BAR_H;
import static com.cheersmind.cheersgenie.features.constant.Dictionary.CHART_BAR_V;
import static com.cheersmind.cheersgenie.features.constant.Dictionary.CHART_FOOTER;
import static com.cheersmind.cheersgenie.features.constant.Dictionary.CHART_HEADER;
import static com.cheersmind.cheersgenie.features.constant.Dictionary.CHART_LINE;
import static com.cheersmind.cheersgenie.features.constant.Dictionary.CHART_RADAR;

/**
 * 报告recyclerView适配器
 */
public class ReportMultiRecyclerAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    //比较范围切换监听
    private RadioGroup.OnCheckedChangeListener  compareChangeListener;


    public ReportMultiRecyclerAdapter(List<MultiItemEntity> data) {
        super(data);

        addItemType(CHART_HEADER, R.layout.recycleritem_report_header);
        addItemType(CHART_FOOTER, R.layout.recycleritem_report_footer);
        addItemType(CHART_RADAR, R.layout.chart_item_radar);
        addItemType(CHART_LINE, R.layout.chart_item_line);
        addItemType(CHART_BAR_V, R.layout.chart_item_bar_v);
        addItemType(CHART_BAR_H, R.layout.chart_item_bar_h);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {
        switch (helper.getItemViewType()) {
            //header
            case CHART_HEADER: {
                //标题布局
                ReportItemEntity reportItem = (ReportItemEntity) item;
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
                        helper.getView(R.id.tv_result_simple).setVisibility(View.VISIBLE);
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

                } else {
                    //结论头布局
                    helper.getView(R.id.ll_result_header).setVisibility(View.GONE);
                }

                //比较范围切换监听
                if (this.compareChangeListener != null) {
                    ((RadioGroup)helper.getView(R.id.rg_compare)).setOnCheckedChangeListener(this.compareChangeListener);
                }
                break;
            }
            //footer
            case CHART_FOOTER: {
                //报告结论
                ReportItemEntity reportItem = (ReportItemEntity) item;
                //报告结论
                ReportResultEntity reportResult = reportItem.getReportResult();
                if (reportResult != null) {
                    //结论脚布局以及内容
                    helper.getView(R.id.ll_result_footer).setVisibility(View.VISIBLE);
                    //结论的长文本描述（评价）
                    if (!TextUtils.isEmpty(reportResult.getContent())) {
                        //显示布局
                        helper.getView(R.id.ll_result_desc).setVisibility(View.VISIBLE);
                        helper.setText(R.id.tv_result_desc, Html.fromHtml(reportResult.getContent()));
                    } else {
                        //隐藏结论的长文本描述（评价）
                        helper.getView(R.id.ll_result_desc).setVisibility(View.GONE);
                    }

                    //建议（目前隐藏）
                    helper.getView(R.id.ll_result_suggest).setVisibility(View.GONE);

                } else {
                    //结论脚布局
                    helper.getView(R.id.ll_result_footer).setVisibility(View.GONE);
                }
                break;
            }
            //网状图
            case CHART_RADAR: {
                ChartItem chartItem = (ChartItem) item;
                chartItem.initChart(helper.getView(R.id.chart));
                break;
            }
            //曲线图
            case CHART_LINE: {
                ChartItem chartItem = (ChartItem) item;
                chartItem.initChart(helper.getView(R.id.chart));
                break;
            }
            //垂直柱状图
            case CHART_BAR_V: {
                ChartItem chartItem = (ChartItem) item;
                chartItem.initChart(helper.getView(R.id.chart));
                break;
            }
            //水平条状图
            case CHART_BAR_H: {
                ChartItem chartItem = (ChartItem) item;
                chartItem.initChart(helper.getView(R.id.chart));
                break;
            }
        }
    }


    /**
     * 设置比较范围切换监听
     * @param compareChangeListener 监听器
     */
    public void setCompareChangeListener(RadioGroup.OnCheckedChangeListener compareChangeListener) {
        this.compareChangeListener = compareChangeListener;
    }


}

