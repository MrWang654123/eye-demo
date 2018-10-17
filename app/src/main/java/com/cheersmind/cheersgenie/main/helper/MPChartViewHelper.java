package com.cheersmind.cheersgenie.main.helper;

import android.content.Context;
import android.widget.LinearLayout;

import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.main.entity.ReportFactorEntity;
import com.cheersmind.cheersgenie.main.entity.ReportItemEntity;
import com.cheersmind.cheersgenie.main.util.DensityUtil;
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
//            int height = chartType == MPChartType.RADARCHARTVIEW.getType() ? 300 : 260;
//            int height = 300;
            //获取图表的高度
            int height = adjustHeightByXLabels(reportItemEntity);
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

    //x轴文本数量满多少个开始微调
    public static final int ADJUST_SIZE_1 = 1;
    public static final int ADJUST_SIZE_2 = 2;
    public static final int ADJUST_SIZE_3 = 6;
    //x轴文本的最大长度达到都少开始微调：第一档
    public static final int ADJUST_MAX_LENGTH_1 = 8;
    //x轴文本的最大长度达到都少开始微调：第二档
    public static final int ADJUST_MAX_LENGTH_2 = 12;

    /**
     * 获取图表的高度
     * @param reportItemEntity
     * @return
     */
    private static int adjustHeightByXLabels(ReportItemEntity reportItemEntity) {
        int height = 260;
        if (reportItemEntity != null) {
            int chartType = reportItemEntity.getChartType();
            //雷达图
            if (chartType == MPChartType.RADARCHARTVIEW.getType()) {
                height = 330;

            } else if (chartType == MPChartType.BARHCHARTVIEW.getType()) {//水平条状图
//                height = 260;

            } else {//其他：柱状图和曲线图
                List<String> xLabels = getXLabels(reportItemEntity);
                //非空
                if (ArrayListUtil.isNotEmpty(xLabels)) {
                    //X轴坐标数量大于5，且最大文本长度大于7，则X轴文本旋转30度
                    if (xLabels.size() >= ADJUST_SIZE_3) {
                        //x轴文本的最大长度
                        int maxLen = getXLabelsMaxLength(xLabels);
                        if (maxLen >= ADJUST_MAX_LENGTH_2) {
                            height = 350;

                        } else if (maxLen >= ADJUST_MAX_LENGTH_1) {
                            height = 300;
                        }
                    }
                }
            }
        }

        return height;
    }

    /**
     * 获取x轴坐标文本集合
     * @param reportItemEntity 报告数据集合
     * @return
     */
    private static List<String> getXLabels(ReportItemEntity reportItemEntity) {
        List<String> xLabels = new ArrayList<>();

        if (reportItemEntity != null && reportItemEntity.getItems() != null) {
            List<ReportFactorEntity> items = reportItemEntity.getItems();

            for (int i = 0; i < items.size(); i++) {
                ReportFactorEntity rfe = items.get(i);
                xLabels.add(rfe.getItemName());
            }
        }

        return xLabels;
    }

    /**
     * 获取x轴文本的最大长度
     * @param xLabels X轴文章集合
     * @return
     */
    private static int getXLabelsMaxLength(List<String> xLabels) {
        if (xLabels == null) {
            return 0;
        }

        //最大长度
        int maxLen = 0;
        for (String label : xLabels) {
            if (label.length() > maxLen) {
                maxLen = label.length();
            }
        }

        return maxLen;
    }

}
