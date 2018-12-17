package com.cheersmind.cheersgenie.features.entity;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.main.entity.ChartCompareItem;
import com.cheersmind.cheersgenie.main.entity.ChartScoreItem;
import com.cheersmind.cheersgenie.main.entity.ReportFactorEntity;
import com.cheersmind.cheersgenie.main.entity.ReportItemEntity;
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
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.List;

import static com.cheersmind.cheersgenie.features.constant.Dictionary.CHART_BAR_V;

/**
 * 垂直柱状图数据
 */
public class VBarChartItem extends ChartItem {

    private BarChart mChart;

    public VBarChartItem(Context context) {
        super(context);
    }

    public VBarChartItem(Context context, ChartData<?> cd) {
        super(context, cd);
    }

    @Override
    public int getItemType() {
        return CHART_BAR_V;
    }

    @Override
    public void initChart(View chartView) {
        super.initChart(chartView);

        if (chartView == null) {
            return;
        }

        mChart = (BarChart) chartView;

//        mChart.setOnChartValueSelectedListener(this);

        mChart.setBackgroundColor(Color.parseColor("#f7f7f7"));

        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);

        mChart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawGridBackground(false);
        // mChart.setDrawYLabels(false);

//        IAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(mChart);

        XAxis xAxis = mChart.getXAxis();
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
        xAxis.setLabelRotationAngle(-20);
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

        YAxis leftAxis = mChart.getAxisLeft();
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
        leftAxis.setAxisMinimum(getMaxAndMinValue().get(1));
        //设置X Y轴网格线为虚线（实体线长度、间隔距离、偏移量：通常使用 0）
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        //水平偏移
        leftAxis.setXOffset(20);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setTextColor(Color.parseColor("#f7f7f7"));
        rightAxis.setDrawGridLines(false);
        rightAxis.setTypeface(mTfLight);
        rightAxis.setLabelCount(8, false);
        rightAxis.setAxisLineColor(Color.parseColor("#f7f7f7"));
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        Legend l = mChart.getLegend();
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

        //X轴文本旋转角度
        mChart.getXAxis().setLabelRotationAngle(xLabelRotationAngle);

        //X轴文本格式化
        mChart.getXAxis().setValueFormatter(iAxisValueFormatter);

        //选中的标记
        MyMarkerView mv = (MyMarkerView) mChart.getMarker();
        if (mv == null) {
            MyMarkerView marker = new MyMarkerView(context, R.layout.custom_marker_view);
            mChart.setMarker(marker);
            mv = marker;
        }
//        MyMarkerView mv = new MyMarkerView(context, R.layout.custom_marker_view);
        mv.setChartView(mChart); // For bounds control
        mv.setxLabels(xLabels);
//        mChart.setMarker(mv); // Set the marker to the chart


        List<?> dataSets = mChartData.getDataSets();
        mChartData.setValueTextSize(10f);
        mChartData.setValueTypeface(mTfLight);
        if (dataSets.size() == 1) {
            ((BarData)mChartData).setBarWidth(0.5f);
        } else {
            //右侧还有部分图表未展示出来，此时还需要对X轴进行相应的设置
            mChart.getXAxis().setAxisMinimum(0f);
            mChart.getXAxis().setAxisMaximum(xLabels.size());
            //将X轴的值显示在中央
            mChart.getXAxis().setCenterAxisLabels(true);

            //由堆积柱状图变为并排多列柱状图
/**
 * float groupSpace = 0.3f;   //柱状图组之间的间距
 * float barSpace =  0.05f;  //每条柱状图之间的间距  一组两个柱状图
 * float barWidth = 0.3f;    //每条柱状图的宽度     一组两个柱状图
 * (barWidth + barSpace) * barAmount + groupSpace = (0.3 + 0.05) * 2 + 0.3 = 1.00
 * 3个数值 加起来 必须等于 1 即100% 按照百分比来计算 组间距 柱状图间距 柱状图宽度
 */
            int barAmount = dataSets.size(); //需要显示柱状图的类别 数量
//设置组间距占比30% 每条柱状图宽度占比 70% /barAmount  柱状图间距占比 0%
            float groupSpace = 0.3f; //柱状图组之间的间距
            float barWidth = (1f - groupSpace) / barAmount;
            float barSpace = 0f;
//设置柱状图宽度
            ((BarData)mChartData).setBarWidth(barWidth);
//(起始点、柱状图组间距、柱状图之间间距)
            ((BarData)mChartData).groupBars(0f, groupSpace, barSpace);
        }


//        xAxis.setAxisMaximum(xLabels.size());

        leftAxis.setAxisMinimum(reportData.getMinScore());

        mChart.setData((BarData) mChartData);
//        mChart.invalidate();
        mChart.animateY(500);
    }


//    @Override
//    public ChartItem generateChartData(ReportItemEntity reportData) {
//
//        if(reportData==null || reportData.getItems()==null || reportData.getItems().size()==0){
//            return this;
//        }
//        List<ReportFactorEntity> items = reportData.getItems();
//        xLabels = new ArrayList<>();
//
//        int chartCounts = 1;
//        if(items.get(0).getCompareScore()>0){
//            chartCounts = 2;
//        }
//
//        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
//        for(int j=0;j<chartCounts;j++) {
//            ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();
//            for (int i = 0; i < items.size(); i++) {
//                ReportFactorEntity rfe = items.get(i);
//                if (j == 0) {
//                    xLabels.add(rfe.getItemName());
//                    yValues.add(new BarEntry(i, (float) rfe.getChildScore()));
//                } else if (j == 1) {
//                    yValues.add(new BarEntry(i, (float) rfe.getCompareScore()));
//                }
//            }
//
//            BarDataSet set1;
//            if(j == 0){
//                set1 = new BarDataSet(yValues, "我");
//                set1.setDrawIcons(false);
//                set1.setColors(Color.parseColor(dataSetColor_1));
//            }else{
//                set1 = new BarDataSet(yValues, reportData.getCompareName());
//                set1.setDrawIcons(false);
//                set1.setColors(Color.parseColor(dataSetColor_2));
//            }
//            dataSets.add(set1);
//        }
//
//        mChartData = new BarData(dataSets);
//
//        return super.generateChartData(reportData);
//    }


    @Override
    public ChartItem generateChartData(ReportItemEntity reportData) {

        if(reportData==null || reportData.getItems()==null || reportData.getItems().size()==0){
            return null;
        }
        List<ChartCompareItem> items = reportData.getItems();
        xLabels = new ArrayList<>();

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        boolean first = true;
        //比较项
        if (ArrayListUtil.isNotEmpty(items)) {
            for (ChartCompareItem compareItem : items) {
                //分数项
                List<ChartScoreItem> scoreItems = compareItem.getScoreItems();
                if (ArrayListUtil.isNotEmpty(scoreItems)) {
                    ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();
                    for (int i=0; i<scoreItems.size(); i++) {
                        ChartScoreItem scoreItem = scoreItems.get(i);
                        yValues.add(new BarEntry(i, (float) scoreItem.getScore()));
                        //第一次循环添加X轴文本
                        if (first) {
                            xLabels.add(scoreItem.getItemName());
                        }
                    }
                    first = false;

                    BarDataSet set1;
                    set1 = new BarDataSet(yValues, compareItem.getCompareName());
                    set1.setDrawIcons(false);
                    if (Dictionary.REPORT_CHART_COMPARE_ID_MINE.equals(compareItem.getCompareId())) {//我
                        set1.setColor(Color.parseColor(dataSetColor_1));

                    } else if (Dictionary.REPORT_CHART_COMPARE_ID_COUNTRY.equals(compareItem.getCompareId())) {//全国
                        set1.setColor(Color.parseColor(dataSetColor_2));

                    } else if (Dictionary.REPORT_CHART_COMPARE_ID_GRADE.equals(compareItem.getCompareId())) {//年级
                        set1.setColor(Color.parseColor(dataSetColor_3));
                    }
                    dataSets.add(set1);
                }
            }
        }

//        int chartCounts = 1;
//        if(items.get(0).getCompareScore()>0){
//            chartCounts = 2;
//        }
//
//        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
//        for(int j=0;j<chartCounts;j++) {
//            ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();
//            for (int i = 0; i < items.size(); i++) {
//                ReportFactorEntity rfe = items.get(i);
//                if (j == 0) {
//                    xLabels.add(rfe.getItemName());
//                    yValues.add(new BarEntry(i, (float) rfe.getChildScore()));
//                } else if (j == 1) {
//                    yValues.add(new BarEntry(i, (float) rfe.getCompareScore()));
//                }
//            }
//
//            BarDataSet set1;
//            if(j == 0){
//                set1 = new BarDataSet(yValues, "我");
//                set1.setDrawIcons(false);
//                set1.setColors(Color.parseColor(dataSetColor_1));
//            }else{
//                set1 = new BarDataSet(yValues, reportData.getCompareName());
//                set1.setDrawIcons(false);
//                set1.setColors(Color.parseColor(dataSetColor_2));
//            }
//            dataSets.add(set1);
//        }

        mChartData = new BarData(dataSets);

        return super.generateChartData(reportData);
    }


    /**
     * 重绘图表
     */
    @Override
    public void invalidate() {
        if (mChart != null) {
            mChart.invalidate();
        }
    }


}
