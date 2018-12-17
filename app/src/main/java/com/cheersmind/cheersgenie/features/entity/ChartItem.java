package com.cheersmind.cheersgenie.features.entity;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.utils.ArrayListUtil;
import com.cheersmind.cheersgenie.main.entity.ChartCompareItem;
import com.cheersmind.cheersgenie.main.entity.ChartScoreItem;
import com.cheersmind.cheersgenie.main.entity.ReportFactorEntity;
import com.cheersmind.cheersgenie.main.entity.ReportItemEntity;
import com.cheersmind.cheersgenie.main.util.DensityUtil;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 图表数据基础类
 */
public class ChartItem implements MultiItemEntity {
    
    ChartData<?> mChartData;
    //x轴文本
    protected List<String> xLabels;
    //格式化x轴文本
    IAxisValueFormatter iAxisValueFormatter;
    //x轴文本旋转角度
    protected float xLabelRotationAngle = -1;

    //图表视图高度
    private int charViewHeight = 260;

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
    float axisLineWidth = 1.7f;
    //x轴颜色
    String xAxisColor = "#289bd3";
    //y轴颜色
    String yAxisColor = "#3ca5d8";
    //轴文本颜色
    String axisTextColor = "#333333";
    //数据集合1的颜色
    protected String dataSetColor_1 = "#2ec8c9";
    //数据集合2的颜色
    protected String dataSetColor_2 = "#cbbfe6";
    //数据集合2的颜色
    protected String dataSetColor_3 = "#416dd2";
    //孔的颜色
    String holeColor = "#ffffff";


    ChartItem(Context context) {
        this.context = context;
        initData();
    }
    
    ChartItem(Context context, ChartData<?> cd) {
        this.context = context;
        this.mChartData = cd;
        initData();
    }

    /**
     * 初始化数据
     */
    public void initData(){
        mTfRegular = Typeface.createFromAsset(context.getAssets(), "OpenSans-Regular.ttf");
        mTfLight = Typeface.createFromAsset(context.getAssets(), "OpenSans-Light.ttf");

//        Utils.init(context);
    }


    /**
     * 初始化图表视图
     * @param chartView
     */
    public void initChart(View chartView){
        if (chartView != null) {
            //赋值动态的高度
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) chartView.getLayoutParams();
            if (params.height != charViewHeight) {
                params.height = charViewHeight;
                chartView.setLayoutParams(params);
            }
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//                    DensityUtil.dip2px(context,height));

            //x轴文本格式化
            onXAxisValueFormatter();

            //x轴文本旋转角度
            onXLabelRotationAngle();
        }
    };

    /**
     * 生成图表适配数据
     * @param reportData
     */
    public ChartItem generateChartData(ReportItemEntity reportData) {
        this.reportData = reportData;

        //动态计算高度
        int height = adjustHeightByXLabels(reportData);
        charViewHeight = DensityUtil.dip2px(context,height);

        //x轴文本格式化
        onXAxisValueFormatter();

        //x轴文本旋转角度
        onXLabelRotationAngle();

        return this;
    }


    public ChartData<?> getChartData() {
        return mChartData;
    }

    public void setChartData(ChartData<?> mChartData) {
        this.mChartData = mChartData;
    }

    public List<String> getxLabels() {
        return xLabels;
    }

    public void setxLabels(List<String> xLabels) {
        this.xLabels = xLabels;
    }


    /**
     * x轴文本格式化
     */
    protected void onXAxisValueFormatter() {
        if (xLabels != null && iAxisValueFormatter == null) {
            iAxisValueFormatter = new IndexAxisValueFormatter(xLabels);
        }
    }

    /**
     * x轴文本旋转角度
     */
    protected void onXLabelRotationAngle() {
        if (xLabels != null && xLabelRotationAngle == -1) {
            boolean hasSetRotationAngle = false;
            //最大长度
            int maxLen = getXLabelsMaxLength(xLabels);
            if (xLabels.size() == ADJUST_SIZE_1) {
                //X轴文本旋转角度
                xLabelRotationAngle = 0;
                hasSetRotationAngle = true;

            } else if (xLabels.size() == ADJUST_SIZE_2) {
                if (maxLen < ADJUST_MAX_LENGTH_2) {
                    //X轴文本旋转角度
                    xLabelRotationAngle = 0;
                    hasSetRotationAngle = true;
                }

//            } else if (xLabels.size() >= ADJUST_SIZE_3) {//X轴坐标数量大于5，且最大文本长度大于7，则X轴文本旋转30度
//                if (maxLen >= ADJUST_MAX_LENGTH_2) {
//                    //X轴文本旋转角度
//                    xLabelRotationAngle = -50;
//                    hasSetRotationAngle = true;
//
//                } else if (maxLen >= ADJUST_MAX_LENGTH_1) {
//                    //X轴文本旋转角度
//                    xLabelRotationAngle = -35;
//                    hasSetRotationAngle = true;
//                }

            }  else {
                if (maxLen >= ADJUST_MAX_LENGTH_2) {
                    //X轴文本旋转角度
                    xLabelRotationAngle = -50;
                    hasSetRotationAngle = true;

                } else if (maxLen >= ADJUST_MAX_LENGTH_1) {
                    //X轴文本旋转角度
                    xLabelRotationAngle = -35;
                    hasSetRotationAngle = true;
                }
            }

            if (!hasSetRotationAngle) {
                //X轴文本旋转角度
                xLabelRotationAngle = -20;
            }
        }
    }


    /**
     * 设置图表最大值和最小值
     * @return
     */
    List<Float> getMaxAndMinValue(){
        List<Float> values = new ArrayList<>();
        float maxValue = 0;
        float minValue = 0;
        if(reportData == null){
            values.add(maxValue);
            values.add(minValue);

            return values;
        }

        //比较项
        List<ChartCompareItem> compareItems = reportData.getItems();
        if(ArrayListUtil.isNotEmpty(compareItems)){
            for(ChartCompareItem compareItem : compareItems){
                //分数项
                List<ChartScoreItem> scoreItems = compareItem.getScoreItems();
                if (ArrayListUtil.isNotEmpty(scoreItems)) {
                    for (ChartScoreItem scoreItem : scoreItems) {
                        float score = (float) scoreItem.getScore();
                        if (score >= 0) {
                            if (score > maxValue) {
                                maxValue = score;
                            }
                        } else {
                            if (score < minValue) {
                                minValue = score;
                            }
                        }
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
     * 获取x轴文本的最大长度
     * @param xLabels
     * @return
     */
    int getXLabelsMaxLength(List<String> xLabels) {
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


    //x轴文本数量满多少个开始微调
    static final int ADJUST_SIZE_1 = 1;
    static final int ADJUST_SIZE_2 = 2;
    static final int ADJUST_SIZE_3 = 6;
    //x轴文本的最大长度达到都少开始微调：第一档
    static final int ADJUST_MAX_LENGTH_1 = 8;
    //x轴文本的最大长度达到都少开始微调：第二档
    static final int ADJUST_MAX_LENGTH_2 = 12;

    /**
     * 获取图表的高度
     * @param reportItemEntity
     * @return
     */
    private int adjustHeightByXLabels(ReportItemEntity reportItemEntity) {
        int height = 280;
        if (reportItemEntity != null) {
            int chartType = reportItemEntity.getChartType();
            //雷达图
            if (chartType == Dictionary.CHART_RADAR) {
                height = 350;

            } else if (chartType == Dictionary.CHART_BAR_H) {//水平条状图
                height = 280;

            } else {//其他：柱状图和曲线图
                List<String> xLabels = getXLabels(reportItemEntity);
                //非空
                if (ArrayListUtil.isNotEmpty(xLabels)) {
                    //X轴坐标数量大于2
                    if (xLabels.size() > ADJUST_SIZE_2) {
                        //x轴文本的最大长度
                        int maxLen = getXLabelsMaxLength(xLabels);
                        //长度>=12
                        if (maxLen >= ADJUST_MAX_LENGTH_2) {
                            height = 440;

                        } else if (maxLen >= ADJUST_MAX_LENGTH_1) {//长度>=8
                            height = 350;
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
    private List<String> getXLabels(ReportItemEntity reportItemEntity) {
        List<String> xLabels = new ArrayList<>();

        if (reportItemEntity != null && reportItemEntity.getItems() != null) {
            //比较项
            List<ChartCompareItem> compareItems = reportItemEntity.getItems();
            if (ArrayListUtil.isNotEmpty(compareItems)) {
                for (ChartCompareItem compareItem : compareItems) {
                    //分数项
                    List<ChartScoreItem> scoreItems = compareItem.getScoreItems();
                    if (ArrayListUtil.isNotEmpty(scoreItems)) {
                        for (ChartScoreItem scoreItem : scoreItems) {
                            xLabels.add(scoreItem.getItemName());
                        }
                    }

                    if (xLabels.size() > 0) {
                        break;
                    }
                }
            }
        }

        return xLabels;
    }

    @Override
    public int getItemType() {
        return 0;
    }

    /**
     * 重绘图表
     */
    public void invalidate() {

    }

}

