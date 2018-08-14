package com.cheersmind.smartbrain.mpcharts;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

import com.cheersmind.smartbrain.R;
import com.cheersmind.smartbrain.main.entity.ReportFactorEntity;
import com.cheersmind.smartbrain.main.entity.ReportItemEntity;
import com.cheersmind.smartbrain.mpcharts.Formmart.MyMarkerView;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/8/2.
 */

public class MPLineChart extends MPBaseChart implements OnChartValueSelectedListener{

    private LineChart mChart;
    private List<String> xLabels;

    public MPLineChart(Context context, ReportItemEntity reportData) {
        super(context,reportData);
    }

    public MPLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MPLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void initChart() {
        super.initChart();
        mChart = new LineChart(context);
        LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        this.addView(mChart,params);

        mChart.setOnChartValueSelectedListener(this);

        // no description text
        mChart.getDescription().setEnabled(false);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        mChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.parseColor("#f7f7f7"));

        // add data
        setData();

        MyMarkerView mv = new MyMarkerView(context, R.layout.custom_marker_view);
        mv.setChartView(mChart); // For bounds control
        mv.setxLabels(xLabels);
        mChart.setMarker(mv); // Set the marker to the chart

        //显示的时候是按照多大的比率缩放显示,1f表示不放大缩小
//        mChart.zoom(1.2f,1f,0,0);

        mChart.animateXY(500,500, Easing.EasingOption.EaseInSine, Easing.EasingOption.EaseInSine);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTypeface(mTfLight);
        l.setTextSize(11f);
        l.setTextColor(Color.parseColor("#666666"));
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
//        l.setYOffset(11f);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setTypeface(mTfLight);
        xAxis.setTextSize(11f);
        xAxis.setTextColor(Color.parseColor("#666666"));
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawLabels(true);//绘制标签  指x轴上的对应数值
        xAxis.setLabelRotationAngle(-20);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置x轴的显示位置
        xAxis.setGranularity(1f);
//        xAxis.setAvoidFirstLastClipping(true);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(mTfLight);
        leftAxis.setTextColor(Color.parseColor("#666666"));
        leftAxis.setAxisMaximum(reportData.getMaxScore());
        leftAxis.setAxisMinimum(reportData.getMinScore());
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setXOffset(20);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setTypeface(mTfLight);
        rightAxis.setTextColor(Color.parseColor("#f7f7f7"));
        rightAxis.setAxisLineColor(Color.parseColor("#f7f7f7"));
        rightAxis.setAxisMaximum(reportData.getMaxScore());
        rightAxis.setAxisMinimum(reportData.getMinScore());
        rightAxis.setDrawGridLines(true);
        rightAxis.setGranularityEnabled(true);
        rightAxis.setXOffset(20);
//        rightAxis.setEnabled(false);//设置图表右边的y轴禁用

    }

    private void setData() {
        if(reportData==null || reportData.getItems()==null || reportData.getItems().size()==0){
            return;
        }
        List<ReportFactorEntity> items = reportData.getItems();
        xLabels = new ArrayList<>();

        int chartCounts = 1;
        if(items.get(0).getCompareScore()>0){
            chartCounts = 2;
        }

        List<ILineDataSet> dataSets = new ArrayList<>();
        for(int j=0;j<chartCounts;j++){
            ArrayList<Entry> yValues = new ArrayList<Entry>();
            for(int i=0;i<items.size();i++){
                ReportFactorEntity rfe = items.get(i);
                if(j == 0){
                    xLabels.add(rfe.getItemName());
                    yValues.add(new Entry(i, (float) rfe.getChildScore()));
                }else if(j == 1){
                    yValues.add(new Entry(i, (float) rfe.getCompareScore()));
                }
            }

            LineDataSet set1;

            if(j == 0){
                set1 = new LineDataSet(yValues, "我");
                set1.setAxisDependency(YAxis.AxisDependency.LEFT);
                set1.setColor(ColorTemplate.getHoloBlue());
                set1.setCircleColor(ColorTemplate.getHoloBlue());
                set1.setLineWidth(3f);
                set1.setCircleRadius(4f);
                set1.setFillAlpha(65);
                set1.setFillColor(ColorTemplate.getHoloBlue());
                set1.setHighLightColor(Color.rgb(244, 117, 117));
                set1.setDrawCircleHole(false);
            }else{
                set1 = new LineDataSet(yValues, "全国");
                set1.setAxisDependency(YAxis.AxisDependency.LEFT);
                set1.setColor(ColorTemplate.getHoloBlue());
                set1.setCircleColor(ColorTemplate.getHoloBlue());
                set1.setLineWidth(3f);
                set1.setCircleRadius(4f);
                set1.setFillAlpha(65);
                set1.setFillColor(ColorTemplate.getHoloBlue());
                set1.setHighLightColor(Color.rgb(244, 117, 117));
                set1.setDrawCircleHole(false);
            }
            dataSets.add(set1);


        }

        mChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xLabels));

        // create a data object with the datasets
        LineData data = new LineData(dataSets);
        data.setValueTextColor(Color.parseColor("#666666"));
        data.setValueTextSize(9f);
        // set data
        mChart.setData(data);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());

//        mChart.centerViewToAnimated(e.getX(), e.getY(), mChart.getData().getDataSetByIndex(h.getDataSetIndex())
//                .getAxisDependency(), 300);
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }
}
