package com.cheersmind.smartbrain.mpcharts;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.cheersmind.smartbrain.R;
import com.cheersmind.smartbrain.main.entity.ReportFactorEntity;
import com.cheersmind.smartbrain.main.entity.ReportItemEntity;
import com.cheersmind.smartbrain.mpcharts.Formmart.MyMarkerView;
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
        xAxis.setTextColor(Color.parseColor("#666666"));

        YAxis yAxis = mChart.getYAxis();
        yAxis.setTypeface(mTfLight);
        yAxis.setLabelCount(5, false);
        yAxis.setTextSize(9f);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(reportData.getMaxScore());
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
        ArrayList<RadarEntry> yValues = new ArrayList<RadarEntry>();
        for(int i=0;i<items.size();i++){
            ReportFactorEntity rfe = items.get(i);
            xLabels.add(rfe.getItemName());
            yValues.add(new RadarEntry((float) rfe.getChildScore()));
        }

        mChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xLabels));

        RadarDataSet set1 = new RadarDataSet(yValues, "我");
        set1.setColor(Color.rgb(103, 110, 129));
        set1.setFillColor(Color.rgb(103, 110, 129));
        set1.setDrawFilled(true);
        set1.setFillAlpha(180);
        set1.setLineWidth(2f);
        set1.setDrawHighlightCircleEnabled(true);
        set1.setDrawHighlightIndicators(false);


        ArrayList<IRadarDataSet> sets = new ArrayList<IRadarDataSet>();
        sets.add(set1);

        RadarData data = new RadarData(sets);
        data.setValueTypeface(mTfLight);
        data.setValueTextSize(8f);
        data.setDrawValues(false);
        data.setValueTextColor(Color.parseColor("#666666"));

        mChart.setData(data);
        mChart.invalidate();
    }
}
