package com.cheersmind.cheersgenie.mpcharts;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.main.entity.ReportFactorEntity;
import com.cheersmind.cheersgenie.main.entity.ReportItemEntity;
import com.cheersmind.cheersgenie.mpcharts.Formmart.MyMarkerView;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/8/2.
 */

public class MPRadarChart extends MPBaseChart {

    private RadarChart mChart;
    private List<String> xLabels;

    public MPRadarChart(Context context, ReportItemEntity reportData) {
        super(context,reportData);
    }

    public MPRadarChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MPRadarChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initChart() {
        super.initChart();
        mChart = new RadarChart(context);
        LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        this.addView(mChart,params);

        mChart.setBackgroundColor(Color.parseColor("#f7f7f7"));

        mChart.getDescription().setEnabled(false);

        mChart.setWebLineWidth(1f);
        mChart.setWebColor(Color.LTGRAY);
        mChart.setWebLineWidthInner(1f);
        mChart.setWebColorInner(Color.LTGRAY);
        mChart.setWebAlpha(100);

        setData();

        MyMarkerView mv = new MyMarkerView(context, R.layout.custom_marker_view);
        mv.setChartView(mChart); // For bounds control
        mv.setxLabels(xLabels);
        mChart.setMarker(mv); // Set the marker to the chart

        mChart.animateXY(500, 500);

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
    }

    public void setData() {

        if(reportData==null || reportData.getItems()==null || reportData.getItems().size()==0){
            return;
        }
        List<ReportFactorEntity> items = reportData.getItems();
        xLabels = new ArrayList<>();


        int chartCounts = 1;
        if(items.get(0).getCompareScore()>0){
            chartCounts = 2;
        }

        ArrayList<IRadarDataSet> sets = new ArrayList<IRadarDataSet>();
        for(int j=0;j<chartCounts;j++){
            ArrayList<RadarEntry> yValues = new ArrayList<RadarEntry>();
            for(int i=0;i<items.size();i++){
                ReportFactorEntity rfe = items.get(i);
                if(j == 0){
                    xLabels.add(rfe.getItemName());
                    yValues.add(new RadarEntry((float) rfe.getChildScore()));
                }else if(j == 1){
                    yValues.add(new RadarEntry((float) rfe.getCompareScore()));
                }

            }

            RadarDataSet set1;
            if(j == 0){
                set1 = new RadarDataSet(yValues, "我");
                set1.setColor(Color.parseColor(dataSetColor_1));
                set1.setFillColor(Color.parseColor(dataSetColor_1));
                set1.setDrawFilled(true);
                set1.setFillAlpha(30);
                set1.setLineWidth(2f);
                set1.setDrawHighlightCircleEnabled(true);
                set1.setDrawHighlightIndicators(false);
                // 是否绘制Y值到图表
                set1.setDrawValues(true);
            }else{
                set1 = new RadarDataSet(yValues, reportData.getCompareName());
                set1.setColor(Color.parseColor(dataSetColor_2));
                set1.setFillColor(Color.parseColor(dataSetColor_2));
                set1.setDrawFilled(true);
                set1.setFillAlpha(50);
                set1.setLineWidth(2f);
                set1.setDrawHighlightCircleEnabled(true);
                set1.setDrawHighlightIndicators(false);
            }

            sets.add(set1);
        }

        mChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xLabels));

        RadarData data = new RadarData(sets);
        data.setValueTypeface(mTfLight);
        data.setValueTextSize(8f);
        // 是否绘制Y值到图表
        data.setDrawValues(true);
        data.setValueTextColor(Color.parseColor("#333333"));

        mChart.setData(data);
        mChart.invalidate();
    }
}
