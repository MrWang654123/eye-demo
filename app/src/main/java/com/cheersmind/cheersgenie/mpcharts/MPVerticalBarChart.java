package com.cheersmind.cheersgenie.mpcharts;

import android.content.Context;
import android.graphics.Color;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

import com.cheersmind.cheersgenie.R;
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

public class MPVerticalBarChart extends MPBaseChart implements OnChartValueSelectedListener {

    private BarChart mChart;

    private List<String> xLabels;

    protected RectF mOnValueSelectedRectF = new RectF();

    public MPVerticalBarChart(Context context, ReportItemEntity reportData) {
        super(context,reportData);
    }

    public MPVerticalBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MPVerticalBarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void initChart() {
        super.initChart();
        mChart = new BarChart(context);
        LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        this.addView(mChart,params);

        mChart.setOnChartValueSelectedListener(this);

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

        setData();

//        xAxis.setAxisMaximum(xLabels.size());

        leftAxis.setAxisMinimum(reportData.getMinScore());
        MyMarkerView mv = new MyMarkerView(context, R.layout.custom_marker_view);
        mv.setChartView(mChart);
        mv.setxLabels(xLabels);
        mChart.setMarker(mv);
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

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        for(int j=0;j<chartCounts;j++) {
            ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();
            for (int i = 0; i < items.size(); i++) {
                ReportFactorEntity rfe = items.get(i);
                if (j == 0) {
                    xLabels.add(rfe.getItemName());
                    yValues.add(new BarEntry(i, (float) rfe.getChildScore()));
                } else if (j == 1) {
                    yValues.add(new BarEntry(i, (float) rfe.getCompareScore()));
                }
            }

            BarDataSet set1;
            if(j == 0){
                set1 = new BarDataSet(yValues, "我");
                set1.setDrawIcons(false);
                set1.setColors(Color.parseColor("#12b2f4"));
            }else{
                set1 = new BarDataSet(yValues, "全国");
                set1.setDrawIcons(false);
                set1.setColors(Color.parseColor("#ffa400"));
            }
            dataSets.add(set1);
        }

        mChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xLabels));

        BarData data = new BarData(dataSets);
        data.setValueTextSize(10f);
        data.setValueTypeface(mTfLight);
        if (dataSets.size() == 1) {
            data.setBarWidth(0.5f);
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
            data.setBarWidth(barWidth);
//(起始点、柱状图组间距、柱状图之间间距)
            data.groupBars(0f, groupSpace, barSpace);
        }

        mChart.setData(data);

        mChart.animateY(500);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;

        RectF bounds = mOnValueSelectedRectF;
        mChart.getBarBounds((BarEntry) e, bounds);
        MPPointF position = mChart.getPosition(e, YAxis.AxisDependency.LEFT);

        Log.i("bounds", bounds.toString());
        Log.i("position", position.toString());

        Log.i("x-index",
                "low: " + mChart.getLowestVisibleX() + ", high: "
                        + mChart.getHighestVisibleX());

        MPPointF.recycleInstance(position);
    }

    @Override
    public void onNothingSelected() { }
}
