package com.cheersmind.cheersgenie.mpcharts;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.cheersmind.cheersgenie.main.entity.ReportFactorEntity;
import com.cheersmind.cheersgenie.main.entity.ReportItemEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/8/2.
 */

public class MPBaseChart extends LinearLayout {

    protected Context context;
    protected ReportItemEntity reportData;

    protected String[] mMonths = new String[] {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
    };

    protected String[] mParties = new String[] {
            "Party A", "Party B", "Party C", "Party D", "Party E", "Party F", "Party G", "Party H",
            "Party I", "Party J", "Party K", "Party L", "Party M", "Party N", "Party O", "Party P",
            "Party Q", "Party R", "Party S", "Party T", "Party U", "Party V", "Party W", "Party X",
            "Party Y", "Party Z"
    };

    protected Typeface mTfRegular;
    protected Typeface mTfLight;

    //轴宽度
    protected float axisLineWidth = 1.7f;
    //x轴颜色
    protected String xAxisColor = "#289bd3";
    //y轴颜色
    protected String yAxisColor = "#3ca5d8";
    //轴文本颜色
    protected String axisTextColor = "#333333";
    //数据集合1的颜色
    protected String dataSetColor_1 = "#2ec8c9";
    //数据集合2的颜色
    protected String dataSetColor_2 = "#cbbfe6";
    //孔的颜色
    protected String holeColor = "#ffffff";


    public MPBaseChart(Context context,ReportItemEntity reportData) {
        super(context);
        this.context = context;
        this.reportData = reportData;
        initdata();
        initChart();
    }

    public MPBaseChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initdata();
        initChart();
    }

    public MPBaseChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initdata();
        initChart();
    }

    public void initdata(){
        mTfRegular = Typeface.createFromAsset(context.getAssets(), "OpenSans-Regular.ttf");
        mTfLight = Typeface.createFromAsset(context.getAssets(), "OpenSans-Light.ttf");
    }

    protected void initChart(){

    }

    /**
     * 设置图表最大值和最小值
     * @return
     */
    public List<Float> getMaxAndMinValue(){
        List<Float> values = new ArrayList<>();
        float maxValue = reportData.getMaxScore();
        float minValue = 0;
        values.add(maxValue);
        values.add(minValue);
        if(reportData == null){
            return values;
        }

        List<ReportFactorEntity> items = reportData.getItems();
        if(items!=null && items.size()>0){
            for(int i=0;i<items.size();i++){
                float childScore = (float) items.get(i).getChildScore();
                if(childScore>=0){
                    if(childScore>maxValue){
                        maxValue = childScore;
                    }
                }else{
                    if(childScore<minValue){
                        minValue = childScore;
                    }
                }
            }
        }

        values.clear();
        values.add(maxValue);
        values.add(minValue);

        return values;
    }


    /**
     * 设置图表最大值和最小值
     * @return
     */
    /*public List<Float> getMaxAndMinValue(){
        List<Float> values = new ArrayList<>();
        float maxValue = reportData.getMaxScore();
        float minValue = 0;
        values.add(maxValue);
        values.add(minValue);
        if(reportData == null){
            return values;
        }

        List<ReportFactorEntity> items = reportData.getItems();
        if(items!=null && items.size()>0){
            for(int i=0;i<items.size();i++){
                float childScore = (float) items.get(i).getChildScore();
                if(childScore>=0){
                    if(childScore>maxValue){
                        maxValue = childScore;
                    }
                }else{
                    if(childScore<minValue){
                        minValue = childScore;
                    }
                }
            }
        }

        values.clear();
        values.add(maxValue);
        values.add(minValue);

        return values;
    }*/

    /**
     * 自定义格式化x轴文本：
     * 1、文本都置于同样长度，左边补空格
     * 2、在最大文本长度基础上额外加1个空格
     * @param xLabels
     * @return
     */
    /*protected List<String> customFormatXLabels(List<String> xLabels) {
        List<String> formatXLabels = new ArrayList<>();
        if (xLabels == null) {
            return formatXLabels;
        }

        String blank = "\u3000";
        //额外的空格符数量
        int extraBlankCount = 2;
        //最大长度
        int maxLen = 0;
        for (String label : xLabels) {
            if (label.length() > maxLen) {
                maxLen = label.length();
            }
        }

        //需要补充的空格数量
        int needBlankCount = 0;
        for (String label : xLabels) {
            needBlankCount = maxLen - label.length() + extraBlankCount;

            StringBuffer blankStr = new StringBuffer();
            for (int i=0; i<needBlankCount; i++) {
                blankStr.append(blank);
            }

            formatXLabels.add(blankStr.append(label).toString());
        }

        return formatXLabels;
    }*/

    /**
     * 获取x轴文本的最大长度
     * @param xLabels
     * @return
     */
    protected int getXLabelsMaxLength(List<String> xLabels) {
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
