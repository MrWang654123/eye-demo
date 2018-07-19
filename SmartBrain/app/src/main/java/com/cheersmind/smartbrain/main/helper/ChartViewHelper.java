package com.cheersmind.smartbrain.main.helper;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.cheersmind.smartbrain.R;
import com.cheersmind.smartbrain.main.entity.ReportItemEntity;
import com.cheersmind.smartbrain.main.util.DensityUtil;
import com.cheersmind.smartbrain.xclcharts.DemoView;
import com.cheersmind.smartbrain.xclcharts.QsBarHChartView;
import com.cheersmind.smartbrain.xclcharts.QsBarVChartView;
import com.cheersmind.smartbrain.xclcharts.QsRadarChartView;
import com.cheersmind.smartbrain.xclcharts.QsSplineChartView;

import java.util.List;

/**
 * 图表管理
 * 图表类型，1-雷达图，2-曲线图，3-柱状图，4-条状图
 * Created by Administrator on 2018/6/30.
 */

public class ChartViewHelper {

    public enum ChartType{
        RADARCHARTVIEW(1),
        SPLINECHARTVIEW(2),
        BARVCHARTVIEW(3),
        BARHCHARTVIEW(4);

        private int type;
        ChartType(int type) {
            this.type = type;
        }

        public int getType(){
            return type;
        }
    }

    public static DemoView addChartView(Context context, RelativeLayout rtLayout, ReportItemEntity reportItemEntity,int curSelect){
        rtLayout.removeAllViews();
        DemoView demoView;
        int chartType = reportItemEntity.getChartType();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);

        if(chartType == ChartType.RADARCHARTVIEW.getType()){
            demoView = new QsRadarChartView(context,reportItemEntity);
        }else if(chartType == ChartType.SPLINECHARTVIEW.getType()){
            if(reportItemEntity.getItems().size()<=2){
                demoView = new QsBarVChartView(context,reportItemEntity,curSelect);
            }else{
                demoView = new QsSplineChartView(context,reportItemEntity,curSelect);
            }
        }else if(chartType == ChartType.BARVCHARTVIEW.getType()){
            demoView = new QsBarVChartView(context,reportItemEntity,curSelect);
        }else if(chartType == ChartType.BARHCHARTVIEW.getType()){
            demoView = new QsBarHChartView(context,reportItemEntity);
        }else{
            demoView = new QsSplineChartView(context,reportItemEntity,curSelect);
        }

        rtLayout.addView(demoView,params);
        startChartAnimation(context,demoView);

        return demoView;

    }

    public static void addChartView(Context context, LinearLayout rtLayout, List<ReportItemEntity> reportItemEntities){
        rtLayout.removeAllViews();
        DemoView demoView;
        for(int i=0;i<reportItemEntities.size();i++){
            ReportItemEntity reportItemEntity = reportItemEntities.get(i);
            int chartType = reportItemEntity.getChartType();
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(context,260));
            params.setMargins(0,-DensityUtil.dip2px(context,40),0,0);
            if(chartType == ChartType.RADARCHARTVIEW.getType()){
                demoView = new QsRadarChartView(context,reportItemEntity);
            }else if(chartType == ChartType.SPLINECHARTVIEW.getType()){
                if(reportItemEntity.getItems().size()<=2){
                    demoView = new QsBarVChartView(context,reportItemEntity,0);
                }else{
                    demoView = new QsSplineChartView(context,reportItemEntity,0);
                }
            }else if(chartType == ChartType.BARVCHARTVIEW.getType()){
                demoView = new QsBarVChartView(context,reportItemEntity,0);
            }else if(chartType == ChartType.BARHCHARTVIEW.getType()){
                demoView = new QsBarHChartView(context,reportItemEntity);
            }else{
                demoView = new QsSplineChartView(context,reportItemEntity,0);
            }

            rtLayout.addView(demoView,params);
            startChartAnimation(context,demoView);
        }

    }

    private static void startChartAnimation(Context context,View view){
        Animation showAnim = AnimationUtils.loadAnimation(
                context, R.anim.anim_scale_center);
        showAnim.setDuration(300);
        view.startAnimation(showAnim);
    }

//    public static void addStageForBottom(Context context,ReportItemEntity topicReports,RelativeLayout rtChart,List<TextView> textViews){
//        if(topicReports!=null){
//            View view = View.inflate(context,R.layout.fragment_report_stage,null);
//            LinearLayout stageLayout = (LinearLayout)view.findViewById(R.id.ll_stage);
//            int size = topicReports.getItems().size() + 1;
//            for(int i=0;i<size;i++){
//                LinearLayout linearLayout = new LinearLayout(context);
//                LinearLayout.LayoutParams paramRoot = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//                        DensityUtil.dip2px(context,30));
//                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
//                linearLayout.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
//                paramRoot.weight = 1;
//
//                TextView tv = new TextView(context);
//                tv.setTextColor(Color.parseColor("#666666"));
//                tv.setTextSize(12);
//                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams( DensityUtil.dip2px(context,20),
//                        DensityUtil.dip2px(context,20));
//                tv.setGravity(Gravity.CENTER);
//
//                if(i== size-1){
//                    tv.setText("");
//                }else{
//                    tv.setText(String.valueOf(i+1));
//                }
//
//                if(i==0){
//                    tv.setBackgroundResource(R.mipmap.qs_circle_bg_selete);
//                }else {
//                    if(i!=size-1){
//                        tv.setBackgroundResource(R.mipmap.qs_circle_bg);
//                    }
//                }
//                textViews.add(tv);
//                linearLayout.addView(tv,param);
//                stageLayout.addView(linearLayout,paramRoot);
//            }
//            rtChart.addView(view);
//        }
//    }
}
