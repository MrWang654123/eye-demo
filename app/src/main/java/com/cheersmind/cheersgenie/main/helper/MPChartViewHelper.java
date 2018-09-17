package com.cheersmind.cheersgenie.main.helper;

import android.content.Context;
import android.widget.LinearLayout;

import com.cheersmind.cheersgenie.main.entity.ReportItemEntity;
import com.cheersmind.cheersgenie.main.util.DensityUtil;
import com.cheersmind.cheersgenie.mpcharts.MPBaseChart;
import com.cheersmind.cheersgenie.mpcharts.MPHorizontalBarChart;
import com.cheersmind.cheersgenie.mpcharts.MPLineChart;
import com.cheersmind.cheersgenie.mpcharts.MPRadarChart;
import com.cheersmind.cheersgenie.mpcharts.MPVerticalBarChart;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/8/1.
 */

public class MPChartViewHelper {

    public static final String REPORT_RELATION_TOPIC = "topic";
//    public static final String REPORT_RELATION_DIMENSION = "dimension";
    public static final String REPORT_RELATION_TOPIC_DIMENSION = "topic_dimension";

    public enum MPChartType{
        //网状图
        RADARCHARTVIEW(1),
        //曲线图
        SPLINECHARTVIEW(2),
        //柱状图
        BARVCHARTVIEW(3),
        //水平条状图
        BARHCHARTVIEW(4);

        private int type;
        MPChartType(int type) {
            this.type = type;
        }

        public int getType(){
            return type;
        }
    }

    public static void addMpChartView(Context context, LinearLayout layout, List<ReportItemEntity> reportItemEntities){

        for(int i=0;i<reportItemEntities.size();i++){
            ReportItemEntity reportItemEntity = reportItemEntities.get(i);
            int chartType = reportItemEntity.getChartType();
            //图表高度，雷达图高度比其他的高一些（方便展示）
            int height = chartType == MPChartType.RADARCHARTVIEW.getType() ? 300 : 260;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    DensityUtil.dip2px(context,height));
            LinearLayout chartLayout;
            if(chartType == MPChartType.RADARCHARTVIEW.getType()){
                chartLayout = new MPRadarChart(context,reportItemEntity);
            }else if(chartType == MPChartType.SPLINECHARTVIEW.getType()){
                if(reportItemEntity.getItems().size()<=2){
                    chartLayout = new MPVerticalBarChart(context,reportItemEntity);
                }else{
                    chartLayout = new MPLineChart(context,reportItemEntity);
                }
            }else if(chartType == MPChartType.BARVCHARTVIEW.getType()){
                chartLayout = new MPVerticalBarChart(context,reportItemEntity);
            }else if(chartType == MPChartType.BARHCHARTVIEW.getType()){
                chartLayout = new MPHorizontalBarChart(context,reportItemEntity);
            }else{
                chartLayout = new MPLineChart(context,reportItemEntity);
            }

            layout.addView(chartLayout,params);
        }
    }

    /**
     * 返回图表集合
     * @param context
     * @param reportItemEntities
     * @return
     */
    public static ArrayList<LinearLayout> getMpChartViews(Context context, List<ReportItemEntity> reportItemEntities){
        ArrayList<LinearLayout> mpBaseCharts = new ArrayList<>();

        for(int i=0;i<reportItemEntities.size();i++){
            ReportItemEntity reportItemEntity = reportItemEntities.get(i);
            int chartType = reportItemEntity.getChartType();
            //图表高度，雷达图高度比其他的高一些（方便展示）
            int height = chartType == MPChartType.RADARCHARTVIEW.getType() ? 300 : 260;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    DensityUtil.dip2px(context,height));
            LinearLayout chartLayout;
            if(chartType == MPChartType.RADARCHARTVIEW.getType()){
                chartLayout = new MPRadarChart(context,reportItemEntity);
            }else if(chartType == MPChartType.SPLINECHARTVIEW.getType()){
                if(reportItemEntity.getItems().size()<=2){
                    chartLayout = new MPVerticalBarChart(context,reportItemEntity);
                }else{
                    chartLayout = new MPLineChart(context,reportItemEntity);
                }
            }else if(chartType == MPChartType.BARVCHARTVIEW.getType()){
                chartLayout = new MPVerticalBarChart(context,reportItemEntity);
            }else if(chartType == MPChartType.BARHCHARTVIEW.getType()){
                chartLayout = new MPHorizontalBarChart(context,reportItemEntity);
            }else{
                chartLayout = new MPLineChart(context,reportItemEntity);
            }

//            layout.addView(chartLayout,params);
            mpBaseCharts.add(chartLayout);
        }

        return mpBaseCharts;
    }

}
