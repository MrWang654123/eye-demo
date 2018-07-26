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

import org.xclcharts.chart.RadarChart;
import org.xclcharts.chart.RadarData;
import org.xclcharts.common.DensityUtil;
import org.xclcharts.common.IFormatterDoubleCallBack;
import org.xclcharts.common.IFormatterTextCallBack;
import org.xclcharts.event.click.PointPosition;
import org.xclcharts.renderer.XEnum;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * 蜘蛛雷达图
 * Created by Administrator on 2018/6/30.
 */

public class QsRadarChartView extends DemoView {

    private Context context;
    private String TAG = "RadarChart01View";
    private RadarChart chart = new RadarChart();


    //标签集合
    private List<String> labels = new LinkedList<String>();
    private List<RadarData> chartData = new LinkedList<RadarData>();

    private Paint mPaintTooltips = new Paint(Paint.ANTI_ALIAS_FLAG);

    private ReportItemEntity reportData;
    private boolean isTopic;

    public QsRadarChartView(Context context, ReportItemEntity reportData) {
        super(context);
        this.context = context;
        this.reportData = reportData;
        initView();
    }

    public QsRadarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public QsRadarChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
//        chartLabels();
//        chartDataSet();
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
            //设置绘图区默认缩进px值
            int[] ltrb = getPieDefaultSpadding();
            chart.setPadding(ltrb[0], ltrb[1], ltrb[2], ltrb[3]);

            chart.setTitle("");
            chart.addSubtitle("");


            //设定数据源
            chart.setCategories(labels);
            chart.setDataSource(chartData);

            //点击事件处理
            chart.ActiveListenItemClick();
            chart.extPointClickRange(1);
            chart.showClikedFocus();

            //数据轴最大值
            chart.getDataAxis().setAxisMax(reportData.getMaxScore());
            //数据轴刻度间隔
            if (reportData.getMaxScore() <= 5) {
                chart.getDataAxis().setAxisSteps(1);
            } else {
                chart.getDataAxis().setAxisSteps(reportData.getMaxScore() / 5);
            }

            //主轴标签偏移50，以便留出空间用于显示点和标签
            chart.getDataAxis().setTickLabelMargin(50);
            chart.getDataAxis().getAxisPaint().setColor(Color.parseColor("#666666"));
            chart.getDataAxis().getAxisPaint().setTextSize(DensityUtil.dip2px(context, 10));

            //设置Y轴标签颜色
            chart.getDataAxis().getTickMarksPaint().setColor(Color.parseColor("#666666"));
            chart.getDataAxis().getTickMarksPaint().setTextSize(DensityUtil.dip2px(context, 10));
            chart.getDataAxis().getTickLabelPaint().setColor(Color.parseColor("#666666"));
            chart.getDataAxis().getTickLabelPaint().setTextSize(DensityUtil.dip2px(context, 10));

            //设置横向标签颜色
            chart.getCategoryAxis().getTickLabelPaint().setColor(Color.parseColor("#666666"));
            chart.getCategoryAxis().getTickLabelPaint().setTextSize(DensityUtil.dip2px(context, 10));
            chart.getCategoryAxis().getTickMarksPaint().setColor(Color.parseColor("#666666"));
            chart.getCategoryAxis().getTickMarksPaint().setTextSize(DensityUtil.dip2px(context, 10));

            chart.getLinePaint().setColor(Color.parseColor("#b5b5b5"));
            chart.getLinePaint().setTextSize(DensityUtil.dip2px(context, 10));

            chart.getLabelPaint().setColor(Color.parseColor("#666666"));
            chart.getLabelPaint().setTextSize(DensityUtil.dip2px(context, 12));


            //定义数据轴标签显示格式
            chart.getDataAxis().setLabelFormatter(new IFormatterTextCallBack() {

                @Override
                public String textFormatter(String value) {
                    // TODO Auto-generated method stub
                    Double tmp = Double.parseDouble(value);
                    DecimalFormat df = new DecimalFormat("#0");
                    String label = df.format(tmp).toString();
                    return (label);
                }

            });

            //定义数据点标签显示格式
            chart.setDotLabelFormatter(new IFormatterDoubleCallBack() {
                @Override
                public String doubleFormatter(Double value) {
                    // TODO Auto-generated method stub
                    DecimalFormat df = new DecimalFormat("#0");
                    String label = "[" + df.format(value).toString() + "]";
                    return label;
                }
            });

            chart.enablePanMode();

        } catch (Exception e) {
            // TODO Auto-generated catch block
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
        PointPosition record = chart.getPositionRecord(x, y);
        if (null == record) return;

        if (record.getDataID() < chartData.size()) {
            RadarData lData = chartData.get(record.getDataID());
            Double lValue = lData.getLinePoint().get(record.getDataChildID());

            String lTitle = labels.get(record.getDataChildID());

            float r = record.getRadius();
            chart.showFocusPointF(record.getPosition(), r * 0.6f);
            chart.getFocusPaint().setStyle(Paint.Style.STROKE);
            chart.getFocusPaint().setStrokeWidth(3);
            chart.getFocusPaint().setColor(Color.RED);

            //在点击处显示tooltip
            mPaintTooltips.setColor(Color.parseColor("#ffffff"));
            mPaintTooltips.setTextSize(DensityUtil.dip2px(context, 14));
            chart.getToolTip().setCurrentXY(x, y);
            chart.getToolTip().addToolTip(lTitle, mPaintTooltips);
            chart.getToolTip().addToolTip(Double.toString(lValue), mPaintTooltips);
            chart.getToolTip().getBackgroundPaint().setColor(Color.parseColor("#6cdaf3"));
            this.invalidate();

            this.invalidate();

        }
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

        labels.clear();
        chartData.clear();

        //标签对应的柱形数据集
        List<Double> dataSeriesA = new LinkedList<Double>();
        //依数据值确定对应的柱形颜色.
        List<Integer> dataColorA = new LinkedList<Integer>();

        for (int i = 0; i < entities.size(); i++) {
            ReportFactorEntity rfe = entities.get(i);
            if (isTopic) {
                if (getCurSelectLabel() == i) {
                    labels.add(String.valueOf(i + 1) + "qs_sel");
                } else {
                    labels.add(String.valueOf(i + 1) + "qs_nor");
                }
            } else {
                labels.add(rfe.getItemName());
            }

            dataSeriesA.add(Double.valueOf(rfe.getChildScore()));
            dataColorA.add(Color.parseColor("#6cdaf3"));

        }
        //此地的颜色为Key值颜色及柱形的默认颜色
        RadarData lineData2 = new RadarData("", dataSeriesA,
                Color.parseColor("#6cdaf3"), XEnum.DataAreaStyle.STROKE);
        lineData2.setDotStyle(XEnum.DotStyle.DOT);
        lineData2.getPlotLine().getPlotDot().setDotRadius(DensityUtil.dip2px(context, 6));
//        lineData2.getPlotLine().getDotPaint().setTextSize(DensityUtil.dip2px(context,3));
        lineData2.getPlotLine().getDotPaint().setColor(Color.parseColor("#6cdaf3"));

        chartData.add(lineData2);

    }

    @Override
    public void updateChart(int curSelect) {
        super.updateChart(curSelect);
    }
}
