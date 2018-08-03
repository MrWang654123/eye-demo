package com.cheersmind.smartbrain.main.helper;

import android.content.Context;
import android.widget.LinearLayout;

import com.cheersmind.smartbrain.main.entity.ReportItemEntity;
import com.cheersmind.smartbrain.main.util.DensityUtil;
import com.cheersmind.smartbrain.mpcharts.MPHorizontalBarChart;
import com.cheersmind.smartbrain.mpcharts.MPLineChart;
import com.cheersmind.smartbrain.mpcharts.MPRadarChart;
import com.cheersmind.smartbrain.mpcharts.MPVerticalBarChart;

import java.util.List;

/**
 * Created by Administrator on 2018/8/1.
 */

public class MPChartViewHelper {

//    public static final String REPORT_RELATION_TOPIC = "topic";
//    public static final String REPORT_RELATION_DIMENSION = "dimension";
//    public static final String REPORT_RELATION_TOPIC_DIMENSION = "topic_dimension";

    public enum MPChartType{
        RADARCHARTVIEW(1),
        SPLINECHARTVIEW(2),
        BARVCHARTVIEW(3),
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
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(context,260));
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
}
