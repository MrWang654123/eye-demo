package com.cheersmind.cheersgenie.features_v2.modules.college.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.DtoKey;
import com.cheersmind.cheersgenie.features.event.StopFlingEvent;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeEmploymentRatio;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeGraduationInfo;
import com.cheersmind.cheersgenie.features_v2.entity.CollegeInfoRatio;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;
import com.cheersmind.cheersgenie.mpcharts.Formmart.MyMarkerView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 院校毕业信息
 */
public class CollegeDetailGraduationFragment extends LazyLoadFragment {

    Unbinder unbinder;

    //院校ID
    private String collegeId;

    @BindView(R.id.nsv_main)
    NestedScrollView nsvMain;
    //空布局
    @BindView(R.id.emptyLayout)
    XEmptyLayout emptyLayout;
    //置顶按钮
    @BindView(R.id.fabGotoTop)
    FloatingActionButton fabGotoTop;

    //总体就业率
    @BindView(R.id.ll_employment_ratio)
    LinearLayout ll_employment_ratio;
    @BindView(R.id.barChartGraduationRatio)
    BarChart barChartGraduationRatio;
    private PercentFormatter formatter = new PercentFormatter();

    private Typeface mTfLight;
    //轴宽度
    float axisLineWidth = 1.7f;
    //x轴颜色
    String xAxisColor = "#289bd3";
    //y轴颜色
    String yAxisColor = "#3ca5d8";
    //轴文本颜色
    String axisTextColor = "#333333";
    //数据集合1的颜色
    protected String dataSetColor_1 = "#2ec8c9";

    //就业与薪资
    @BindView(R.id.ll_info_ratio)
    LinearLayout llInfoRatio;
    @BindView(R.id.cl_job_ratio)
    ConstraintLayout clJobRatio;
    @BindView(R.id.tv_job_ratio_val)
    TextView tvJobRatioVal;
    @BindView(R.id.cl_salary)
    ConstraintLayout clSalary;
    @BindView(R.id.tv_salary_val)
    TextView tvSalaryVal;

    //国内读研
    @BindView(R.id.ll_postgraduate)
    LinearLayout llPostgraduate;
    @BindView(R.id.tv_postgraduate_val)
    TextView tvPostgraduateVal;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册事件
        EventBus.getDefault().register(this);
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_college_detail_graduation;
    }

    @Override
    protected void onInitView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

        //获取数据
        Bundle bundle = getArguments();
        if (bundle != null) {
            collegeId = bundle.getString(DtoKey.COLLEGE_ID);
        }

        //设置无数据提示文本
        emptyLayout.setNoDataTip(getResources().getString(R.string.empty_tip_college_detail_graduation));
        //重载监听
        emptyLayout.setOnReloadListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                //初始化为加载状态
                emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);
                //加载数据
                loadGraduationInfo();
            }
        });
        //初始化为加载状态
        emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);

        //初始隐藏置顶按钮
        fabGotoTop.setVisibility(View.INVISIBLE);

        mTfLight = Typeface.createFromAsset(getContext().getAssets(), "OpenSans-Light.ttf");
    }

    @Override
    protected void lazyLoad() {
        //加载招生基本信息
        loadGraduationInfo();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //注销事件
        EventBus.getDefault().unregister(this);
    }

    /**
     * 停止Fling的消息
     *
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStopFlingNotice(StopFlingEvent event) {
        if (nsvMain != null) {
//            nsvMain.stopNestedScroll();
            nsvMain.stopNestedScroll(1);
        }
    }


    /**
     * 加载毕业信息
     */
    private void loadGraduationInfo() {
        //通信等待提示
//        emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);

        DataRequestService.getInstance().getCollegeGraduationInfo(collegeId, new BaseService.ServiceCallback() {
            @Override
            public void onFailure(QSCustomException e) {
                //空布局：网络连接有误，或者请求失败
                emptyLayout.setErrorType(XEmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onResponse(Object obj) {
                //空布局：隐藏
                emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);

                try {
                    Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                    CollegeGraduationInfo graduationInfo = InjectionWrapperUtil.injectMap(dataMap, CollegeGraduationInfo.class);

                    //空数据处理
                    if (graduationInfo == null) {
                        emptyLayout.setErrorType(XEmptyLayout.NO_DATA);
                        return;
                    }

                    //刷新子模块视图
                    refreshBlockViews(graduationInfo);

                } catch (Exception e) {
                    e.printStackTrace();
                    //空布局：加载失败
                    emptyLayout.setErrorType(XEmptyLayout.NO_DATA_ENABLE_CLICK);
                }
            }
        }, httpTag, getActivity());
    }

    /**
     * 刷新子模块视图
     *
     * @param graduationInfo 毕业信息
     */
    private void refreshBlockViews(CollegeGraduationInfo graduationInfo) {
        if (graduationInfo == null) {
            ll_employment_ratio.setVisibility(View.GONE);
            return;
        }

        //总体就业率
        List<CollegeEmploymentRatio> employmentRatio = graduationInfo.getEmploymentRatio();
        if (!allIsZero(employmentRatio)) {
            refreshEmploymentBarChart(employmentRatio);

        } else {
            ll_employment_ratio.setVisibility(View.GONE);
        }

        //就业与薪资=薪资
        tvSalaryVal.setText(graduationInfo.getFive_year_salary() > 0 ? String.valueOf(graduationInfo.getFive_year_salary()) : "--");
        //就业与薪资=就业
        List<CollegeInfoRatio> infoRatios = graduationInfo.getInfoRatio();
        if (ArrayListUtil.isNotEmpty(infoRatios)) {
            CollegeInfoRatio infoRatio = null;
            for (CollegeInfoRatio item : infoRatios) {
                if ("employment".equals(item.getType())) {
                    infoRatio = item;
                    break;
                }
            }

            if (infoRatio != null && infoRatio.getRatio() > 0.000001) {
                clJobRatio.setVisibility(View.VISIBLE);
                tvJobRatioVal.setText(String.valueOf(infoRatio.getRatio() * 100));

            } else {
                clJobRatio.setVisibility(View.GONE);
            }
        }

        //国内读研
        if (ArrayListUtil.isNotEmpty(infoRatios)) {
            CollegeInfoRatio infoRatio = null;
            for (CollegeInfoRatio item : infoRatios) {
                if ("domestic_postgraduate".equals(item.getType())) {
                    infoRatio = item;
                    break;
                }
            }

            if (infoRatio != null && infoRatio.getRatio() > 0.000001) {
                llPostgraduate.setVisibility(View.VISIBLE);
                tvPostgraduateVal.setText(String.format(Locale.CHINA, "%.2f", infoRatio.getRatio() * 100));
//                //有四舍五入
//                tvPostgraduateVal.setText(String.format(Locale.CHINA, "%.2f", infoRatio.getRatio() * 100));
//                //有四舍五入
//                DecimalFormat df = new DecimalFormat("#.0");
//                tvPostgraduateVal.setText(String.valueOf(df.format(2.221)));
//                //可控制是否四舍五入
//                BigDecimal bg = new BigDecimal(1.5834);
//                double f1 = bg.setScale(1, BigDecimal.ROUND_FLOOR).doubleValue();
//                tvPostgraduateVal.setText(String.valueOf(f1));
//                //可控制是否四舍五入
//                NumberFormat nf = NumberFormat.getNumberInstance();
//                // 保留两位小数
//                nf.setMaximumFractionDigits(2);
//                // 如果不需要四舍五入，可以使用RoundingMode.DOWN
//                nf.setRoundingMode(RoundingMode.HALF_UP);
//                tvPostgraduateVal.setText(nf.format(0.238));

            } else {
                llPostgraduate.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 所有就业率项都为零
     * @param employmentRatios 就业率集合
     * @return true：都为0
     */
    private boolean allIsZero(List<CollegeEmploymentRatio> employmentRatios) {
        if (ArrayListUtil.isNotEmpty(employmentRatios)) {
            for (CollegeEmploymentRatio ratio : employmentRatios) {
                if (ratio.getRatio() > 0.000001) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 刷新总体就业率的图表
     *
     * @param employmentRatios 就业率集合
     */
    private void refreshEmploymentBarChart(List<CollegeEmploymentRatio> employmentRatios) {
        barChartGraduationRatio.setBackgroundColor(Color.parseColor("#f7f7f7"));

        barChartGraduationRatio.setDrawBarShadow(false);
        barChartGraduationRatio.setDrawValueAboveBar(true);

        barChartGraduationRatio.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        barChartGraduationRatio.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        barChartGraduationRatio.setPinchZoom(false);

        barChartGraduationRatio.setDrawGridBackground(false);
        // mChart.setDrawYLabels(false);

//        IAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(mChart);

        XAxis xAxis = barChartGraduationRatio.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(mTfLight);
        //x轴文本颜色
        xAxis.setTextColor(Color.parseColor(axisTextColor));
        //x轴颜色
        xAxis.setAxisLineColor(Color.parseColor(xAxisColor));
        //x轴宽度
        xAxis.setAxisLineWidth(axisLineWidth);
        xAxis.setDrawGridLines(false);
        //X轴文本旋转角度
//        xAxis.setLabelRotationAngle(-20);
        xAxis.setGranularity(1f); // only intervals of 1 day
//        xAxis.setLabelCount(7);
        //右侧还有部分图表未展示出来，此时还需要对X轴进行相应的设置
//        xAxis.setAxisMinimum(0f);
        //放到setData之后
//        xAxis.setAxisMaximum(xValues.size());
//        //将X轴的值显示在中央
//        xAxis.setCenterAxisLabels(true);

        //X轴文本
//        xAxis.setValueFormatter(new IndexAxisValueFormatter(xLabels));
//        xAxis.setValueFormatter(xAxisFormatter);

//        IAxisValueFormatter custom = new MyAxisValueFormatter();

        YAxis leftAxis = barChartGraduationRatio.getAxisLeft();
        leftAxis.setTypeface(mTfLight);
        //y轴颜色
        leftAxis.setTextColor(Color.parseColor(axisTextColor));
        //y轴颜色
        leftAxis.setAxisLineColor(Color.parseColor(yAxisColor));
        //y轴宽度
        leftAxis.setAxisLineWidth(axisLineWidth);
        leftAxis.setLabelCount(8, false);
//        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        //最小值
        leftAxis.setAxisMinimum(0);
        //设置X Y轴网格线为虚线（实体线长度、间隔距离、偏移量：通常使用 0）
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        //水平偏移
        leftAxis.setXOffset(20);
        //格式化
        leftAxis.setValueFormatter(formatter);

        YAxis rightAxis = barChartGraduationRatio.getAxisRight();
        rightAxis.setTextColor(Color.parseColor("#f7f7f7"));
        rightAxis.setDrawGridLines(false);
        rightAxis.setTypeface(mTfLight);
        rightAxis.setLabelCount(8, false);
        rightAxis.setAxisLineColor(Color.parseColor("#f7f7f7"));
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        Legend l = barChartGraduationRatio.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
//        l.setYOffset(10f);
        l.setXEntrySpace(4f);

//        setData();

        //X轴文本
        List<String> xLabels = new ArrayList<>();
        for (CollegeEmploymentRatio ratio : employmentRatios) {
            xLabels.add(ratio.getDegree());
        }

        IndexAxisValueFormatter iAxisValueFormatter = new IndexAxisValueFormatter(xLabels);
        //X轴文本格式化
        barChartGraduationRatio.getXAxis().setValueFormatter(iAxisValueFormatter);

        //选中的标记
        MyMarkerView mv = (MyMarkerView) barChartGraduationRatio.getMarker();
        if (mv == null) {
            MyMarkerView marker = new MyMarkerView(getContext(), R.layout.custom_marker_view);
            barChartGraduationRatio.setMarker(marker);
            mv = marker;
        }
//        MyMarkerView mv = new MyMarkerView(context, R.layout.custom_marker_view);
        mv.setChartView(barChartGraduationRatio); // For bounds control
        mv.setxLabels(xLabels);
//        mChart.setMarker(mv); // Set the marker to the chart

        //添加数据
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        //比较项
        if (ArrayListUtil.isNotEmpty(employmentRatios)) {
            ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();
            for (int i = 0; i < employmentRatios.size(); i++) {
                CollegeEmploymentRatio employmentRatio = employmentRatios.get(i);
                yValues.add(new BarEntry(i, (float) employmentRatio.getRatio() * 100));
            }

            BarDataSet set1;
            set1 = new BarDataSet(yValues, "总体就业率");
            set1.setDrawIcons(false);
            set1.setColor(Color.parseColor(dataSetColor_1));

            dataSets.add(set1);
        }

        ChartData<?> mChartData = new BarData(dataSets);

        mChartData.setValueTextSize(10f);
        mChartData.setValueTypeface(mTfLight);
        ((BarData) mChartData).setBarWidth(0.5f);
        //内容文本格式
        mChartData.setValueFormatter(formatter);


//        xAxis.setAxisMaximum(xLabels.size());

        leftAxis.setAxisMinimum(0);
        leftAxis.setAxisMaximum(100);

        barChartGraduationRatio.setData((BarData) mChartData);
//        mChart.invalidate();
        barChartGraduationRatio.animateY(500);
    }

}

