package com.cheersmind.cheersgenie.features.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.entity.DimensionInfoChildEntity;
import com.cheersmind.cheersgenie.main.entity.DimensionInfoEntity;
import com.cheersmind.cheersgenie.main.entity.ReportItemEntity;
import com.cheersmind.cheersgenie.main.entity.ReportResultEntity;
import com.cheersmind.cheersgenie.main.entity.ReportRootEntity;
import com.cheersmind.cheersgenie.main.helper.MPChartViewHelper;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
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
    //结论的长文本描述布局
    @BindView(R.id.ll_result_desc)
    LinearLayout llResultDesc;
    //结论的长文本描述（评价）
    @BindView(R.id.tv_result_desc)
    TextView tvResultDesc;
    //建议
    @BindView(R.id.ll_result_suggest)
    LinearLayout llResultSuggest;

    //后台不生成报告的提示
    @BindView(R.id.tv_none)
    TextView tvNone;
    //空布局
    @BindView(R.id.emptyLayout)
    XEmptyLayout emptyLayout;

    //上下文
    private Context context;

    //量表对象
    private DimensionInfoEntity dimension;

    //操作监听
    private OnOperationListener listener;


    private String testReportStr = "{\n" +
            "\t\"chart_datas\": [{\n" +
            "\t\t\"topic\": false,\n" +
            "\t\t\"report_result\": null,\n" +
            "\t\t\"score_type\": 1,\n" +
            "\t\t\"items\": [{\n" +
            "\t\t\t\"child_score\": 38.11,\n" +
            "\t\t\t\"item_name\": \"组织材料\",\n" +
            "\t\t\t\"item_id\": \"6952bdb0-aa21-c3c8-7bfe-3848412c9120\",\n" +
            "\t\t\t\"compare_score\": 50\n" +
            "\t\t}, {\n" +
            "\t\t\t\"child_score\": 38.84,\n" +
            "\t\t\t\"item_name\": \"重复学习\",\n" +
            "\t\t\t\"item_id\": \"88b322b0-831b-238a-3080-2fde1020061d\",\n" +
            "\t\t\t\"compare_score\": 50\n" +
            "\t\t}, {\n" +
            "\t\t\t\"child_score\": 39.61,\n" +
            "\t\t\t\"item_name\": \"精细化\",\n" +
            "\t\t\t\"item_id\": \"a462ad75-e3e4-81aa-bc76-c48d599c9e2e\",\n" +
            "\t\t\t\"compare_score\": 50\n" +
            "\t\t}, {\n" +
            "\t\t\t\"child_score\": 40.47,\n" +
            "\t\t\t\"item_name\": \"批判性思维\",\n" +
            "\t\t\t\"item_id\": \"aef73be2-3c7c-cd29-19f2-b7ddb611d94d\",\n" +
            "\t\t\t\"compare_score\": 50\n" +
            "\t\t}, {\n" +
            "\t\t\t\"child_score\": 42.18,\n" +
            "\t\t\t\"item_name\": \"自我调节\",\n" +
            "\t\t\t\"item_id\": \"c75a1ada-ee38-516c-d223-432c937d3aac\",\n" +
            "\t\t\t\"compare_score\": 50\n" +
            "\t\t}],\n" +
            "\t\t\"min_score\": 0,\n" +
            "\t\t\"chart_item_id\": \"cef7f249-5c27-4813-9e53-10896c562777\",\n" +
            "\t\t\"chart_show_item_name\": false,\n" +
            "\t\t\"chart_type\": 1,\n" +
            "\t\t\"max_score\": 100,\n" +
            "\t\t\"chart_description\": \"测量学生在各门课程学习中采取各类学习策略的程度。\\r\\n\",\n" +
            "\t\t\"chart_item_name\": \"我的学习策略\"\n" +
            "\t}, {\n" +
            "\t\t\"topic\": false,\n" +
            "\t\t\"report_result\": null,\n" +
            "\t\t\"score_type\": 1,\n" +
            "\t\t\"items\": [{\n" +
            "\t\t\t\"child_score\": 58.71,\n" +
            "\t\t\t\"item_name\": \"努力指向调节\",\n" +
            "\t\t\t\"item_id\": \"33ea9258-440a-7a9f-723f-1bb50cf0d965\",\n" +
            "\t\t\t\"compare_score\": 50\n" +
            "\t\t}, {\n" +
            "\t\t\t\"child_score\": 47,\n" +
            "\t\t\t\"item_name\": \"同伴学习\",\n" +
            "\t\t\t\"item_id\": \"38b7da76-8495-ec8b-51d8-167e54718036\",\n" +
            "\t\t\t\"compare_score\": 50\n" +
            "\t\t}, {\n" +
            "\t\t\t\"child_score\": 47.23,\n" +
            "\t\t\t\"item_name\": \"寻求帮助\",\n" +
            "\t\t\t\"item_id\": \"a5fcaaa4-57b0-19a7-1cf9-b8aaf2e80ed8\",\n" +
            "\t\t\t\"compare_score\": 50\n" +
            "\t\t}, {\n" +
            "\t\t\t\"child_score\": 39.25,\n" +
            "\t\t\t\"item_name\": \"时间\\/学习环境管理\",\n" +
            "\t\t\t\"item_id\": \"ff4f9c3c-5196-ef8c-3d40-efbfe975a71d\",\n" +
            "\t\t\t\"compare_score\": 50\n" +
            "\t\t}],\n" +
            "\t\t\"min_score\": 0,\n" +
            "\t\t\"chart_item_id\": \"cef7f249-5c27-4813-9e53-10896c562777\",\n" +
            "\t\t\"chart_show_item_name\": false,\n" +
            "\t\t\"chart_type\": 4,\n" +
            "\t\t\"max_score\": 100,\n" +
            "\t\t\"chart_description\": \"测量学生在各门课程学习中采取各类学习策略的程度。\\r\\n\",\n" +
            "\t\t\"chart_item_name\": \"我的学习策略\"\n" +
            "\t}],\n" +
            "\t\"report_results\": [],\n" +
            "\t\"compare_name\": \"全国\"\n" +
            "}";

    public DimensionReportDialog(@NonNull Context context) {
        super(context, R.style.loading_dialog);
        this.context = context;
    }

    public DimensionReportDialog(@NonNull Context context, DimensionInfoEntity dimension, OnOperationListener listener) throws Exception {
        super(context, R.style.loading_dialog);
        this.context = context;
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
            testInitView();
        } else {
            //加载报告
            loadReport();
        }
    }

    /**
     * 初始化视图
     */
    private void initView() {
        //空布局
        emptyLayout = findViewById(R.id.emptyLayout);
        //点击重载监听
        emptyLayout.setOnReloadListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                //加载分类
                loadReport();
            }
        });

        //图标容器布局
        llChart = findViewById(R.id.ll_chart);
        //后台不生成报告的提示
        tvNone = findViewById(R.id.tv_none);
    }

    /**
     * 加载报告
     */
    private void loadReport() {
        //通信等待提示
        emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);

        DataRequestService.getInstance().getReportV2(
                dimension.getChildDimension().getChildExamId(),
                dimension.getTopicDimensionId(),
                Dictionary.REPORT_TYPE_DIMENSION,
                Dictionary.REPORT_COMPARE_AREA_COUNTRY,
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
                                //把报告结果置于报告图表对象中
                                if (ArrayListUtil.isNotEmpty(dimensionReports)  && ArrayListUtil.isNotEmpty(reportResultEntities)) {
                                    //图表项
                                    for (int i = 0; i < dimensionReports.size(); i++) {
                                        ReportItemEntity reportItemEntity = dimensionReports.get(i);
                                        //结果项
                                        for (int j = 0; j < reportResultEntities.size(); j++) {
                                            ReportResultEntity reportResultEntity = reportResultEntities.get(j);
                                            try {
                                                //判等：chart_item_id和relationId
                                                if (reportItemEntity.getChartItemId().equals(reportResultEntity.getRelationId())) {
                                                    //图表项中的reportResult为空才赋值
                                                    if (reportItemEntity.getReportResult() == null) {
                                                        reportItemEntity.setReportResult(reportResultEntity);
                                                    }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }

                                //刷新报告视图
                                refreshReportView(dimensionReports);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            //加载失败
                            emptyLayout.setErrorType(XEmptyLayout.NO_DATA_ENABLE_CLICK);
                        }
                    }
                });
    }

    /**
     * 刷新报告视图
     *
     * @param dimensionReports 图表对象集合
     */
    private void refreshReportView(List<ReportItemEntity> dimensionReports) throws Exception {
        if (ArrayListUtil.isNotEmpty(dimensionReports)) {
            //标题
            String chartName = dimensionReports.get(0).getChartItemName();
            if (!TextUtils.isEmpty(chartName)) {
                tvDimensionTitle.setText(chartName);
            } else {
                tvDimensionTitle.setText(dimension.getDimensionName());
            }

            //生成图表
            MPChartViewHelper.addMpChartView(context, llChart, dimensionReports);

            //报告结果
            ReportResultEntity reportResult = dimensionReports.get(0).getReportResult();
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
            }
        }
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
        //高度
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
    private void testInitView() {
        emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);

        try {
            Map dataMap = JsonUtil.fromJson(testReportStr, Map.class);
            ReportRootEntity data = InjectionWrapperUtil.injectMap(dataMap, ReportRootEntity.class);
            if (data != null && data.getChartDatas() != null) {

                if (data.getChartDatas().size() == 0 && data.getReportResults().size() == 0) {
//                                    ToastUtil.showShort(context,"感谢您的信息搜集");
                    tvNone.setVisibility(View.VISIBLE);
                    return;
                }

                //报告结果
                List<ReportResultEntity> reportResultEntities = data.getReportResults();
                //报告图表数据
                List<ReportItemEntity> dimensionReports = data.getChartDatas();
                //把报告结果置于报告图表对象中
                if (ArrayListUtil.isNotEmpty(dimensionReports)) {
                    for (int i = 0; i < dimensionReports.size(); i++) {
                        if (ArrayListUtil.isNotEmpty(reportResultEntities)) {
                            //图表对象中的报告结果为空，且报告结果集合中对应索引的对象不为空，则赋值
                            if (dimensionReports.get(i).getReportResult() == null
                                    && reportResultEntities.get(i) != null) {
                                dimensionReports.get(i).setReportResult(reportResultEntities.get(i));
                            }
                        }
                    }
                }

                //刷新报告视图
                refreshReportView(dimensionReports);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //加载失败
            emptyLayout.setErrorType(XEmptyLayout.NO_DATA_ENABLE_CLICK);
        }
    }

}
