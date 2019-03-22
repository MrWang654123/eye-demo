package com.cheersmind.cheersgenie.features_v2.adapter;

import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.entity.ChartItem;
import com.cheersmind.cheersgenie.features.entity.ChartItemDesc;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features_v2.entity.ExamReportRootEntity;
import com.cheersmind.cheersgenie.features_v2.entity.ReportSubItemEntity;
import com.cheersmind.cheersgenie.features_v2.entity.ReportSubTitleEntity;

import java.util.List;

import static com.cheersmind.cheersgenie.features.constant.Dictionary.CHART_BAR_H;
import static com.cheersmind.cheersgenie.features.constant.Dictionary.CHART_BAR_V;
import static com.cheersmind.cheersgenie.features.constant.Dictionary.CHART_DESC;
import static com.cheersmind.cheersgenie.features.constant.Dictionary.CHART_HEADER;
import static com.cheersmind.cheersgenie.features.constant.Dictionary.CHART_LINE;
import static com.cheersmind.cheersgenie.features.constant.Dictionary.CHART_RADAR;
import static com.cheersmind.cheersgenie.features.constant.Dictionary.CHART_SUB_ITEM;
import static com.cheersmind.cheersgenie.features.constant.Dictionary.CHART_SUB_TITLE;

/**
 * 测评报告recyclerView适配器
 */
public class ExamReportRecyclerAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    //是否是话题
    private boolean topic;

    public ExamReportRecyclerAdapter(List<MultiItemEntity> data) {
        super(data);

        addItemType(CHART_HEADER, R.layout.recycleritem_exam_report_header);
        addItemType(CHART_SUB_ITEM, R.layout.recycleritem_exam_report_sub_item);
        addItemType(CHART_SUB_TITLE, R.layout.recycleritem_exam_report_sub_title);
        addItemType(CHART_DESC, R.layout.chart_item_desc);
        addItemType(CHART_RADAR, R.layout.chart_item_radar);
        addItemType(CHART_LINE, R.layout.chart_item_line);
        addItemType(CHART_BAR_V, R.layout.chart_item_bar_v);
        addItemType(CHART_BAR_H, R.layout.chart_item_bar_h);
    }

    @Override
    protected void convert(final BaseViewHolder helper, MultiItemEntity item) {
        switch (helper.getItemViewType()) {
            //header
            case CHART_HEADER: {
                ExamReportRootEntity entity = (ExamReportRootEntity) item;
                //标题
                helper.setText(R.id.tv_title, entity.getTitle());

                //描述
                if (!TextUtils.isEmpty(entity.getDescription())) {
                    helper.getView(R.id.tv_desc).setVisibility(View.VISIBLE);
                    helper.setText(R.id.tv_desc, Html.fromHtml(entity.getDescription().trim()));
                } else {
                    helper.getView(R.id.tv_desc).setVisibility(View.GONE);
                }

                //综合结果
                helper.getView(R.id.ll_score).setVisibility(View.GONE);
                helper.getView(R.id.tv_score_desc).setVisibility(View.GONE);
                helper.getView(R.id.tv_topic_sub_items_result).setVisibility(View.GONE);
                helper.getView(R.id.tv_dimension_original_score).setVisibility(View.GONE);
                helper.getView(R.id.ll_dimension_no_score).setVisibility(View.GONE);
                //话题
                if (isTopic()) {
                    //没有排名（视为没有分数和结论）
                    if (entity.getRank() < 0.000001) {
                        List<String> sub_result = entity.getSub_result();

                        if (ArrayListUtil.isNotEmpty(sub_result)) {
                            StringBuilder result = new StringBuilder();

                            for (int i=0; i<sub_result.size(); i++) {
                                String sub = sub_result.get(i);
                                result.append(sub);
                                if (i < sub_result.size() - 1) {
                                    result.append("\n");
                                }
                            }

                            helper.getView(R.id.tv_topic_sub_items_result).setVisibility(View.VISIBLE);
                            helper.setText(R.id.tv_topic_sub_items_result, result.toString());
                        }

                    } else {//有排名则视为有结论
                        helper.getView(R.id.ll_score).setVisibility(View.VISIBLE);
                        helper.getView(R.id.tv_score_desc).setVisibility(View.VISIBLE);

                        helper.setText(R.id.tv_score, String.valueOf(entity.getScore()) + "分");
                        helper.setText(R.id.tv_result, entity.getResult());
                        String scoreDesc = "本测评中你的个人得分" + entity.getScore() + "分,高于" + entity.getRank() * 100 + "%的用户";
                        helper.setText(R.id.tv_score_desc, scoreDesc);
                    }

                } else {
                    //量表：没有T分数和结论，则显示原始分；学业兴趣和职业兴趣没有分数和排名，则显示子项结果集
                    //没有排名
                    if (entity.getRank() < 0.000001) {
                        //有分数
                        if (entity.getScore() > 0.000001) {
                            //原始分
                            helper.getView(R.id.tv_dimension_original_score).setVisibility(View.VISIBLE);
                            String scoreStr = "本测评中你的个人原始得分为" + entity.getScore() + "分";
                            helper.setText(R.id.tv_dimension_original_score, scoreStr);

                        } else {//无分数（学业兴趣、职业兴趣）
                            if (!TextUtils.isEmpty(entity.getResult())) {
                                helper.getView(R.id.ll_dimension_no_score).setVisibility(View.VISIBLE);
                                helper.setText(R.id.tv_dimension_no_score_title, "推荐结果如下：");
                                helper.setText(R.id.tv_dimension_no_score_result, entity.getResult());
                            }
                        }

                    } else {//有排名则视为有结论
                        helper.getView(R.id.ll_score).setVisibility(View.VISIBLE);
                        helper.getView(R.id.tv_score_desc).setVisibility(View.VISIBLE);

                        helper.setText(R.id.tv_score, String.valueOf(entity.getScore()) + "分");
                        helper.setText(R.id.tv_result, entity.getResult());
                        String scoreDesc = "本测评中你的个人得分" + entity.getScore() + "分,高于" + entity.getRank() * 100 + "%的用户";
                        helper.setText(R.id.tv_score_desc, scoreDesc);
                    }
                }

                //评价
                if (!TextUtils.isEmpty(entity.getAppraisal())) {
//                    String appraisal = entity.getAppraisal();
                    helper.getView(R.id.ll_appraise).setVisibility(View.VISIBLE);
                    helper.setText(R.id.tv_appraise, Html.fromHtml(entity.getAppraisal().trim()));

                } else {
                    helper.getView(R.id.ll_appraise).setVisibility(View.GONE);
                }

                break;
            }
            //图表描述
            case CHART_DESC: {
                ChartItemDesc chartItemDesc = (ChartItemDesc) item;
                helper.setText(R.id.tv_chart_desc, Html.fromHtml(chartItemDesc.getDesc()));
                helper.addOnClickListener(R.id.tv_ctrl_chart_desc);

                //伸缩
                if (chartItemDesc.isExpand()) {
                    //向上图标
//                    ((TextView)helper.getView(R.id.tv_ctrl_chart_desc)).setCompoundDrawablesWithIntrinsicBounds(
//                            null, null, ContextCompat.getDrawable(mContext, R.drawable.ic_arrow_drop_up), null);
                    helper.getView(R.id.tv_chart_desc).setVisibility(View.VISIBLE);

                } else {
                    //向下图标
//                    ((TextView)helper.getView(R.id.tv_ctrl_chart_desc)).setCompoundDrawablesWithIntrinsicBounds(
//                            null, null, ContextCompat.getDrawable(mContext, R.drawable.ic_arrow_drop_down), null);
                    helper.getView(R.id.tv_chart_desc).setVisibility(View.GONE);
                }

                //伸缩图标
                ((TextView)helper.getView(R.id.tv_ctrl_chart_desc)).setCompoundDrawablesWithIntrinsicBounds(
                        null, null, ContextCompat.getDrawable(mContext, chartItemDesc.isExpand() ?
                                R.drawable.ic_arrow_drop_up : R.drawable.ic_arrow_drop_down), null);

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
            //子标题
            case CHART_SUB_TITLE: {
                ReportSubTitleEntity subTitle = (ReportSubTitleEntity) item;
                helper.setText(R.id.tv_sub_title, subTitle.getTitle());
                break;
            }
            //子项结果
            case CHART_SUB_ITEM: {
                ReportSubItemEntity subItem = (ReportSubItemEntity) item;

                helper.getView(R.id.iv_right_arrow).setVisibility(View.GONE);
                helper.getView(R.id.iv_expand).setVisibility(View.GONE);
                helper.getView(R.id.tv_desc).setVisibility(View.GONE);

                //标题
                helper.setText(R.id.tv_title, subItem.getItem_name());

                String result = "";
                //结果
                if (subItem.getSort() > 0) {//sort大于0，视为学业兴趣、职业兴趣
                    result = "本因子得分为" + subItem.getScore() +
                            "分,在各个方向中排名第" + subItem.getSort();
                } else {
                    //result是否为空
                    if (!TextUtils.isEmpty(subItem.getResult())) {
                        //是否话题
                        if (isTopic()) {
                            //话题显示结果、排名
                            //量表显示结果、得分、排名
                            result = "本测评评价为" + subItem.getResult() +
                                    "，得分为" + subItem.getScore() + "分";

                        } else {
                            //量表显示结果、得分、排名
                            result = "本测评评价为" + subItem.getResult() +
                                    "，得分为" + subItem.getScore() + "分" +
                                    "，超过" + subItem.getRank() * 100 + "%的用户";
                        }
                    } else {
                        //得分、排名
                        result = "本测评得分为" + subItem.getScore() + "分" +
                                "，超过" + subItem.getRank() * 100 + "%的用户";
                    }
                }
                helper.setText(R.id.tv_result, result);

                //显示箭头和子项描述
                if (isTopic()) {//话题
                    helper.getView(R.id.iv_right_arrow).setVisibility(View.VISIBLE);

                } else {//量表
                    helper.getView(R.id.iv_expand).setVisibility(View.VISIBLE);

                    //伸缩
                    if (subItem.isExpand()) {
                        helper.getView(R.id.tv_desc).setVisibility(View.VISIBLE);
                    }

                    //伸缩图标
                    helper.setImageResource(R.id.iv_expand, subItem.isExpand() ? R.drawable.ic_arrow_drop_up_black_24dp : R.drawable.ic_arrow_drop_down_black_24dp);

                    //描述
                    if (!TextUtils.isEmpty(subItem.getDescription())) {
                        helper.setText(R.id.tv_desc, subItem.getDescription().trim());

                    } else {
                        helper.setText(R.id.tv_desc, "暂无描述信息");
                    }

                    helper.addOnClickListener(R.id.iv_expand);
                }

                break;
            }
        }
    }

    public boolean isTopic() {
        return topic;
    }

    public void setTopic(boolean topic) {
        this.topic = topic;
    }
}

