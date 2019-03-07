package com.cheersmind.cheersgenie.features.utils;

import android.content.Context;

import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.entity.ChartItem;
import com.cheersmind.cheersgenie.features.entity.HBarChartItem;
import com.cheersmind.cheersgenie.features.entity.LineChartItem;
import com.cheersmind.cheersgenie.features.entity.RadarChartItem;
import com.cheersmind.cheersgenie.features.entity.VBarChartItem;
import com.cheersmind.cheersgenie.features.entity.ChartCompareItem;
import com.cheersmind.cheersgenie.features.entity.ChartScoreItem;
import com.cheersmind.cheersgenie.main.entity.ReportItemEntity;

import java.util.List;

/**
 * 图表工具
 */
public class ChartUtil {

    /**
     * 报告项转图表项
     * @param context 上下文
     * @param chartType 图表类型
     * @param reportItem 报告项
     * @return 图表项
     */
    public static ChartItem reportItemToChartItem(Context context, int chartType, ReportItemEntity reportItem) {
        ChartItem chartItem;

        if (reportItem == null || ArrayListUtil.isEmpty(reportItem.getItems())) {
            return null;
        }

        //分数项数量
        int scoreItemSize = 0;
        List<ChartCompareItem> compareItems = reportItem.getItems();
        for (ChartCompareItem compareItem : compareItems) {
            List<ChartScoreItem> scoreItems = compareItem.getScoreItems();
            if (ArrayListUtil.isNotEmpty(scoreItems)) {
                scoreItemSize = scoreItems.size();
                break;
            }
        }

        //测试
//        chartItem = new RadarChartItem(context).generateChartData(reportItem);

        if(chartType == Dictionary.CHART_RADAR){
            chartItem = new RadarChartItem(context).generateChartData(reportItem);

        }else if(chartType == Dictionary.CHART_LINE){
            if(scoreItemSize <= 2){
                chartItem = new VBarChartItem(context).generateChartData(reportItem);
            }else{
                chartItem = new LineChartItem(context).generateChartData(reportItem);
            }
        }else if(chartType == Dictionary.CHART_BAR_V){
            chartItem = new VBarChartItem(context).generateChartData(reportItem);

        }else if(chartType == Dictionary.CHART_BAR_H){
            chartItem = new HBarChartItem(context).generateChartData(reportItem);
        }else{
            chartItem = new LineChartItem(context).generateChartData(reportItem);
        }

        return chartItem;
    }

}
