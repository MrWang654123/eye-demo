package com.cheersmind.cheersgenie.features.entity;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.main.entity.ReportFactorEntity;
import com.cheersmind.cheersgenie.main.entity.ReportItemEntity;
import com.cheersmind.cheersgenie.mpcharts.Formmart.MyMarkerView;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

import static com.cheersmind.cheersgenie.features.constant.Dictionary.CHART_LINE;

/**
 * 曲线图数据
 */
public class LineChartItem extends ChartItem {

    private LineChart mChart;


    public LineChartItem(Context context) {
        super(context);
    }

    public LineChartItem(Context context, ChartData<?> cd) {
        super(context, cd);
    }

    @Override
    public int getItemType() {
        return CHART_LINE;
    }

    @Override
    public void initChart(View chartView) {
        super.initChart(chartView);

        if (chartView == null) {
            return;
        }

        mChart = (LineChart) chartView;

//        mChart.setOnChartValueSelectedListener(this);

        // no description text
        mChart.getDescription().setEnabled(false);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        mChart.setDragDecelerationFrictionCoef(0.9f);

        //是否可以拖动
        mChart.setDragEnabled(true);
        //是否可以缩放
        mChart.setScaleEnabled(true);
        //是否显示边界
        mChart.setDrawBorders(false);
        //是否展示网格线
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.parseColor("#f7f7f7"));

        // add data
//        setData();

        //X轴文本旋转角度
        mChart.getXAxis().setLabelRotationAngle(xLabelRotationAngle);

        //X轴文本格式化
        mChart.getXAxis().setValueFormatter(iAxisValueFormatter);

        // create a data object with the datasets
//        data.setValueTextColor(Color.parseColor("#333333"));
        mChartData.setValueTextSize(8f);
        mChartData.setValueTypeface(mTfLight);


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

        //显示的时候是按照多大的比率缩放显示,1f表示不放大缩小
//        mChart.zoom(1.2f,1f,0,0);

//        mChart.animateXY(500,500, Easing.EasingOption.EaseInSine, Easing.EasingOption.EaseInSine);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTypeface(mTfLight);
        l.setTextSize(11f);
        l.setFormSize(9f);
        l.setTextColor(Color.parseColor("#666666"));
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
//        l.setYOffset(-10f);
        l.setXEntrySpace(10f);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setTypeface(mTfLight);
        xAxis.setTextSize(11f);
        //x轴文本颜色
        xAxis.setTextColor(Color.parseColor(axisTextColor));
        //x轴颜色
        xAxis.setAxisLineColor(Color.parseColor(xAxisColor));
        //x轴宽度
        xAxis.setAxisLineWidth(axisLineWidth);
        //网格线
        xAxis.setDrawGridLines(false);
        //轴线
        xAxis.setDrawAxisLine(true);
        //X轴标签文本
        xAxis.setDrawLabels(true);
        //X轴文本旋转角度
//        xAxis.setLabelRotationAngle(-20);
        //设置x轴的显示位置（显示在底部）
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
//        xAxis.setAvoidFirstLastClipping(true);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(mTfLight);
        //y轴颜色
        leftAxis.setTextColor(Color.parseColor(axisTextColor));
        //y轴颜色
        leftAxis.setAxisLineColor(Color.parseColor(yAxisColor));
        //y轴宽度
        leftAxis.setAxisLineWidth(axisLineWidth);
//        List<Float> maxAndMinValue = getMaxAndMinValue();
        //最大值
//        leftAxis.setAxisMaximum(reportData.getMaxScore());
//        leftAxis.setAxisMaximum(getMaxAndMinValue().get(0));
        //最小值
        leftAxis.setAxisMinimum(reportData.getMinScore());
//        leftAxis.setAxisMaximum(getMaxAndMinValue().get(1));
        //网格线
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        //水平偏移
        leftAxis.setXOffset(20);
        //设置X Y轴网格线为虚线（实体线长度、间隔距离、偏移量：通常使用 0）
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        //格式化Y轴文本
//        leftAxis.setValueFormatter(new IAxisValueFormatter() {
//            @Override
//            public String getFormattedValue(float value, AxisBase axis) {
//                return ((int) (value * 100)) + "%";
//            }
//        });
        //将Y轴分为 8份
//        leftAxis.setLabelCount(8);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setTypeface(mTfLight);
        rightAxis.setTextColor(Color.parseColor("#f7f7f7"));
        rightAxis.setAxisLineColor(Color.parseColor("#f7f7f7"));
//        rightAxis.setAxisMaximum(reportData.getMaxScore());
        rightAxis.setAxisMinimum(reportData.getMinScore());
        rightAxis.setDrawGridLines(false);
        rightAxis.setGranularityEnabled(true);
        rightAxis.setXOffset(20);
//        rightAxis.setEnabled(false);//设置图表右边的y轴禁用

        mChart.setData((LineData) mChartData);
//        mChart.invalidate();
        mChart.animateX(500);
    }


    @Override
    public ChartItem generateChartData(ReportItemEntity reportData) {
        if(reportData==null || reportData.getItems()==null || reportData.getItems().size()==0){
            return this;
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
                set1.setColor(Color.parseColor(dataSetColor_1));
                set1.setCircleColor(Color.parseColor(dataSetColor_1));
                set1.setCircleColorHole(Color.parseColor(holeColor));
                set1.setLineWidth(1.5f);
                set1.setCircleRadius(3.5f);
                set1.setCircleHoleRadius(2f);
                set1.setFillAlpha(65);
//                set1.setFillColor(ColorTemplate.getHoloBlue());
                set1.setHighLightColor(Color.parseColor(dataSetColor_1));
                set1.setDrawCircleHole(true);
            }else{
                set1 = new LineDataSet(yValues, reportData.getCompareName());
                set1.setAxisDependency(YAxis.AxisDependency.LEFT);
                set1.setColor(Color.parseColor(dataSetColor_2));
                set1.setCircleColor(Color.parseColor(dataSetColor_2));
                set1.setCircleColorHole(Color.parseColor(holeColor));
                set1.setLineWidth(1.5f);
                set1.setCircleRadius(3.5f);
                set1.setCircleHoleRadius(2f);
                set1.setFillAlpha(65);
//                set1.setFillColor(ColorTemplate.getHoloBlue());
                set1.setHighLightColor(Color.parseColor(dataSetColor_2));
                set1.setDrawCircleHole(true);
            }

            //曲线模式
            set1.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

            dataSets.add(set1);

        }

        mChartData = new LineData(dataSets);

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

