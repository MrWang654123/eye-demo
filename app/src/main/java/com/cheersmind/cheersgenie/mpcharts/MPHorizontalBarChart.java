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
//        //上侧还有部分图表未展示出来，此时还需要对X轴进行相应的设置
//        xl.setAxisMinimum(0f);
        //放到setData之后
//        xAxis.setAxisMaximum(xValues.size());
//        //将X轴的值显示在中央
//        xl.setCenterAxisLabels(true);

        YAxis yl = mChart.getAxisLeft();
        yl.setTypeface(mTfLight);
        yl.setDrawAxisLine(true);
        yl.setDrawGridLines(true);
        yl.setAxisMinimum(getMaxAndMinValue().get(1));
        //设置X Y轴网格线为虚线（实体线长度、间隔距离、偏移量：通常使用 0）
        yl.enableGridDashedLine(10f, 10f, 0f);
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

//        xl.setAxisMaximum(xLabels.size());

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
        l.setDrawInside(true);
        l.setFormSize(8f);
        l.setXEntrySpace(4f);
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
//        data.setBarWidth(0.5f);
        if (dataSets.size() == 1) {
            data.setBarWidth(0.5f);
        } else {
            //上侧还有部分图表未展示出来，此时还需要对X轴进行相应的设置
            mChart.getXAxis().setAxisMinimum(0f);
            mChart.getXAxis().setAxisMaximum(xLabels.size());
            //将X轴的值显示在中央
            mChart.getXAxis().setCenterAxisLabels(true);

            //由堆积模式变为并排多列模式
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

        mChart.animateX(500);
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
