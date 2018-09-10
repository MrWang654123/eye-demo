package com.cheersmind.cheersgenie.xclcharts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.cheersmind.cheersgenie.main.entity.ReportFactorEntity;
import com.cheersmind.cheersgenie.main.entity.ReportItemEntity;

import org.xclcharts.chart.BarChart;
import org.xclcharts.chart.BarData;
import org.xclcharts.common.DensityUtil;
import org.xclcharts.common.IFormatterDoubleCallBack;
import org.xclcharts.common.IFormatterTextCallBack;
import org.xclcharts.event.click.BarPosition;
import org.xclcharts.renderer.XEnum;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2018/6/30.
 */

public class QsBarHChartView extends DemoView {

    private Context context;
    private static final String TAG = "BarChart09View";
    private BarChart chart = new BarChart();

    //标签轴
    private List<String> chartLabels = new LinkedList<String>();
    private List<BarData> chartData = new LinkedList<BarData>();
    Paint mPaintToolTip = new Paint(Paint.ANTI_ALIAS_FLAG);

    private ReportItemEntity reportData;
    private boolean isTopic;

    public QsBarHChartView(Context context, ReportItemEntity reportData, int curSelectLabel) {
        super(context);
        this.reportData = reportData;
        this.context = context;
        setCurSelectLabel(curSelectLabel);
        initView();
    }

    public QsBarHChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public QsBarHChartView(Context context, AttributeSet attrs, int defStyle) {
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
        if (null != chart) chart.setChartRange(w, h);
    }


    private void chartRender() {
        try {

            int scaleDp = DensityUtil.dip2px(getContext(), 50);
            //设置绘图区默认缩进px值,留置空间显示Axis,Axistitle....
            int[] ltrb = getBarLnDefaultSpadding();
            chart.setPadding(scaleDp, ltrb[1], ltrb[2], ltrb[3]);


            chart.setTitle("");
            chart.addSubtitle("");
            chart.setTitleVerticalAlign(XEnum.VerticalAlign.MIDDLE);
            //chart.setTitleAlign(XEnum.HorizontalAlign.LEFT);

            //数据源
            chart.setDataSource(chartData);
            chart.setCategories(chartLabels);

            //轴标题
            chart.getAxisTitle().setLeftTitle("");
            chart.getAxisTitle().setLowerTitle("");
            chart.getAxisTitle().setRightTitle("");

            //数据轴
            chart.getDataAxis().setAxisMax(reportData.getMaxScore());
            chart.getDataAxis().setAxisMin(reportData.getMinScore());
            chart.getDataAxis().setAxisSteps(reportData.getMaxScore() / 2);

            chart.getDataAxis().setDetailModeSteps(1);

            chart.getDataAxis().enabledAxisStd();
            chart.getDataAxis().setAxisStd(0);
            chart.getCategoryAxis().setAxisBuildStd(true);

            chart.getDataAxis().hideTickMarks();

            //轴颜色
            chart.getDataAxis().getAxisPaint().setColor(Color.parseColor("#999999"));
            chart.getCategoryAxis().getAxisPaint().setColor(Color.parseColor("#999999"));

//            chart.getDataAxis().getAxisPaint().setColor(Color.parseColor("#f7f7f7"));
            //设置Y轴标签颜色
            chart.getDataAxis().getTickMarksPaint().setColor(Color.parseColor("#666666"));
            chart.getDataAxis().getTickMarksPaint().setTextSize(DensityUtil.dip2px(context, 12));
            chart.getDataAxis().getTickLabelPaint().setColor(Color.parseColor("#12b2f4"));
            chart.getDataAxis().getTickLabelPaint().setTextSize(DensityUtil.dip2px(context, 12));

            //设置横向标签颜色
            chart.getCategoryAxis().getTickLabelPaint().setColor(Color.parseColor("#12b2f4"));
            if(reportData.getItems().size()>4){
                chart.getCategoryAxis().getTickLabelPaint().setTextSize(DensityUtil.dip2px(context, 10));
            }else{
                chart.getCategoryAxis().getTickLabelPaint().setTextSize(DensityUtil.dip2px(context, 12));
            }
            chart.getCategoryAxis().getTickMarksPaint().setColor(Color.parseColor("#666666"));
            chart.getCategoryAxis().getTickMarksPaint().setTextSize(DensityUtil.dip2px(context, 12));
//            chart.getDataAxis().setLabelFormatter(new IFormatterTextCallBack(){
//
//                @Override
//                public String textFormatter(String value) {
//                    // TODO Auto-generated method stub
//                    String tmp = value+"万元";
//                    return tmp;
//                }
//
//            });

            //网格
            chart.getPlotGrid().showHorizontalLines();
            chart.getPlotGrid().hideVerticalLines();
            chart.getPlotGrid().hideEvenRowBgColor();

            //设置横向标签旋转角度
            if (isTopic) {
                chart.getCategoryAxis().setTickLabelRotateAngle(0);
            } else {
                chart.getCategoryAxis().setTickLabelRotateAngle(-20);
            }

            //横向显示柱形
            chart.setChartDirection(XEnum.Direction.HORIZONTAL);
            //在柱形顶部显示值
            chart.getBar().setItemLabelVisible(true);
            chart.getBar().getItemLabelPaint().setColor(Color.parseColor("#ffa400"));
            chart.getBar().getItemLabelPaint().setTypeface(Typeface.DEFAULT_BOLD);
            chart.getBar().getItemLabelPaint().setTextSize(DensityUtil.dip2px(context, 12));

            chart.getBorder().setBorderLineColor(Color.parseColor("#f7f7f7"));
            chart.getPlotGrid().getHorizontalLinePaint().setColor(Color.parseColor("#f7f7f7"));

            //定义数据轴标签显示格式（设置后横轴显示到了底部）
            chart.getDataAxis().setLabelFormatter(new IFormatterTextCallBack(){

                @Override
                public String textFormatter(String value) {
                    // TODO Auto-generated method stub
                    Double tmp = Double.parseDouble(value);
                    String label = String.valueOf(tmp);
                    return (label);
                }

            });

            chart.setItemLabelFormatter(new IFormatterDoubleCallBack() {
                @Override
                public String doubleFormatter(Double value) {
                    // TODO Auto-generated method stub
//                    DecimalFormat df=new DecimalFormat("[#0]");
//                    String label = df.format(value).toString();
                    String label = "[" + value + "]";
                    return label;
                }
            });

            //激活点击监听
            chart.ActiveListenItemClick();
            chart.showClikedFocus();

            //禁用平移模式
//            chart.disablePanMode();
            chart.getDataAxis().setVerticalTickPosition(XEnum.VerticalAlign.BOTTOM);
//            chart.setDataAxisLocation(XEnum.AxisLocation.TOP);

            chart.getPlotLegend().hide();
            chart.getBar().setBarStyle(XEnum.BarStyle.FILL);

            /*
			chart.setDataAxisPosition(XEnum.DataAxisPosition.BOTTOM);
			chart.getDataAxis().setVerticalTickPosition(XEnum.VerticalAlign.BOTTOM);

			chart.setCategoryAxisPosition(XEnum.CategoryAxisPosition.LEFT);
			chart.getCategoryAxis().setHorizontalTickAlign(Align.LEFT);
			*/

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

        //交叉线
        if (chart.getDyLineVisible()) chart.getDyLine().setCurrentXY(x, y);

        if (!chart.getListenItemClickStatus()) {
            //交叉线
            if (chart.getDyLineVisible() && chart.getDyLine().isInvalidate()) this.invalidate();
        } else {
            BarPosition record = chart.getPositionRecord(x, y);
            if (null == record) {
                if (chart.getDyLineVisible()) this.invalidate();
                return;
            }

            if (record.getDataID() >= chartData.size()) return;
            BarData bData = chartData.get(record.getDataID());

            if (record.getDataChildID() >= bData.getDataSet().size()) return;
            Double bValue = bData.getDataSet().get(record.getDataChildID());
            String lTitle = chartLabels.get(record.getDataChildID());

            //显示选中框
            chart.showFocusRectF(record.getRectF());
            chart.getFocusPaint().setStyle(Paint.Style.STROKE);
            chart.getFocusPaint().setStrokeWidth(3);
            chart.getFocusPaint().setColor(Color.GREEN);


            //在点击处显示tooltip
            mPaintToolTip.setAntiAlias(true);
            mPaintToolTip.setColor(Color.parseColor("#ffffff"));
            mPaintToolTip.setTextSize(com.cheersmind.cheersgenie.main.util.DensityUtil.dip2px(context, 14));


//            mDotToolTip.setDotStyle(XEnum.DotStyle.RECT);
//            mDotToolTip.setColor(Color.BLUE); //bData.getColor());

            //位置显示方法一:
            //用下列方法可以让tooltip显示在柱形顶部
            //chart.getToolTip().setCurrentXY(record.getRectF().centerX(),record.getRectF().top);
            //位置显示方法二:
            //用下列方法可以让tooltip在所点击位置显示
            chart.getToolTip().setCurrentXY(x, y);
            chart.getToolTip().setStyle(XEnum.DyInfoStyle.ROUNDRECT);
            chart.getToolTip().addToolTip(lTitle, mPaintToolTip);
            chart.getToolTip().addToolTip(Double.toString(bValue), mPaintToolTip);
            chart.getToolTip().getBackgroundPaint().setColor(Color.parseColor("#ffa400"));
            chart.getToolTip().setAlign(Paint.Align.CENTER);

            chart.getToolTip().setInfoStyle(XEnum.DyInfoStyle.RECT);
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

        chartLabels.clear();
        chartData.clear();

        //标签对应的柱形数据集
        List<Double> dataSeriesA = new LinkedList<Double>();
        //依数据值确定对应的柱形颜色.
        List<Integer> dataColorA = new LinkedList<Integer>();

        for (int i = 0; i < entities.size(); i++) {
            ReportFactorEntity rfe = entities.get(i);
            if (isTopic) {
                if (getCurSelectLabel() == i) {
                    chartLabels.add(String.valueOf(i + 1) + "qs_sel");
                } else {
                    chartLabels.add(String.valueOf(i + 1) + "qs_nor");
                }
            } else {
                chartLabels.add(rfe.getItemName());
            }

            dataSeriesA.add(Double.valueOf(rfe.getChildScore()));
            dataColorA.add(Color.parseColor("#6cdaf3"));

        }
        //此地的颜色为Key值颜色及柱形的默认颜色
        BarData BarDataA = new BarData("", dataSeriesA, dataColorA,
                Color.parseColor("#6cdaf3"));

        chartData.add(BarDataA);
    }

    @Override
    public void updateChart(int curSelect) {
        super.updateChart(curSelect);
        setCurSelectLabel(curSelect);
        setChartData(reportData.getItems());
        this.invalidate();
    }
}
