package com.cheersmind.smartbrain.xclcharts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.cheersmind.smartbrain.main.entity.ReportFactorEntity;
import com.cheersmind.smartbrain.main.entity.ReportItemEntity;
import com.cheersmind.smartbrain.main.util.DensityUtil;

import org.xclcharts.chart.CustomLineData;
import org.xclcharts.chart.PointD;
import org.xclcharts.chart.SplineChart;
import org.xclcharts.chart.SplineData;
import org.xclcharts.common.IFormatterTextCallBack;
import org.xclcharts.event.click.PointPosition;
import org.xclcharts.renderer.XEnum;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 定制-曲线图
 * Created by Administrator on 2018/6/30.
 */

public class QsSplineChartView extends DemoView {
    private String TAG = "QsSplineChartView";
    private SplineChart chart = new SplineChart();
    private Context context;
    //分类轴标签集合
    private LinkedList<String> labels = new LinkedList<String>();
    private LinkedList<SplineData> chartData = new LinkedList<SplineData>();
    private LinkedList<String> itemlabels = new LinkedList<String>();

    private Paint mPaintTooltips = new Paint(Paint.ANTI_ALIAS_FLAG);

    ReportItemEntity reportData;
    //    List<ReportFactorEntity> reportListData = new ArrayList<>();
    private boolean isTopic;

    //setCategoryAxisCustomLines
    // splinechart支持横向和竖向定制线
    private List<CustomLineData> mXCustomLineDataset = new ArrayList<CustomLineData>();
    private List<CustomLineData> mYCustomLineDataset = new ArrayList<CustomLineData>();

    public QsSplineChartView(Context context, ReportItemEntity reportData, int curSelectLabel) {
        super(context);
        this.context = context;
        this.reportData = reportData;
        setCurSelectLabel(curSelectLabel);
        initView();
    }

    public QsSplineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public QsSplineChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {

        setChartData(reportData.getItems());
        chartRender();

        //綁定手势滑动事件
        this.bindTouch(this, chart);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //图所占范围大小
        chart.setChartRange(w, h);
    }


    private void chartRender() {
        try {

            //设置绘图区默认缩进px值,留置空间显示Axis,Axistitle....
            int[] ltrb = getBarLnDefaultSpadding();
            chart.setPadding(ltrb[0], ltrb[1], ltrb[2], ltrb[3]);

            //显示边框
//			chart.showRoundBorder();

            //数据源
            chart.setCategories(labels);
            chart.setDataSource(chartData);


            //坐标系
            //数据轴最大值
            chart.getDataAxis().setAxisMax(reportData.getMaxScore());
            //chart.getDataAxis().setAxisMin(0);
            //数据轴刻度间隔
            chart.getDataAxis().setAxisSteps(reportData.getMaxScore() / 5);
            chart.setCustomLines(mYCustomLineDataset); //y轴

            //设置Y轴标签颜色
            chart.getDataAxis().getTickMarksPaint().setColor(Color.parseColor("#424242"));
            chart.getDataAxis().getTickMarksPaint().setTextSize(DensityUtil.dip2px(context, 12));

            //设置横向标签颜色
            chart.getCategoryAxis().getTickLabelPaint().setColor(Color.parseColor("#424242"));
            chart.getCategoryAxis().getTickLabelPaint().setTextSize(DensityUtil.dip2px(context, 12));

            //设置横向标签旋转角度
            if (isTopic) {
                chart.getCategoryAxis().setTickLabelRotateAngle(0);
            } else {
                chart.getCategoryAxis().setTickLabelRotateAngle(-20);
            }

            //标签轴最大值
//            chart.setCategoryAxisMax(labels.size());
            //标签轴最小值
            chart.setCategoryAxisMin(0);
            //chart.setCustomLines(mXCustomLineDataset); //y轴
            chart.setCategoryAxisCustomLines(mXCustomLineDataset); //x轴

            //设置图的背景色
            chart.setApplyBackgroundColor(false);
            chart.setBackgroundColor(Color.parseColor("#ffffff"));
            chart.getBorder().setBorderLineColor(Color.parseColor("#f7f7f7"));

            //调轴线与网络线风格
//            chart.getCategoryAxis().hideTickMarks();
//            chart.getDataAxis().hideAxisLine();
            chart.getDataAxis().hideTickMarks();
            chart.getPlotGrid().showHorizontalLines();
            //chart.hideTopAxis();
            //chart.hideRightAxis();

            chart.getDataAxis().getAxisPaint().setColor(Color.parseColor("#1e88cc"));
            chart.getDataAxis().getAxisPaint().setStrokeWidth(3);

            chart.getPlotGrid().getHorizontalLinePaint().setColor(Color.parseColor("#f7f7f7"));
            chart.getCategoryAxis().getAxisPaint().setColor(Color.parseColor("#1e88cc"));
            chart.getCategoryAxis().getAxisPaint().setStrokeWidth(3);


            //定义交叉点标签显示格式,特别备注,因曲线图的特殊性，所以返回格式为:  x值,y值
            //请自行分析定制
            chart.setDotLabelFormatter(new IFormatterTextCallBack() {

                @Override
                public String textFormatter(String value) {
                    // TODO Auto-generated method stub
                    String label = "[" + value + "]";
                    return (label);
                }

            });
            //标题
            chart.setTitle("");
            chart.addSubtitle("");

            //激活点击监听
            chart.ActiveListenItemClick();
            //为了让触发更灵敏，可以扩大5px的点击监听范围
            chart.extPointClickRange(5);
            chart.showClikedFocus();

            //显示平滑曲线
            if (reportData.getItems().size() == 2) {
                chart.setCrurveLineStyle(XEnum.CrurveLineStyle.BEELINE);
            } else {
                chart.setCrurveLineStyle(XEnum.CrurveLineStyle.BEZIERCURVE);
            }

            //图例显示在正下方
            chart.getPlotLegend().setVerticalAlign(XEnum.VerticalAlign.BOTTOM);
            chart.getPlotLegend().setHorizontalAlign(XEnum.HorizontalAlign.CENTER);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public void render(Canvas canvas) {
        try {
            chart.render(canvas);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:

                triggerClick(event.getX(), event.getY());
                break;
        }
        return true;
    }


    //触发监听
    private void triggerClick(float x, float y) {
        if (!chart.getListenItemClickStatus()) return;

        PointPosition record = chart.getPositionRecord(x, y);
        if (null == record) {
            return;
        }

        if (record.getDataID() >= chartData.size()) return;
        SplineData lData = chartData.get(record.getDataID());
        List<PointD> linePoint = lData.getLineDataSet();
        int pos = record.getDataChildID();
        int i = 0;
        Iterator it = linePoint.iterator();
        while (it.hasNext()) {
            PointD entry = (PointD) it.next();

            if (pos == i) {
                Double xValue = entry.x;
                Double yValue = entry.y;

                float r = record.getRadius();
                chart.showFocusPointF(record.getPosition(), r + r * 0.1f);
                chart.getFocusPaint().setStyle(Paint.Style.FILL);
                chart.getFocusPaint().setStrokeWidth(2);
                if (record.getDataID() >= 2) {
                    chart.getFocusPaint().setColor(Color.BLUE);
                } else {
                    chart.getFocusPaint().setColor(Color.RED);
                }
                //在点击处显示tooltip
                mPaintTooltips.setColor(Color.parseColor("#ffffff"));
                mPaintTooltips.setTextSize(DensityUtil.dip2px(context, 14));
                chart.getToolTip().setCurrentXY(x, y);
                chart.getToolTip().addToolTip(itemlabels.get(i + 1), mPaintTooltips);
                chart.getToolTip().addToolTip(Double.toString(yValue), mPaintTooltips);
                chart.getToolTip().getBackgroundPaint().setColor(Color.parseColor("#6cdaf3"));
                this.invalidate();

                break;
            }
            i++;
        }//end while

    }


    /**
     * 设置图表数据
     * *******************************************************
     */
    public void setChartData(List<ReportFactorEntity> entities) {
        if (entities == null || entities.size() == 0) {
            return;
        }

        resetMax(reportData);

        isTopic = reportData.getTopic();

        chartData.clear();//数据集合
        labels.clear();//设置横坐标文字

        //线i的数据集
        List<PointD> linePoint = new ArrayList<PointD>();
        int size = entities.size();
        for (int i = 0; i < size; i++) {
            ReportFactorEntity rfe = entities.get(i);
            if (i == 0) {
                labels.add("");
                itemlabels.add("");
            }
//            if(size>6){
//                labels.add("");//长度超过6，不显示横轴标签
//            }else{
//                if(isTopic){
//                    if(getCurSelectLabel() == i){
//                        labels.add(String.valueOf(i+1)+"qs_sel");//主题报告
//                    }else{
//                        labels.add(String.valueOf(i+1)+"qs_nor");//主题报告
//                    }
//                }else{
//                    labels.add(rfe.getItemName());
//                }
//            }

            if (isTopic) {
                if (getCurSelectLabel() == i) {
                    labels.add(String.valueOf(i + 1) + "qs_sel");//主题报告
                } else {
                    labels.add(String.valueOf(i + 1) + "qs_nor");//主题报告
                }
            } else {
                labels.add(rfe.getItemName());
            }

            itemlabels.add(rfe.getItemName());
            linePoint.add(new PointD(i + 1, Double.valueOf(rfe.getChildScore())));
            if (i == entities.size() - 1) {
                labels.add("");
                itemlabels.add("");
            }

        }

        chart.setCategoryAxisMax(labels.size() - 1);

        SplineData dataSeries = new SplineData("", linePoint,
                Color.parseColor("#6cdaf3"));
        if (entities.size() == 2) {
//            dataSeries.setDotStyle(XEnum.DotStyle.HIDE);
            dataSeries.setDotStyle(XEnum.DotStyle.DOT);
        } else {
            dataSeries.setDotStyle(XEnum.DotStyle.DOT);
        }
        //设置点和线的大小
        dataSeries.setDotRadius(DensityUtil.dip2px(context, 4));
        dataSeries.getDotPaint().setColor(Color.parseColor("#6cdaf3"));
        dataSeries.getLinePaint().setStrokeWidth(DensityUtil.dip2px(context, 2));
        chartData.add(dataSeries);
    }

    @Override
    public void updateChart(int curSelect) {
        super.updateChart(curSelect);
        setCurSelectLabel(curSelect);
        setChartData(reportData.getItems());
        this.invalidate();
    }
}
