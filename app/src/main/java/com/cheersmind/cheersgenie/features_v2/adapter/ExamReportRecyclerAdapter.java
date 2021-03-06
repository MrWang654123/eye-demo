package com.cheersmind.cheersgenie.features_v2.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.entity.ChartItem;
import com.cheersmind.cheersgenie.features.entity.ChartItemDesc;
import com.cheersmind.cheersgenie.features.entity.ReportRecommend;
import com.cheersmind.cheersgenie.features.entity.ReportRecommendItem;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.WarpLinearLayout;
import com.cheersmind.cheersgenie.features_v2.entity.ExamMbtiData;
import com.cheersmind.cheersgenie.features_v2.entity.ExamReportRootEntity;
import com.cheersmind.cheersgenie.features_v2.entity.OccupationCategory;
import com.cheersmind.cheersgenie.features_v2.entity.ReportEvaluate;
import com.cheersmind.cheersgenie.features_v2.entity.ReportEvaluateItem;
import com.cheersmind.cheersgenie.features_v2.entity.ReportMbtiData;
import com.cheersmind.cheersgenie.features_v2.entity.ReportRecommendActType;
import com.cheersmind.cheersgenie.features_v2.entity.ReportSubItemEntity;
import com.cheersmind.cheersgenie.features_v2.entity.ReportSubTitleEntity;
import com.cheersmind.cheersgenie.features_v2.view.CircleScaleView;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import static com.cheersmind.cheersgenie.features.constant.Dictionary.CHART_BAR_H;
import static com.cheersmind.cheersgenie.features.constant.Dictionary.CHART_BAR_V;
import static com.cheersmind.cheersgenie.features.constant.Dictionary.CHART_DESC;
import static com.cheersmind.cheersgenie.features.constant.Dictionary.CHART_HEADER;
import static com.cheersmind.cheersgenie.features.constant.Dictionary.CHART_LEFT_RIGHT_RATIO;
import static com.cheersmind.cheersgenie.features.constant.Dictionary.CHART_LINE;
import static com.cheersmind.cheersgenie.features.constant.Dictionary.CHART_RADAR;
import static com.cheersmind.cheersgenie.features.constant.Dictionary.CHART_RECOMMEND_ACT_TYPE;
import static com.cheersmind.cheersgenie.features.constant.Dictionary.CHART_RECOMMEND_CONTENT_DIVIDER;
import static com.cheersmind.cheersgenie.features.constant.Dictionary.CHART_RECOMMEND_CONTENT_ITEM;
import static com.cheersmind.cheersgenie.features.constant.Dictionary.CHART_RECOMMEND_CONTENT_TITLE;
import static com.cheersmind.cheersgenie.features.constant.Dictionary.CHART_REPORT_EVALUATE;
import static com.cheersmind.cheersgenie.features.constant.Dictionary.CHART_SUB_ITEM;
import static com.cheersmind.cheersgenie.features.constant.Dictionary.CHART_SUB_TITLE;

/**
 * 测评报告recyclerView适配器
 */
public class ExamReportRecyclerAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    //是否是话题
    private boolean topic;
    //上下文
    private Context context;
    //默认强调色
    private int accentColor;
    //可控制是否四舍五入
    private NumberFormat nf;

    public ExamReportRecyclerAdapter(Context context, List<MultiItemEntity> data) {
        super(data);
        this.context = context;
        accentColor = context.getResources().getColor(R.color.colorAccent);

        addItemType(CHART_HEADER, R.layout.recycleritem_exam_report_header);
        addItemType(CHART_SUB_ITEM, R.layout.recycleritem_exam_report_sub_item);
        addItemType(CHART_SUB_TITLE, R.layout.recycleritem_exam_report_sub_title);
        addItemType(CHART_RECOMMEND_ACT_TYPE, R.layout.recycleritem_exam_report_recommend_act_type);
        addItemType(CHART_RECOMMEND_CONTENT_TITLE, R.layout.recycleritem_exam_report_recommend_content_title);
        addItemType(CHART_RECOMMEND_CONTENT_ITEM, R.layout.recycleritem_exam_report_recommend_content_item);
        addItemType(CHART_RECOMMEND_CONTENT_DIVIDER, R.layout.recycleritem_exam_report_recommend_content_divider);
        addItemType(CHART_REPORT_EVALUATE, R.layout.recycleritem_exam_report_evaluate);
        addItemType(CHART_DESC, R.layout.chart_item_desc);
        addItemType(CHART_RADAR, R.layout.chart_item_radar);
        addItemType(CHART_LINE, R.layout.chart_item_line);
        addItemType(CHART_BAR_V, R.layout.chart_item_bar_v);
        addItemType(CHART_BAR_H, R.layout.chart_item_bar_h);
        addItemType(CHART_LEFT_RIGHT_RATIO, R.layout.chart_item_ratio);

        nf = NumberFormat.getNumberInstance();
        // 保留一位小数
        nf.setMaximumFractionDigits(1);
        // 如果不需要四舍五入，可以使用RoundingMode.DOWN
        nf.setRoundingMode(RoundingMode.DOWN);
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
                helper.getView(R.id.ll_mbti_result).setVisibility(View.GONE);
                //图表类型为左右堆叠图，是MBTI
                if (entity.getChart_type() == CHART_LEFT_RIGHT_RATIO) {
                    helper.getView(R.id.ll_mbti_result).setVisibility(View.VISIBLE);
                    helper.setText(R.id.tv_mbti_result, entity.getResult());
                    helper.setText(R.id.tv_mbti_sub_result, entity.getVice_result());

                } else {
                    //话题
                    if (isTopic()) {
                        //有排名（T分数才有排名）
                        if (entity.getRank() != null) {
                            hasRankAndResult(helper, entity);

                        } else {//没有排名（视为没有分数和结论）
                            List<String> sub_result = entity.getSub_result();

                            if (ArrayListUtil.isNotEmpty(sub_result)) {
                                StringBuilder result = new StringBuilder();

                                for (int i = 0; i < sub_result.size(); i++) {
                                    String sub = sub_result.get(i);
                                    //非空
                                    if (!TextUtils.isEmpty(sub) && !"null".equalsIgnoreCase(sub)) {
                                        result.append(sub);
                                    }
                                    //非最后一个
                                    if (i < sub_result.size() - 1) {
                                        result.append("\n");
                                    }
                                }

                                helper.getView(R.id.tv_topic_sub_items_result).setVisibility(View.VISIBLE);
                                helper.setText(R.id.tv_topic_sub_items_result, result.length() > 0 ? result.toString() : "暂无综合结果");

                            } else {//无综合结果
                                //隐藏综合结果块
                                helper.getView(R.id.ll_result).setVisibility(View.GONE);
                            }
                        }

                    } else {
                        //量表：如果没有T分数和结论，则显示原始分；学业兴趣和职业兴趣没有分数和排名，则显示result
                        //有排名（T分数才有排名）
                        if (entity.getRank() != null) {
                            hasRankAndResult(helper, entity);

                        } else {
                            //有分数
                            if (entity.getScore() != null) {
                                //原始分
                                helper.getView(R.id.tv_dimension_original_score).setVisibility(View.VISIBLE);
                                String scoreStr = "本测评中你的得分为<b><font color='" + accentColor + "'>" +
                                        nf.format(entity.getScore()) + "分</font></b>";
                                helper.setText(R.id.tv_dimension_original_score, Html.fromHtml(scoreStr));

                            } else {//无分数（学业兴趣、职业兴趣）
                                if (!TextUtils.isEmpty(entity.getResult())) {
                                    helper.getView(R.id.ll_dimension_no_score).setVisibility(View.VISIBLE);
                                    helper.setText(R.id.tv_dimension_no_score_title, "推荐结果如下：");
                                    helper.setText(R.id.tv_dimension_no_score_result, entity.getResult());
                                }
                            }
                        }
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
            //水平比例图
            case CHART_LEFT_RIGHT_RATIO: {
                LinearLayout llContainer = helper.getView(R.id.ll_container);
                //孩子视图未0才处理
                if (llContainer.getChildCount() == 0) {

                    ReportMbtiData reportMbtiData = (ReportMbtiData) item;
                    List<ExamMbtiData> items = reportMbtiData.getItems();

                    for (ExamMbtiData subItem : items) {
                        View view = LayoutInflater.from(context).inflate(R.layout.layout_ratio_chart_item, null);
                        View bgLeft = view.findViewById(R.id.bgLeft);
                        View bgRight = view.findViewById(R.id.bgRight);
                        TextView tvRatioLeft = view.findViewById(R.id.tv_ratio_left);
                        TextView tvRatioRight = view.findViewById(R.id.tv_ratio_right);
                        TextView tvResultLeft = view.findViewById(R.id.tv_result_left);
                        TextView tvResultRight = view.findViewById(R.id.tv_result_right);
                        //比例值
                        tvRatioLeft.setText(String.format(Locale.CHINA,"%d%%", (int)(subItem.getLeft() * 100)));
                        tvRatioRight.setText(String.format(Locale.CHINA,"%d%%", (int)(subItem.getRight() * 100)));

                        //背景视图宽度比例
                        ConstraintLayout.LayoutParams bgLeftParams = (ConstraintLayout.LayoutParams) bgLeft.getLayoutParams();
                        bgLeftParams.horizontalWeight = (float) (subItem.getLeft() * 100);
                        ConstraintLayout.LayoutParams bgRightParams = (ConstraintLayout.LayoutParams) bgRight.getLayoutParams();
                        bgRightParams.horizontalWeight = (float) (subItem.getRight() * 100);

                        if (subItem.getLeft() >= subItem.getRight()) {
                            //背景
                            bgLeft.setBackgroundResource(R.drawable.ratio_chart_dark_left);
                            bgRight.setBackgroundResource(R.drawable.ratio_chart_light_right);
                            //结果文本
                            tvResultLeft.setText(subItem.getResult());
                            tvResultRight.setVisibility(View.GONE);

                        } else {
                            //背景
                            bgLeft.setBackgroundResource(R.drawable.ratio_chart_light_left);
                            bgRight.setBackgroundResource(R.drawable.ratio_chart_dark_right);
                            //结果文本
                            tvResultRight.setText(subItem.getResult());
                            tvResultLeft.setVisibility(View.GONE);
                        }

                        llContainer.addView(view);
                    }
                }
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

                //综合结果
                String result = "";
                if (isTopic()) {//话题
                    //result是否为空
                    if (!TextUtils.isEmpty(subItem.getResult())) {
                        //得分是否为空
                        if (subItem.getScore() != null && subItem.getScore() > 0.000001) {
                            //结果、得分
                            result = "本测评评价为<b><font color='" + accentColor + "'>" +
                                    subItem.getResult() + "</font></b>，得分为<b><font color='" + accentColor + "'>" +
                                    nf.format(subItem.getScore()) + "分</font></b>";
                        } else {
                            //结果
                            result = "本测评评价为<b><font color='" + accentColor + "'>" + subItem.getResult() + "</font></b>";
                        }

                    } else {
                        //得分
                        result = "本测评得分为<b><font color='" + accentColor + "'>" + nf.format(subItem.getScore()) + "分</font></b>";
                    }

                } else {//量表
                    if (subItem.getSort() != null) {//sort不为null，视为学业兴趣、职业兴趣
                        //得分、排序
                        result = "本因子得分为<b><font color='" + accentColor + "'>" + nf.format(subItem.getScore()) +
                                "分</font></b>,在各个方向中排名第<b><font color='" + accentColor + "'>" + subItem.getSort() + "</font></b>";
                    } else {
                        //有排名（T分数才有排名）
                        if (subItem.getRank() != null) {
                            //result是否为空
                            if (!TextUtils.isEmpty(subItem.getResult())) {
                                //结果、得分、排名
                                result = "本测评评价为<b><font color='" + accentColor + "'>" + subItem.getResult() +
                                        "</font></b>，得分为<b><font color='" + accentColor + "'>" + nf.format(subItem.getScore()) + "分</font></b>" +
                                        "，超过<b><font color='" + accentColor + "'>" + String.format(Locale.CHINA,"%.1f", subItem.getRank() * 100) + "%</font></b>的用户";
                            } else {
                                //得分、排名
                                result = "本测评得分为<b><font color='" + accentColor + "'>" + nf.format(subItem.getScore()) + "分</font></b>" +
                                        "，超过<b><font color='" + accentColor + "'>" + String.format(Locale.CHINA,"%.1f", subItem.getRank() * 100) + "%</font></b>的用户";
                            }
                        } else {//无排名则显示原始分
                            //得分
                            result = "本次测评中你的得分为<b><font color='" + accentColor + "'>" + nf.format(subItem.getScore()) + "分</font></b>";
                        }
                    }
                }
                helper.setText(R.id.tv_result, !TextUtils.isEmpty(result) ? Html.fromHtml(result) : "");

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
                        helper.setText(R.id.tv_desc, Html.fromHtml(subItem.getDescription().trim()));

                    } else {
                        helper.setText(R.id.tv_desc, "暂无描述信息");
                    }

//                    helper.addOnClickListener(R.id.iv_expand);
                }

                break;
            }
            //推荐的ACT分类
            case CHART_RECOMMEND_ACT_TYPE: {
                WarpLinearLayout warpLinearLayout = helper.getView(R.id.warpLinearLayout);
                if (warpLinearLayout.getChildCount() == 0) {
                    ReportRecommendActType type = (ReportRecommendActType) item;
                    List<OccupationCategory> items = type.getItems();
                    //ACT分类
                    for (int i=0; i<items.size(); i++) {
                        final OccupationCategory category = items.get(i);
                        TextView tv = (TextView) LayoutInflater.from(context).inflate(R.layout.record_result_item_clickable, null);
                        tv.setText(category.getArea_name());
                        tv.setTag(i);
                        tv.setOnClickListener(new OnMultiClickListener() {
                            @Override
                            public void onMultiClick(View view) {
                                if (actCategoryClickListener != null) {
                                    actCategoryClickListener.onClick(category);
                                }
                            }
                        });
                        warpLinearLayout.addView(tv);
                    }
                }

                break;
            }
            //推荐内容标题
            case CHART_RECOMMEND_CONTENT_TITLE: {
                ReportRecommend entity = (ReportRecommend) item;
                helper.setText(R.id.tv_title, entity.getNode_name());
                break;
            }
            //推荐内容项
            case CHART_RECOMMEND_CONTENT_ITEM: {
                ReportRecommendItem entity = (ReportRecommendItem) item;
                helper.setText(R.id.tv_title, entity.getElement_name());

                String tag = "";
                if ("audio".equals(entity.getElement_type())) {
                    tag = "音频";

                } else if ("video".equals(entity.getElement_type())) {
                    tag = "视频";

                } else if ("article".equals(entity.getElement_type())) {
                    tag = "文章";

                } else if ("task".equals(entity.getElement_type())) {
                    tag = "任务";

                } else if ("dimension".equals(entity.getElement_type())) {
                    tag = "测评";
                }

                helper.setText(R.id.tv_tag, tag);

                break;
            }
            //报告的用户评价
            case CHART_REPORT_EVALUATE: {
                final ReportEvaluate evaluate = (ReportEvaluate) item;
                //标题
                helper.setText(R.id.tv_title, ((ReportEvaluate) item).getTitle());
                //选项
                WarpLinearLayout warpLinearLayout = helper.getView(R.id.warpLinearLayout);
                if (warpLinearLayout.getChildCount() == 0) {
                    List<ReportEvaluateItem> items = evaluate.getItems();
                    //评价选项
                    for (int i=0; i<items.size(); i++) {
                        final ReportEvaluateItem evaluateItem = items.get(i);
                        final TextView tv = (TextView) LayoutInflater.from(context).inflate(R.layout.report_evaluate_item, null);
                        //选中状态
                        if (evaluateItem.isEvaluate()) {
                            tv.setBackgroundResource(R.drawable.shape_corner_aaaaaa_3dp);
                        } else {
                            tv.setBackgroundResource(R.drawable.shape_corner_accent_3dp);
                        }
                        tv.setText(evaluateItem.getContent());
                        tv.setOnClickListener(new OnMultiClickListener() {
                            @Override
                            public void onMultiClick(View view) {
                                if (onEvaluateItemClickListener != null) {
                                    int layoutPosition = helper.getLayoutPosition();
                                    onEvaluateItemClickListener.onClick(ExamReportRecyclerAdapter.this, layoutPosition, evaluate, evaluateItem);
                                }
                            }
                        });
                        warpLinearLayout.addView(tv);
                    }
                }

                break;
            }
        }
    }

    /**
     * 有排名和结论的情况处理
     * @param helper BaseViewHolder
     * @param entity ExamReportRootEntity
     */
    private void hasRankAndResult(BaseViewHolder helper, ExamReportRootEntity entity) {
        helper.getView(R.id.ll_score).setVisibility(View.VISIBLE);
        helper.getView(R.id.tv_score_desc).setVisibility(View.VISIBLE);
        double score = entity.getScore() != null ? new BigDecimal(entity.getScore()).setScale(1, BigDecimal.ROUND_DOWN).doubleValue() : 0;
        //分数
        String scoreStr = String.valueOf(score);
        helper.setText(R.id.tv_score, scoreStr);
        //分数圆环
        ((CircleScaleView)helper.getView(R.id.csv_score)).setCirclePercent((float)(100 - score), (float) score);
        //结果
        helper.setText(R.id.tv_result, entity.getResult());
        //分数描述
        String scoreDesc = "本测评中你的得分<b><font color='" + accentColor + "'>" + scoreStr +
                "分</font></b>,高于<b><font color='" + accentColor + "'>" +
                String.format(Locale.CHINA,"%.1f", entity.getRank() * 100) + "%</font></b>的用户";
        helper.setText(R.id.tv_score_desc, Html.fromHtml(scoreDesc));
    }

    public boolean isTopic() {
        return topic;
    }

    public void setTopic(boolean topic) {
        this.topic = topic;
    }

    private OnActCategoryClickListener actCategoryClickListener;

    /**
     * ACT职业分类点击监听
     */
    public interface OnActCategoryClickListener {
        void onClick(OccupationCategory category);
    }

    public void setActCategoryClickListener(OnActCategoryClickListener actCategoryClickListener) {
        this.actCategoryClickListener = actCategoryClickListener;
    }

    private OnEvaluateItemClickListener onEvaluateItemClickListener;

    /**
     * 设置评价选项点击监听
     * @param onEvaluateItemClickListener 监听
     */
    public void setOnEvaluateItemClickListener(OnEvaluateItemClickListener onEvaluateItemClickListener) {
        this.onEvaluateItemClickListener = onEvaluateItemClickListener;
    }

    /**
     * 评价项点击监听
     */
    public interface OnEvaluateItemClickListener {
        /**
         * 点击
         * @param adapter 适配器
         * @param layoutPosition 布局位置
         * @param evaluate 评价对象
         */
        void onClick(BaseQuickAdapter adapter, int layoutPosition, ReportEvaluate evaluate, ReportEvaluateItem evaluateItem);
    }

}

