package com.cheersmind.smartbrain.mpcharts;

import android.content.Context;
import android.graphics.Color;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

import com.cheersmind.smartbrain.R;
import com.cheersmind.smartbrain.main.entity.ReportFactorEntity;
import com.cheersmind.smartbrain.main.entity.ReportItemEntity;
import com.cheersmind.smartbrain.mpcharts.Formmart.MyMarkerView;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/8/2.
 */

public class MPHorizontalBarChart extends MPBaseChart implements OnChartValueSelectedListener {

    private HorizontalBarChart mChart;

    private List<String> xLabels;

    protected RectF mOnValueSelectedRectF = new RectF();

    public MPHorizontalBarChart(Context context, ReportItemEntity reportData) {
        super(context,reportData);
    }

    public MPHorizontalBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MPHorizontalBarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void initChart() {
        super.initChart();
        mChart = new HorizontalBarChart(context);
        LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        this.addView(mChart,params);
        mChart.setOnChartValueSelectedListener(this);
        // mChart.setHighlightEnabled(false);
        mChart.setBackgroundColor(Color.parseColor("#f7f7f7"));

        mChart.setDrawBarShadow(false);

        mChart.setDrawValueAboveBar(true);

        mChart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        // draw shadows for each bar that show the maximum value
        // mChart.setDrawBarShadow(true);

        mChart.setDrawGridBackground(false);

        XAxis xl = mChart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setTypeface(mTfLight);
        xl.setDrawAxisLine(true);
        xl.setDrawGridLines(false);
        xl.setLabelRotationAngle(-20);
        xl.setGranularity(1f);

        YAxis yl = mChart.getAxisLeft();
        yl.setTypeface(mTfLight);
        yl.setDrawAxisLine(true);
        yl.setDrawGridLines(true);
        yl.setAxisMinimum(reportData.getMinScore());
//        yl.setInverted(true);

        YAxis yr = mChart.getAxisRight();
        yr.setTextColor(Color.parseColor("#f7f7f7"));
        yr.setTypeface(mTfLight);
        yr.setDrawAxisLine(true);
        yr.setDrawGridLines(false);
        yr.setAxisLineColor(Color.parseColor("#f7f7f7"));
        yr.setAxisMinimum(0f); // this replaces setStartAtZero(true)
//        yr.setInverted(true);

        setData();

        MyMarkerView mv = new MyMarkerView(context, R.layout.custom_marker_view);
        mv.setChartView(mChart);
        mv.setxLabels(xLabels);
        mChart.setMarker(mv);

        mChart.setFitBars(true);
        mChart.animateY(500);

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setFormSize(8f);
        l.setXEntrySpace(4f);
    }

    private void setData() {


        if(reportData==null || reportData.getItems()==null || reportData.getItems().size()==0){
            return;
        }
        List<ReportFactorEntity> items = reportData.getItems();
        xLabels = new ArrayList<>();
        ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();
        for(int i=0;i<items.size();i++){
            ReportFactorEntity rfe = items.get(i);
            xLabels.add(rfe.getItemName());
//            if(rfe.getChildScore()<0){
//                yValues.add(new BarEntry(i,new float[]{(float) rfe.getChildScore() , 0}));
//            }else{
//                yValues.add(new BarEntry(i,new float[]{0 , (float) rfe.getChildScore()}));
//            }
            yValues.add(new BarEntry(i,(float) rfe.getChildScore()));
        }

        mChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xLabels));

        BarDataSet set1 = new BarDataSet(yValues, "我");
        set1.setDrawIcons(false);
        set1.setColors(Color.parseColor("#12b2f4"));

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        data.setValueTextSize(10f);
        data.setValueTypeface(mTfLight);
        data.setBarWidth(0.5f);
        mChart.setData(data);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;

        RectF bounds = mOnValueSelectedRectF;
        mChart.getBarBounds((BarEntry) e, bounds);

        MPPointF position = mChart.getPosition(e, mChart.getData().getDataSetByIndex(h.getDataSetIndex())
                .getAxisDependency());

        Log.i("bounds", bounds.toString());
        Log.i("position", position.toString());

        MPPointF.recycleInstance(position);
    }

    @Override
    public void onNothingSelected() {
    };
}
