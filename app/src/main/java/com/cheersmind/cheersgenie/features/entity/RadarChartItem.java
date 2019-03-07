package com.cheersmind.cheersgenie.features.entity;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.main.entity.ReportItemEntity;
import com.cheersmind.cheersgenie.mpcharts.Formmart.MyMarkerView;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;

import java.util.ArrayList;
import java.util.List;

import static com.cheersmind.cheersgenie.features.constant.Dictionary.CHART_RADAR;

/**
 * 网状图数据
 */
public class RadarChartItem extends ChartItem {

    private RadarChart mChart;

    public RadarChartItem(Context context) {
        super(context);
    }

    public RadarChartItem(Context context, ChartData<?> cd) {
        super(context, cd);
    }

    @Override
    public int getItemType() {
        return CHART_RADAR;
    }

    @Override
    public void initChart(View chartView) {
        super.initChart(chartView);

        if (chartView == null) {
            return;
        }

        mChart = (RadarChart) chartView;

        mChart.setBackgroundColor(Color.parseColor("#f7f7f7"));

        mChart.getDescription().setEnabled(false);

        mChart.setWebLineWidth(1f);
        mChart.setWebColor(Color.LTGRAY);
        mChart.setWebLineWidthInner(1f);
        mChart.setWebColorInner(Color.LTGRAY);
        mChart.setWebAlpha(100);

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

        mChartData.setValueTypeface(mTfLight);
        mChartData.setValueTextSize(8f);
        // 是否绘制Y值到图表
        mChartData.setDrawValues(true);
        mChartData.setValueTextColor(Color.parseColor("#333333"));

        XAxis xAxis = mChart.getXAxis();
        xAxis.setTypeface(mTfLight);
        xAxis.setTextSize(10f);
        xAxis.setYOffset(0f);
        xAxis.setXOffset(0f);
        xAxis.setTextColor(Color.parseColor("#333333"));

        YAxis yAxis = mChart.getYAxis();
        yAxis.setTypeface(mTfLight);
        yAxis.setLabelCount(5, false);
        yAxis.setTextSize(9f);
//        yAxis.setAxisMinimum(getMaxAndMinValue().get(1));
//        yAxis.setAxisMaximum(getMaxAndMinValue().get(0));
        yAxis.setAxisMinimum(reportData.getMinScore());
        yAxis.setDrawLabels(false);

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setTypeface(mTfLight);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(5f);
        l.setTextColor(Color.parseColor("#666666"));

        mChart.setData((RadarData) mChartData);
//        mChart.invalidate();
        mChart.animateXY(500, 500);
    }


//    @Override
//    public ChartItem generateChartData(ReportItemEntity reportData) {
//        if(reportData == null || reportData.getItems() == null || reportData.getItems().size() == 0){
//            return null;
//        }
//        List<ReportFactorEntity> items = reportData.getItems();
//        xLabels = new ArrayList<>();
//
//
//        int chartCounts = 1;
//        if(items.get(0).getCompareScore()>0){
//            chartCounts = 2;
//        }
//
//        ArrayList<IRadarDataSet> sets = new ArrayList<IRadarDataSet>();
//        for(int j=0;j<chartCounts;j++){
//            ArrayList<RadarEntry> yValues = new ArrayList<RadarEntry>();
//            for(int i=0;i<items.size();i++){
//                ReportFactorEntity rfe = items.get(i);
//                if(j == 0){
//                    xLabels.add(rfe.getItemName());
//                    yValues.add(new RadarEntry((float) rfe.getChildScore()));
//                }else if(j == 1){
//                    yValues.add(new RadarEntry((float) rfe.getCompareScore()));
//                }
//
//            }
//
//            RadarDataSet set1;
//            if(j == 0){
//                set1 = new RadarDataSet(yValues, "我");
//                set1.setColor(Color.parseColor(dataSetColor_1));
//                set1.setFillColor(Color.parseColor(dataSetColor_1));
//                set1.setDrawFilled(true);
//                set1.setFillAlpha(30);
//                set1.setLineWidth(2f);
//                set1.setDrawHighlightCircleEnabled(true);
//                set1.setDrawHighlightIndicators(false);
//                // 是否绘制Y值到图表
//                set1.setDrawValues(true);
//            }else{
//                set1 = new RadarDataSet(yValues, reportData.getCompareName());
//                set1.setColor(Color.parseColor(dataSetColor_2));
//                set1.setFillColor(Color.parseColor(dataSetColor_2));
//                set1.setDrawFilled(true);
//                set1.setFillAlpha(50);
//                set1.setLineWidth(2f);
//                set1.setDrawHighlightCircleEnabled(true);
//                set1.setDrawHighlightIndicators(false);
//            }
//
//            sets.add(set1);
//        }
//
////        mChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xLabels));
////
////        RadarData data = new RadarData(sets);
////        data.setValueTypeface(mTfLight);
////        data.setValueTextSize(8f);
////        // 是否绘制Y值到图表
////        data.setDrawValues(true);
////        data.setValueTextColor(Color.parseColor("#333333"));
//
////        mChartData = data;
//
//        mChartData = new RadarData(sets);
//
//        return super.generateChartData(reportData);
//    }


    @Override
    public ChartItem generateChartData(ReportItemEntity reportData) {
        if(reportData == null || reportData.getItems() == null || reportData.getItems().size() == 0){
            return null;
        }
        List<ChartCompareItem> items = reportData.getItems();
        xLabels = new ArrayList<>();

        ArrayList<IRadarDataSet> dataSets = new ArrayList<>();
        boolean first = true;
        //比较项
        if (ArrayListUtil.isNotEmpty(items)) {
            for (ChartCompareItem compareItem : items) {
                //分数项
                List<ChartScoreItem> scoreItems = compareItem.getScoreItems();
                if (ArrayListUtil.isNotEmpty(scoreItems)) {
                    ArrayList<RadarEntry> yValues = new ArrayList<>();
                    for (int i=0; i<scoreItems.size(); i++) {
                        ChartScoreItem scoreItem = scoreItems.get(i);
                        yValues.add(new RadarEntry((float) scoreItem.getScore()));
                        //第一次循环添加X轴文本
                        if (first) {
                            xLabels.add(scoreItem.getItemName());
                        }
                    }
                    first = false;

                    RadarDataSet set1;
                    set1 = new RadarDataSet(yValues, compareItem.getCompareName());
                    set1.setDrawFilled(true);
                    set1.setLineWidth(2f);
                    set1.setDrawHighlightCircleEnabled(true);
                    set1.setDrawHighlightIndicators(false);

                    if (Dictionary.REPORT_CHART_COMPARE_ID_MINE.equals(compareItem.getCompareId())) {//我
                        set1.setColor(Color.parseColor(dataSetColor_1));
                        set1.setFillColor(Color.parseColor(dataSetColor_1));
                        set1.setFillAlpha(30);
                        // 是否绘制Y值到图表
                        set1.setDrawValues(true);

                    } else if (Dictionary.REPORT_CHART_COMPARE_ID_COUNTRY.equals(compareItem.getCompareId())) {//全国
                        set1.setColor(Color.parseColor(dataSetColor_2));
                        set1.setFillColor(Color.parseColor(dataSetColor_2));
                        set1.setFillAlpha(50);

                    } else if (Dictionary.REPORT_CHART_COMPARE_ID_GRADE.equals(compareItem.getCompareId())) {//年级
                        set1.setColor(Color.parseColor(dataSetColor_3));
                        set1.setFillColor(Color.parseColor(dataSetColor_3));
                        set1.setFillAlpha(70);
                    }
                    dataSets.add(set1);
                }
            }
        }

//        for(int j=0;j<chartCounts;j++){
//            ArrayList<RadarEntry> yValues = new ArrayList<RadarEntry>();
//            for(int i=0;i<items.size();i++){
//                ReportFactorEntity rfe = items.get(i);
//                if(j == 0){
//                    xLabels.add(rfe.getItemName());
//                    yValues.add(new RadarEntry((float) rfe.getChildScore()));
//                }else if(j == 1){
//                    yValues.add(new RadarEntry((float) rfe.getCompareScore()));
//                }
//
//            }
//
//            RadarDataSet set1;
//            if(j == 0){
//                set1 = new RadarDataSet(yValues, "我");
//                set1.setColor(Color.parseColor(dataSetColor_1));
//                set1.setFillColor(Color.parseColor(dataSetColor_1));
//                set1.setDrawFilled(true);
//                set1.setFillAlpha(30);
//                set1.setLineWidth(2f);
//                set1.setDrawHighlightCircleEnabled(true);
//                set1.setDrawHighlightIndicators(false);
//                // 是否绘制Y值到图表
//                set1.setDrawValues(true);
//            }else{
//                set1 = new RadarDataSet(yValues, reportData.getCompareName());
//                set1.setColor(Color.parseColor(dataSetColor_2));
//                set1.setFillColor(Color.parseColor(dataSetColor_2));
//                set1.setDrawFilled(true);
//                set1.setFillAlpha(50);
//                set1.setLineWidth(2f);
//                set1.setDrawHighlightCircleEnabled(true);
//                set1.setDrawHighlightIndicators(false);
//            }
//
//            sets.add(set1);
//        }
//
//        List<ReportFactorEntity> items = reportData.getItems();
//        xLabels = new ArrayList<>();
//
//
//        int chartCounts = 1;
//        if(items.get(0).getCompareScore()>0){
//            chartCounts = 2;
//        }
//
//        ArrayList<IRadarDataSet> sets = new ArrayList<IRadarDataSet>();
//        for(int j=0;j<chartCounts;j++){
//            ArrayList<RadarEntry> yValues = new ArrayList<RadarEntry>();
//            for(int i=0;i<items.size();i++){
//                ReportFactorEntity rfe = items.get(i);
//                if(j == 0){
//                    xLabels.add(rfe.getItemName());
//                    yValues.add(new RadarEntry((float) rfe.getChildScore()));
//                }else if(j == 1){
//                    yValues.add(new RadarEntry((float) rfe.getCompareScore()));
//                }
//
//            }
//
//            RadarDataSet set1;
//            if(j == 0){
//                set1 = new RadarDataSet(yValues, "我");
//                set1.setColor(Color.parseColor(dataSetColor_1));
//                set1.setFillColor(Color.parseColor(dataSetColor_1));
//                set1.setDrawFilled(true);
//                set1.setFillAlpha(30);
//                set1.setLineWidth(2f);
//                set1.setDrawHighlightCircleEnabled(true);
//                set1.setDrawHighlightIndicators(false);
//                // 是否绘制Y值到图表
//                set1.setDrawValues(true);
//            }else{
//                set1 = new RadarDataSet(yValues, reportData.getCompareName());
//                set1.setColor(Color.parseColor(dataSetColor_2));
//                set1.setFillColor(Color.parseColor(dataSetColor_2));
//                set1.setDrawFilled(true);
//                set1.setFillAlpha(50);
//                set1.setLineWidth(2f);
//                set1.setDrawHighlightCircleEnabled(true);
//                set1.setDrawHighlightIndicators(false);
//            }
//
//            sets.add(set1);
//        }

        mChartData = new RadarData(dataSets);

        return super.generateChartData(reportData);
    }


    /**
     * x轴文本旋转角度
     */
    protected void onXLabelRotationAngle() {
        if (xLabels != null && xLabelRotationAngle == -1) {
            //X轴文本旋转角度
            xLabelRotationAngle = 0;
        }
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
