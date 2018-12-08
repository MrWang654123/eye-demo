package com.cheersmind.cheersgenie.features.entity;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

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
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.List;

import static com.cheersmind.cheersgenie.features.constant.Dictionary.CHART_BAR_H;

/**
 * 水平条状图数据
 */
public class HBarChartItem extends ChartItem {

    private HorizontalBarChart mChart;

    public HBarChartItem(Context context) {
        super(context);
    }

    public HBarChartItem(Context context, ChartData<?> cd) {
        super(context, cd);
    }

    @Override
    public int getItemType() {
        return CHART_BAR_H;
    }

    @Override
    public void initChart(View chartView) {
        super.initChart(chartView);

        if (chartView == null) {
            return;
        }

        mChart = (HorizontalBarChart) chartView;

//        mChart.setOnChartValueSelectedListener(this);
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

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(mTfLight);
        //x轴文本颜色
        xAxis.setTextColor(Color.parseColor(axisTextColor));
        //x轴颜色
        xAxis.setAxisLineColor(Color.parseColor(xAxisColor));
        //x轴宽度
        xAxis.setAxisLineWidth(axisLineWidth);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
//        xAxis.setLabelRotationAngle(-20);
        xAxis.setGranularity(1f);
//        //上侧还有部分图表未展示出来，此时还需要对X轴进行相应的设置
//        xl.setAxisMinimum(0f);
        //放到setData之后
//        xAxis.setAxisMaximum(xValues.size());
//        //将X轴的值显示在中央
//        xl.setCenterAxisLabels(true);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.parseColor("#f7f7f7"));
        leftAxis.setTypeface(mTfLight);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(getMaxAndMinValue().get(1));
        //设置X Y轴网格线为虚线（实体线长度、间隔距离、偏移量：通常使用 0）
//        yl.enableGridDashedLine(10f, 10f, 0f);
//        yl.setInverted(true);

        YAxis yr = mChart.getAxisRight();
//        yr.setTextColor(Color.parseColor("#f7f7f7"));
        yr.setTypeface(mTfLight);
        //y轴颜色
        yr.setTextColor(Color.parseColor(axisTextColor));
        //y轴颜色
        yr.setAxisLineColor(Color.parseColor(yAxisColor));
        //y轴宽度
        yr.setAxisLineWidth(axisLineWidth);
        yr.setDrawAxisLine(true);
        yr.setDrawGridLines(true);
//        yr.setAxisLineColor(Color.parseColor("#f7f7f7"));
//        yr.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        yr.setAxisMinimum(getMaxAndMinValue().get(1));
        //设置X Y轴网格线为虚线（实体线长度、间隔距离、偏移量：通常使用 0）
        yr.enableGridDashedLine(10f, 10f, 0f);
//        yr.setInverted(true);

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
//        data.setBarWidth(0.5f);
        if (dataSets.size() == 1) {
            ((BarData)mChartData).setBarWidth(0.5f);
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
            ((BarData)mChartData).setBarWidth(barWidth);
//(起始点、柱状图组间距、柱状图之间间距)
            ((BarData)mChartData).groupBars(0f, groupSpace, barSpace);
        }


//        xl.setAxisMaximum(xLabels.size());


        mChart.setFitBars(true);
//        mChart.animateY(500);

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setFormSize(8f);
        l.setXEntrySpace(4f);

        mChart.setData((BarData) mChartData);
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
                set1.setColors(Color.parseColor(dataSetColor_1));
            }else{
                set1 = new BarDataSet(yValues, reportData.getCompareName());
                set1.setDrawIcons(false);
                set1.setColors(Color.parseColor(dataSetColor_2));
            }
            dataSets.add(set1);
        }

        mChartData = new BarData(dataSets);

        return super.generateChartData(reportData);
    }


    /**
     * x轴文本旋转角度
     */
    protected void onXLabelRotationAngle() {
        if (xLabels != null && xLabelRotationAngle == -1) {
            //X轴文本旋转角度
            xLabelRotationAngle = -20;
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
