package com.cheersmind.cheersgenie.features.view.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.entity.ChartItem;
import com.cheersmind.cheersgenie.features.modules.article.activity.ArticleDetailActivity;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.utils.ChartUtil;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.entity.DimensionInfoChildEntity;
import com.cheersmind.cheersgenie.main.entity.DimensionInfoEntity;
import com.cheersmind.cheersgenie.main.entity.FactorResultEntity;
import com.cheersmind.cheersgenie.main.entity.ReportItemEntity;
import com.cheersmind.cheersgenie.main.entity.ReportResultEntity;
import com.cheersmind.cheersgenie.main.entity.ReportRootEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.DensityUtil;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;
import com.cheersmind.cheersgenie.main.util.RepetitionClickUtil;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 量表报告的对话框
 */
public class DimensionReportDialog extends Dialog {

    //标题
    @BindView(R.id.tv_dimension_title)
    TextView tvDimensionTitle;

    //比较范围模块
    @BindView(R.id.ll_compare)
    LinearLayout llCompare;
    //比较范围
    @BindView(R.id.rg_compare)
    RadioGroup rgCompare;

    //报告内容布局
    @BindView(R.id.ll_report_content)
    LinearLayout llReportContent;

    //结论的头布局
    @BindView(R.id.ll_result_header)
    LinearLayout llResultHeader;
    //结论的简单文本的前缀语
    @BindView(R.id.tv_result_simple_prefix)
    TextView tvResultSimplePrefix;
    //结论的简单文本
    @BindView(R.id.tv_result_simple)
    TextView tvResultSimple;

    //图表容器布局
    @BindView(R.id.ll_chart)
    LinearLayout llChart;

    //结论的脚布局
    @BindView(R.id.ll_result_footer)
    LinearLayout llResultFooter;
    //图标说明布局
    @BindView(R.id.ll_char_desc)
    LinearLayout llCharDesc;
    //图标说明收缩控制文本
    @BindView(R.id.tv_ctrl_chart_desc)
    TextView tvCtrlChartDesc;
    //图标说明文本
    @BindView(R.id.tv_chart_desc)
    TextView tvChartDesc;
    //结论的长文本描述布局
    @BindView(R.id.ll_result_desc)
    LinearLayout llResultDesc;
    //结论的长文本描述（评价）
    @BindView(R.id.tv_result_desc)
    TextView tvResultDesc;
    //因子结果布局
    @BindView(R.id.ll_factor_result)
    LinearLayout llFactorResult;
    //因子结果文本
    @BindView(R.id.tv_factor_result)
    TextView tvFactorResult;
    //建议
    @BindView(R.id.ll_result_suggest)
    LinearLayout llResultSuggest;

    //后台不生成报告的提示
    @BindView(R.id.tv_none)
    TextView tvNone;
    //空布局
    @BindView(R.id.emptyLayout)
    XEmptyLayout emptyLayout;

    //量表对象
    private DimensionInfoEntity dimension;

    //操作监听
    private OnOperationListener listener;

    //比较范围：默认全国
    private int compareType = Dictionary.REPORT_COMPARE_AREA_COUNTRY;
    //通信tag
    private String tag = System.currentTimeMillis() + "";


//    private String testReportStr = "{\n" +
//            "\t\"chart_datas\": [{\n" +
//            "\t\t\"topic\": false,\n" +
//            "\t\t\"report_result\": null,\n" +
//            "\t\t\"score_type\": 1,\n" +
//            "\t\t\"items\": [{\n" +
//            "\t\t\t\"child_score\": 38.11,\n" +
//            "\t\t\t\"item_name\": \"组织材料\",\n" +
//            "\t\t\t\"item_id\": \"6952bdb0-aa21-c3c8-7bfe-3848412c9120\",\n" +
//            "\t\t\t\"compare_score\": 50\n" +
//            "\t\t}, {\n" +
//            "\t\t\t\"child_score\": 38.84,\n" +
//            "\t\t\t\"item_name\": \"重复学习\",\n" +
//            "\t\t\t\"item_id\": \"88b322b0-831b-238a-3080-2fde1020061d\",\n" +
//            "\t\t\t\"compare_score\": 50\n" +
//            "\t\t}, {\n" +
//            "\t\t\t\"child_score\": 39.61,\n" +
//            "\t\t\t\"item_name\": \"精细化\",\n" +
//            "\t\t\t\"item_id\": \"a462ad75-e3e4-81aa-bc76-c48d599c9e2e\",\n" +
//            "\t\t\t\"compare_score\": 50\n" +
//            "\t\t}, {\n" +
//            "\t\t\t\"child_score\": 40.47,\n" +
//            "\t\t\t\"item_name\": \"批判性思维\",\n" +
//            "\t\t\t\"item_id\": \"aef73be2-3c7c-cd29-19f2-b7ddb611d94d\",\n" +
//            "\t\t\t\"compare_score\": 50\n" +
//            "\t\t}, {\n" +
//            "\t\t\t\"child_score\": 42.18,\n" +
//            "\t\t\t\"item_name\": \"自我调节\",\n" +
//            "\t\t\t\"item_id\": \"c75a1ada-ee38-516c-d223-432c937d3aac\",\n" +
//            "\t\t\t\"compare_score\": 50\n" +
//            "\t\t}],\n" +
//            "\t\t\"min_score\": 0,\n" +
//            "\t\t\"chart_item_id\": \"cef7f249-5c27-4813-9e53-10896c562777\",\n" +
//            "\t\t\"chart_show_item_name\": false,\n" +
//            "\t\t\"chart_type\": 1,\n" +
//            "\t\t\"max_score\": 100,\n" +
//            "\t\t\"chart_description\": \"测量学生在各门课程学习中采取各类学习策略的程度。\\r\\n\",\n" +
//            "\t\t\"chart_item_name\": \"我的学习策略\"\n" +
//            "\t}, {\n" +
//            "\t\t\"topic\": false,\n" +
//            "\t\t\"report_result\": null,\n" +
//            "\t\t\"score_type\": 1,\n" +
//            "\t\t\"items\": [{\n" +
//            "\t\t\t\"child_score\": 58.71,\n" +
//            "\t\t\t\"item_name\": \"努力指向调节\",\n" +
//            "\t\t\t\"item_id\": \"33ea9258-440a-7a9f-723f-1bb50cf0d965\",\n" +
//            "\t\t\t\"compare_score\": 50\n" +
//            "\t\t}, {\n" +
//            "\t\t\t\"child_score\": 47,\n" +
//            "\t\t\t\"item_name\": \"同伴学习\",\n" +
//            "\t\t\t\"item_id\": \"38b7da76-8495-ec8b-51d8-167e54718036\",\n" +
//            "\t\t\t\"compare_score\": 50\n" +
//            "\t\t}, {\n" +
//            "\t\t\t\"child_score\": 47.23,\n" +
//            "\t\t\t\"item_name\": \"寻求帮助\",\n" +
//            "\t\t\t\"item_id\": \"a5fcaaa4-57b0-19a7-1cf9-b8aaf2e80ed8\",\n" +
//            "\t\t\t\"compare_score\": 50\n" +
//            "\t\t}, {\n" +
//            "\t\t\t\"child_score\": 39.25,\n" +
//            "\t\t\t\"item_name\": \"时间\\/学习环境管理\",\n" +
//            "\t\t\t\"item_id\": \"ff4f9c3c-5196-ef8c-3d40-efbfe975a71d\",\n" +
//            "\t\t\t\"compare_score\": 50\n" +
//            "\t\t}],\n" +
//            "\t\t\"min_score\": 0,\n" +
//            "\t\t\"chart_item_id\": \"cef7f249-5c27-4813-9e53-10896c562777\",\n" +
//            "\t\t\"chart_show_item_name\": false,\n" +
//            "\t\t\"chart_type\": 4,\n" +
//            "\t\t\"max_score\": 100,\n" +
//            "\t\t\"chart_description\": \"测量学生在各门课程学习中采取各类学习策略的程度。\\r\\n\",\n" +
//            "\t\t\"chart_item_name\": \"我的学习策略\"\n" +
//            "\t}],\n" +
//            "\t\"report_results\": [],\n" +
//            "\t\"compare_name\": \"全国\"\n" +
//            "}";

    public DimensionReportDialog(@NonNull Context context) {
        super(context, R.style.loading_dialog);
    }

    public DimensionReportDialog(@NonNull Context context, DimensionInfoEntity dimension, OnOperationListener listener) throws Exception {
        super(context, R.style.loading_dialog);
        this.dimension = dimension;

        //量表非空
        if (this.dimension == null) {
            throw new Exception("数据有误");
        } else {
            //话题量表ID非空
            if (TextUtils.isEmpty(this.dimension.getTopicDimensionId())) {
                throw new Exception("数据有误");
            }
            //孩子量表非空
            DimensionInfoChildEntity childDimension = this.dimension.getChildDimension();
            if (childDimension == null) {
                throw new Exception("数据有误");
            } else {
                //孩子测评ID非空
                if (TextUtils.isEmpty(childDimension.getChildExamId())) {
                    throw new Exception("数据有误");
                }
            }
        }

        this.listener = listener;
        if (this.listener != null) {
            //设置对话框cancel监听
            setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    DimensionReportDialog.this.listener.onExit();
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //布局文件
        setContentView(R.layout.dialog_dimension_report);
        //ButterKnife绑定
        ButterKnife.bind(this);

        setCanceledOnTouchOutside(true);
        setCancelable(true);

        //初始化视图
        initView();

        if (dimension == null) {
            //测试用
//            testInitView();
        } else {
            //加载报告
            loadReport(compareType);
        }
    }

    /**
     * 初始化视图
     */
    private void initView() {
        //点击重载监听
        emptyLayout.setOnReloadListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                //加载报告
                loadReport(compareType);
            }
        });

        //比较范围切换监听
        rgCompare.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //取消当前对话框的所有通信
                BaseService.cancelTag(tag);

                switch (checkedId) {
                    //比较范围国家
                    case R.id.rb_compare_country: {
                        compareType = Dictionary.REPORT_COMPARE_AREA_COUNTRY;
                        break;
                    }
                    //比较范围年级
                    case R.id.rb_compare_grade: {
                        compareType = Dictionary.REPORT_COMPARE_AREA_GRADE;
                        break;
                    }
                }

                //加载报告
                loadReport(compareType);
            }
        });
    }

    /**
     * 加载报告
     * @param compareType 比较类型
     */
    private void loadReport(int compareType) {
        //通信等待提示
        emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);

        DataRequestService.getInstance().getReportV2(
                dimension.getChildDimension().getChildExamId(),
                dimension.getTopicDimensionId(),
                Dictionary.REPORT_TYPE_DIMENSION,
                compareType,
                new BaseService.ServiceCallback() {
                    @Override
                    public void onFailure(QSCustomException e) {
                        //网络异常
                        emptyLayout.setErrorType(XEmptyLayout.NETWORK_ERROR);
                    }

                    @Override
                    public void onResponse(Object obj) {
                        emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);

                        try {
                            Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                            ReportRootEntity data = InjectionWrapperUtil.injectMap(dataMap, ReportRootEntity.class);
                            if (data != null && data.getChartDatas() != null) {

                                if (data.getChartDatas().size() == 0 && data.getReportResults().size() == 0) {
                                    //标题设置
                                    tvDimensionTitle.setText(dimension.getDimensionName());
                                    //隐藏比较范围模块
                                    llCompare.setVisibility(View.GONE);
                                    //隐藏报告内容模块
                                    llReportContent.setVisibility(View.GONE);
                                    //后台不生成报告的提示
                                    tvNone.setVisibility(View.VISIBLE);
                                    return;
                                }

                                //报告结果
                                List<ReportResultEntity> reportResultEntities = data.getReportResults();
                                //报告图表数据
                                List<ReportItemEntity> dimensionReports = data.getChartDatas();

                                //拼接因子结果文本
                                formatFactorResultText(reportResultEntities);
                                //把报告结果置于报告图表对象中（和话题报告不太一样）
                                dimensionReports = settingResultToReportItem(dimensionReports, reportResultEntities);
                                //初始图表说明是否展开（没有评价则展开）
                                initDescIsExpand(dimensionReports);
                                //把比较名称置于报告图表对象中
                                settingCompareName(dimensionReports, data);

                                //刷新报告视图
                                refreshReportView(dimensionReports);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            //加载失败
                            emptyLayout.setErrorType(XEmptyLayout.NO_DATA_ENABLE_CLICK);
                        }
                    }
                }, tag, getContext());
    }

    /**
     * 刷新报告视图
     *
     * @param dimensionReports 图表对象集合
     */
    private void refreshReportView(List<ReportItemEntity> dimensionReports) throws Exception {
        if (ArrayListUtil.isNotEmpty(dimensionReports)) {
            final ReportItemEntity reportItem = dimensionReports.get(0);
            //标题
            String chartName = reportItem.getChartItemName();
            if (!TextUtils.isEmpty(chartName)) {
                tvDimensionTitle.setText(chartName);
            } else {
                tvDimensionTitle.setText(dimension.getDimensionName());
            }

            if (llChart.getChildCount() > 0 ) {
                llChart.removeAllViews();
            }
            //生成图表
//            MPChartViewHelper.addMpChartView(context, llChart, dimensionReports);
            doAddChartView(llChart, dimensionReports);

            //图表说明
            if (!TextUtils.isEmpty(reportItem.getChartDescription())) {
                llCharDesc.setVisibility(View.VISIBLE);

                tvChartDesc.setText(Html.fromHtml(reportItem.getChartDescription()));
                //刷新图表说明布局
                refreshChartDesc(reportItem.isExpandDesc());

                //图标说明是否展开点击监听
                tvCtrlChartDesc.setOnClickListener(new OnMultiClickListener() {
                    @Override
                    public void onMultiClick(View view) {
                        //取反
                        reportItem.setExpandDesc(!reportItem.isExpandDesc());
                        //刷新图表说明布局
                        refreshChartDesc(reportItem.isExpandDesc());
                    }
                });

            } else {
                llCharDesc.setVisibility(View.GONE);
            }

            //报告结果
            final ReportResultEntity reportResult = dimensionReports.get(0).getReportResult();
            if (reportResult != null) {
                //结论头布局以及内容
                llResultHeader.setVisibility(View.VISIBLE);
                //结论的简单文本的前缀语
                if (!TextUtils.isEmpty(reportResult.getHeader())) {
                    tvResultSimplePrefix.setVisibility(View.VISIBLE);
                    tvResultSimplePrefix.setText(reportResult.getHeader());
                } else {
                    tvResultSimplePrefix.setVisibility(View.GONE);
                }
                //结论的简单文本
                if (!TextUtils.isEmpty(reportResult.getTitle())) {
                    tvResultSimple.setText(Html.fromHtml(reportResult.getTitle()));
                } else {
                    tvResultSimple.setVisibility(View.GONE);
                }
                try {
                    //使用服务端的返回的颜色
                    if (!TextUtils.isEmpty(reportResult.getColor())) {
                        tvResultSimple.setTextColor(Color.parseColor(reportResult.getColor()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //结论脚布局以及内容
                llResultFooter.setVisibility(View.VISIBLE);
                //结论的长文本描述（评价）
                if (!TextUtils.isEmpty(reportResult.getContent())) {
                    tvResultDesc.setText(Html.fromHtml(reportResult.getContent()));
                } else {
                    //隐藏结论的长文本描述布局
                    llResultDesc.setVisibility(View.GONE);
                }

                //因子结果（即详细说明）
                if (!TextUtils.isEmpty(reportResult.getFactorResultText())) {
                    llFactorResult.setVisibility(View.VISIBLE);
                    tvFactorResult.setText(Html.fromHtml(reportResult.getFactorResultText()));

                } else {
                    llFactorResult.setVisibility(View.GONE);
                }

                //建议（目前隐藏）
                llResultSuggest.setVisibility(View.GONE);


            } else {
                //结论头布局
                llResultHeader.setVisibility(View.GONE);
                //结论脚布局
                llResultFooter.setVisibility(View.GONE);
            }

        } else {
            throw new Exception();
        }
    }


    /**
     * 刷新图表说明布局
     * @param isExpandDesc 是否展开
     */
    private void refreshChartDesc(boolean isExpandDesc) {
        //是否展开
        if (isExpandDesc) {
            //向上图标
            tvCtrlChartDesc.setCompoundDrawablesWithIntrinsicBounds(
                    null, null, ContextCompat.getDrawable(getContext(), R.drawable.ic_arrow_drop_up), null);
            tvChartDesc.setVisibility(View.VISIBLE);

        } else {
            //向下图标
            tvCtrlChartDesc.setCompoundDrawablesWithIntrinsicBounds(
                    null, null, ContextCompat.getDrawable(getContext(), R.drawable.ic_arrow_drop_down), null);
            tvChartDesc.setVisibility(View.GONE);
        }
    }


    /**
     * 拼接因子结果文本
     * @param reportResultEntities
     */
    private void formatFactorResultText(List<ReportResultEntity> reportResultEntities) {
        if (ArrayListUtil.isNotEmpty(reportResultEntities)) {
            for (ReportResultEntity reportResult : reportResultEntities) {
                //因子结果集合非空，且数量大于1
                if (ArrayListUtil.isNotEmpty(reportResult.getFactorResultList()) && reportResult.getFactorResultList().size() > 1) {
                    StringBuilder stringBuilder = new StringBuilder();

                    for (int i=0; i<reportResult.getFactorResultList().size(); i++) {
                        FactorResultEntity factorResult = reportResult.getFactorResultList().get(i);

                        stringBuilder.append("<strong>");
                        stringBuilder.append((i+1));
                        stringBuilder.append("、");
                        stringBuilder.append("</strong>");

                        //标题非空
                        if (!TextUtils.isEmpty(factorResult.getTitle())) {
                            stringBuilder.append("<strong>");
                            //颜色非空
                            if (!TextUtils.isEmpty(factorResult.getColor())) {
                                stringBuilder.append("<font color='");
                                stringBuilder.append(factorResult.getColor());
                                stringBuilder.append("'>");
                            } else {
                                stringBuilder.append("<font>");
                            }
                            stringBuilder.append(factorResult.getTitle());
                            stringBuilder.append("</font>");
                            stringBuilder.append("</strong>");

                            //内容非空（结果详细）
                            if (!TextUtils.isEmpty(factorResult.getContent())) {
                                stringBuilder.append("，");
                            }
                        }

                        //内容非空（结果详细）
                        if (!TextUtils.isEmpty(factorResult.getContent())) {
                            stringBuilder.append(factorResult.getContent());
                        }

                        stringBuilder.append("<br/><br/>");
                    }

                    //最终拼接结果
                    reportResult.setFactorResultText(stringBuilder.toString());

                }
            }
        }
    }


    /**
     * 初始图表说明是否展开（没有评价则展开）
     * @param reportItems
     */
    private void initDescIsExpand(List<ReportItemEntity> reportItems) {
        //非空
        if (ArrayListUtil.isNotEmpty(reportItems)) {
            for (ReportItemEntity reportItem : reportItems) {
                //评价
                if (reportItem.getReportResult() == null || TextUtils.isEmpty(reportItem.getReportResult().getContent())) {
                    reportItem.setExpandDesc(true);
                } else {
                    reportItem.setExpandDesc(false);
                }
            }
        }
    }


    /**
     * 把ReportResult设置到对应的ReportItem中（和话题报告不太一样）
     *
     * @param reportItemEntities   图表项集合
     * @param reportResultEntities 结果项集合
     * @return
     */
    private List<ReportItemEntity> settingResultToReportItem(List<ReportItemEntity> reportItemEntities, List<ReportResultEntity> reportResultEntities) {
        if (ArrayListUtil.isNotEmpty(reportItemEntities)  && ArrayListUtil.isNotEmpty(reportResultEntities)) {
            //图表项
            for (int i = 0; i < reportItemEntities.size(); i++) {
                ReportItemEntity reportItemEntity = reportItemEntities.get(i);
                //结果项
                //图表项中的reportResult为空才赋值
                if (reportItemEntity.getReportResult() == null) {
                    reportItemEntity.setReportResult(reportResultEntities.get(0));
                }
//                for (int j = 0; j < reportResultEntities.size(); j++) {
//                    ReportResultEntity reportResultEntity = reportResultEntities.get(j);
//                    try {
//                        //判等：chart_item_id和relationId
//                        if (reportItemEntity.getChartItemId().equals(reportResultEntity.getRelationId())) {
//                            //图表项中的reportResult为空才赋值
//                            if (reportItemEntity.getReportResult() == null) {
//                                reportItemEntity.setReportResult(reportResultEntity);
//                            }
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
            }
        }

        return reportItemEntities;
    }


    /**
     * 把比较名称置于报告图表对象中
     * @param reportItems
     * @param data
     */
    private void settingCompareName(List<ReportItemEntity> reportItems, ReportRootEntity data) {
        if (reportItems == null || data == null) {
            return;
        }

        for (ReportItemEntity reportItem : reportItems) {
            reportItem.setCompareName(data.getCompareName());
        }
    }


    /**
     * 生成图表视图
     * @param dimensionReports
     */
    private void doAddChartView(LinearLayout llChart, List<ReportItemEntity> dimensionReports) {
        try {
            //非空
            if (llChart == null || ArrayListUtil.isEmpty(dimensionReports)) {
                return;
            } else {
                llChart.removeAllViews();
            }

            for (ReportItemEntity reportItem : dimensionReports) {
                ChartItem chartItem = ChartUtil.reportItemToChartItem(getContext(), reportItem.getChartType(), reportItem);
                if (chartItem != null) {
                    View chartItemView = null;
                    //图表类型
                    switch (chartItem.getItemType()) {
                        case Dictionary.CHART_RADAR: {
                            chartItemView = View.inflate(getContext(), R.layout.chart_item_radar, null);
//                        chartItemView = LayoutInflater.from(getContext()).inflate(R.layout.chart_item_radar, null);
//                            LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_footer, parent, false);
                            break;
                        }
                        case Dictionary.CHART_LINE: {
                            chartItemView = View.inflate(getContext(), R.layout.chart_item_line, null);
//                        chartItemView = LayoutInflater.from(getContext()).inflate(R.layout.chart_item_line, llChart);
                            break;
                        }
                        case Dictionary.CHART_BAR_V: {
                            chartItemView = View.inflate(getContext(), R.layout.chart_item_bar_v, null);
//                        chartItemView = LayoutInflater.from(getContext()).inflate(R.layout.chart_item_bar_v, null);
                            break;
                        }
                        case Dictionary.CHART_BAR_H: {
                            chartItemView = View.inflate(getContext(), R.layout.chart_item_bar_h, null);
//                        chartItemView = LayoutInflater.from(getContext()).inflate(R.layout.chart_item_bar_h, null);
                            break;
                        }
                    }

                    if (chartItemView != null) {
                        View charView = chartItemView.findViewById(R.id.chart);
                        if (charView != null) {
                            //初始化图表视图
                            chartItem.initChart(charView);
//                            chartItem.invalidate();
//                            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) charView.getLayoutParams();
                            //添加到容器中
                            llChart.addView(chartItemView);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void setOnDismissListener(@Nullable OnDismissListener listener) {
        super.setOnDismissListener(listener);
    }

    @OnClick({R.id.iv_close, R.id.iv_close_right, R.id.viewSimulateOutSite})
    public void onViewClick(View v) {
        if (!RepetitionClickUtil.isFastClick()) {
            return;
        }

        switch (v.getId()) {
            //关闭按钮
            case R.id.iv_close:
            //右侧关闭按钮
            case R.id.iv_close_right:
            //模拟边界视图
            case R.id.viewSimulateOutSite: {
//                if (listener != null) {
//                    listener.onExit();
//                }
                dismiss();
                break;
            }
        }
    }


    @Override
    public void dismiss() {
        super.dismiss();
        //取消当前对话框的所有通信
        BaseService.cancelTag(tag);
    }

    //操作监听
    public interface OnOperationListener {

        //退出操作
        void onExit();
    }

    @Override
    public void show() {
        //动画
        getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        super.show();
        //设置宽度全屏，要设置在show的后面
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        //位于底部
        layoutParams.gravity = Gravity.BOTTOM;
        //宽度
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        //高度，（会影响状态栏的颜色）
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        //DecorView的内间距（目前测试的机型还没发现有影响）
//        getWindow().getDecorView().setPadding(0, 0, 0, 0);
        //背景灰度
        layoutParams.dimAmount = 0.4f;
        //设置属性
        getWindow().setAttributes(layoutParams);

    }

    /**
     * 测试用
     */
//    private void testInitView() {
//        emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);
//
//        try {
//            Map dataMap = JsonUtil.fromJson(testReportStr, Map.class);
//            ReportRootEntity data = InjectionWrapperUtil.injectMap(dataMap, ReportRootEntity.class);
//            if (data != null && data.getChartDatas() != null) {
//
//                if (data.getChartDatas().size() == 0 && data.getReportResults().size() == 0) {
////                                    ToastUtil.showShort(context,"感谢您的信息搜集");
//                    tvNone.setVisibility(View.VISIBLE);
//                    return;
//                }
//
//                //报告结果
//                List<ReportResultEntity> reportResultEntities = data.getReportResults();
//                //报告图表数据
//                List<ReportItemEntity> dimensionReports = data.getChartDatas();
//                //把报告结果置于报告图表对象中
//                if (ArrayListUtil.isNotEmpty(dimensionReports)) {
//                    for (int i = 0; i < dimensionReports.size(); i++) {
//                        if (ArrayListUtil.isNotEmpty(reportResultEntities)) {
//                            //图表对象中的报告结果为空，且报告结果集合中对应索引的对象不为空，则赋值
//                            if (dimensionReports.get(i).getReportResult() == null
//                                    && reportResultEntities.get(i) != null) {
//                                dimensionReports.get(i).setReportResult(reportResultEntities.get(i));
//                            }
//                        }
//                    }
//                }
//
//                //刷新报告视图
//                refreshReportView(dimensionReports);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            //加载失败
//            emptyLayout.setErrorType(XEmptyLayout.NO_DATA_ENABLE_CLICK);
//        }
//    }

    /**
     * 清空监听
     */
    public void clearListener() {
        listener = null;
        setOnDismissListener(null);
        setOnCancelListener(null);
        setOnShowListener(null);
    }

}
