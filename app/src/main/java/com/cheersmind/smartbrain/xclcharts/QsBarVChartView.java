package com.cheersmind.smartbrain.xclcharts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.cheersmind.smartbrain.main.entity.ReportFactorEntity;
import com.cheersmind.smartbrain.main.entity.ReportItemEntity;
import com.cheersmind.smartbrain.main.util.DensityUtil;

import org.xclcharts.chart.BarChart;
import org.xclcharts.chart.BarData;
import org.xclcharts.chart.CustomLineData;
import org.xclcharts.common.DrawHelper;
import org.xclcharts.common.IFormatterDoubleCallBack;
import org.xclcharts.common.IFormatterTextCallBack;
import org.xclcharts.event.click.BarPosition;
import org.xclcharts.renderer.XEnum;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * 横向-正负背向柱状图
 * Created by Administrator on 2018/6/30.
 */

public class QsBarVChartView extends DemoView{

    private Context context;
    private String TAG = "QsBarVChartView";
    private BarChart chart = new BarChart();
    //轴数据源
    private List<String> chartLabels = new LinkedList<String>();
    private List<BarData> chartData = new LinkedList<BarData>();
    private List<CustomLineData> mCustomLineDataset = new LinkedList<CustomLineData>();

    ReportItemEntity reportData;
    private boolean isTopic;

    public QsBarVChartView(Context context,ReportItemEntity reportData,int curSelectLabel) {
        super(context);
        this.context = context;
        this.reportData = reportData;
        setCurSelectLabel(curSelectLabel);
        initView();
    }

    public QsBarVChartView(Context context, AttributeSet attrs){
        super(context, attrs);
        initView();
    }

    public QsBarVChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView()
    {
//        chartLabels();
//        chartDataSet();
//        chartDesireLines();
        setChartData(reportData.getItems());
        chartRender();

        //綁定手势滑动事件
        this.bindTouch(this,chart);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //图所占范围大小
        chart.setChartRange(w,h);
    }


    private void chartRender()
    {
        try {

            //设置绘图区默认缩进px值,留置空间显示Axis,Axistitle....
            int [] ltrb = getBarLnDefaultSpadding();
            chart.setPadding(ltrb[0], ltrb[1], ltrb[2], ltrb[3]);

            //显示边框
//            chart.showRoundBorder();

            //标题
            chart.setTitle("");
            chart.addSubtitle("");
            //数据源
            chart.setDataSource(chartData);
            chart.setCategories(chartLabels);
            chart.setCustomLines(mCustomLineDataset);

            //图例
            //chart.getAxisTitle().setLeftAxisTitle("参考成年男性标准值");
            //chart.getAxisTitle().setLowerAxisTitle("(请不要忽视您的健康)");

            //数据轴
            chart.getDataAxis().setAxisMax(reportData.getMaxScore());
            chart.getDataAxis().setAxisMin(reportData.getMinScore());
            chart.getDataAxis().setAxisSteps(reportData.getMaxScore()/2);
            //指隔多少个轴刻度(即细刻度)后为主刻度
            chart.getDataAxis().setDetailModeSteps(1);

            //设置Y轴标签颜色
            chart.getDataAxis().getTickMarksPaint().setColor(Color.parseColor("#666666"));
            chart.getDataAxis().getTickMarksPaint().setTextSize(org.xclcharts.common.DensityUtil.dip2px(context,12));
            chart.getDataAxis().getTickLabelPaint().setColor(Color.parseColor("#666666"));
            chart.getDataAxis().getTickLabelPaint().setTextSize(org.xclcharts.common.DensityUtil.dip2px(context,12));

            //设置横向标签颜色
            chart.getCategoryAxis().getTickLabelPaint().setColor(Color.parseColor("#666666"));
            chart.getCategoryAxis().getTickLabelPaint().setTextSize(org.xclcharts.common.DensityUtil.dip2px(context,12));
            chart.getCategoryAxis().getTickMarksPaint().setColor(Color.parseColor("#666666"));
            chart.getCategoryAxis().getTickMarksPaint().setTextSize(org.xclcharts.common.DensityUtil.dip2px(context,12));

            //设置横向标签旋转角度
            if(isTopic){
                chart.getCategoryAxis().setTickLabelRotateAngle(0);
            }else{
                chart.getCategoryAxis().setTickLabelRotateAngle(-20);
            }

            //定义柱形上标签显示颜色
            chart.getBar().getItemLabelPaint().setColor(Color.parseColor("#6cdaf3"));
            chart.getBar().getItemLabelPaint().setTypeface(Typeface.DEFAULT_BOLD);
            chart.getBar().getItemLabelPaint().setTextSize(DensityUtil.dip2px(context,12));

            chart.getBorder().setBorderLineColor(Color.parseColor("#f7f7f7"));
            chart.getPlotGrid().getHorizontalLinePaint().setColor(Color.parseColor("#f7f7f7"));

            chart.getDataAxis().enabledAxisStd();
            chart.getDataAxis().setAxisStd(0);
            chart.getCategoryAxis().setAxisBuildStd(true);

            //背景网格
            chart.getPlotGrid().showHorizontalLines();

            //定义数据轴标签显示格式
            chart.getDataAxis().setLabelFormatter(new IFormatterTextCallBack(){

                @Override
                public String textFormatter(String value) {
                    // TODO Auto-generated method stub
                    Double tmp = Double.parseDouble(value);
                    DecimalFormat df=new DecimalFormat("#0");
                    String label = df.format(tmp).toString();
                    return (label);
                }

            });

            //标签旋转45度
            //	chart.getCategoryAxis().setTickLabelRotateAngle(45f);
//            chart.getCategoryAxis().getTickLabelPaint().setTextSize(15);

            //设置横轴颜色
            chart.getCategoryAxis().getAxisPaint().setColor(Color.parseColor("#f7f7f7"));
            //设置y轴颜色
            chart.getDataAxis().getAxisPaint().setColor(Color.parseColor("#f7f7f7"));
            chart.getCategoryAxis().hideTickMarks();
            chart.getDataAxis().hideAxisLine();
            chart.getDataAxis().hideTickMarks();

            //在柱形顶部显示值
            chart.getBar().setItemLabelVisible(true);
            //设定格式
            chart.setItemLabelFormatter(new IFormatterDoubleCallBack() {
                @Override
                public String doubleFormatter(Double value) {
                    // TODO Auto-generated method stub
//                    DecimalFormat df=new DecimalFormat("#0");
//                    String label = df.format(value).toString();
                    String label = "[" + value + "]";
                    return label;
                }});

            //激活点击监听
            chart.ActiveListenItemClick();
            chart.showClikedFocus();

            //隐藏Key
            chart.getPlotLegend().hide();

            //让柱子间没空白
            chart.getBar().setBarInnerMargin(0.1f); //可尝试0.1或0.5各有啥效果噢


            //禁用平移模式
            // chart.disablePanMode();

            chart.disableHighPrecision();

            //限制只能左右滑动
            chart.setPlotPanMode(XEnum.PanMode.HORIZONTAL);

            chart.setBarCenterStyle(XEnum.BarCenterStyle.TICKMARKS);

            // chart.showRoundBorder();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    private void chartDataSet()
    {
        //标签对应的柱形数据集
        List<Double> dataSeriesA= new LinkedList<Double>();
        //依数据值确定对应的柱形颜色.
        List<Integer> dataColorA= new LinkedList<Integer>();

        for(int i=1;i<3;i++)
        {
            Random random = new Random();
            if(i%2==0){
                dataSeriesA.add((double) -12);
            }else{
                dataSeriesA.add((double) 12);
            }
            dataColorA.add(Color.RED);
//            if(v <= -5d ) //适中
//            {
//                dataColorA.add(Color.rgb(77, 184, 73));
//            }else if(v <= 5d){ //超重
//                dataColorA.add(Color.rgb(252, 210, 9));
//            }else if(v <= 10d){ //偏胖
//                dataColorA.add(Color.rgb(171, 42, 96));
//            }else{  //肥胖
//                dataColorA.add(Color.RED);
//            }
        }
        //此地的颜色为Key值颜色及柱形的默认颜色
        BarData BarDataA = new BarData("",dataSeriesA,dataColorA,
                Color.rgb(53, 169, 239));

        chartData.add(BarDataA);
    }

    private void chartLabels()
    {
        for(int i=1;i<3;i++)
        {
            chartLabels.add("");
        }
    }

    /**
     * 期望线/分界线
     */
    private void chartDesireLines()
    {
        mCustomLineDataset.add(new CustomLineData("损失",-5d,Color.rgb(77, 184, 73),3));
        //mCustomLineDataset.add(new CustomLineData("超重",6d,(int)Color.rgb(252, 210, 9),4));
        mCustomLineDataset.add(new CustomLineData("平衡",10d,Color.rgb(171, 42, 96),5));
        mCustomLineDataset.add(new CustomLineData("良好",15d,Color.RED,6));

    }

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    @Override
    public void render(Canvas canvas) {
        try{
            chart.render(canvas);


            paint.setTextSize(22);
            paint.setColor(Color.RED);

            float textHeight = DrawHelper.getInstance().getPaintFontHeight(paint);
            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText("",chart.getPlotArea().getLeft(), chart.getPlotArea().getTop() - textHeight ,paint);

            paint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText("",chart.getPlotArea().getRight(), chart.getPlotArea().getBottom() + textHeight ,paint);

        } catch (Exception e){
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        super.onTouchEvent(event);

        switch(event.getAction()){
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
    private void triggerClick(float x,float y)
    {
        BarPosition record = chart.getPositionRecord(x,y);
        if( null == record) return;

        BarData bData = chartData.get(record.getDataID());
        Double bValue = bData.getDataSet().get(record.getDataChildID());

//        Toast.makeText(this.getContext(),
//                "info:" + record.getRectInfo() +
//                        " Key:" + bData.getKey() +
//                        " Current Value:" + Double.toString(bValue),
//                Toast.LENGTH_SHORT).show();

        chart.showFocusRectF(record.getRectF());
        chart.getFocusPaint().setStyle(Paint.Style.STROKE);
        chart.getFocusPaint().setStrokeWidth(3);
        chart.getFocusPaint().setColor(Color.RED);
        this.invalidate();
    }


    /**
     * 设置图表数据
     * *******************************************************
     */
    public void setChartData(List<ReportFactorEntity> entities) {

        if(entities==null || entities.size()==0){
            return;
        }

        resetMax(reportData);

        isTopic = reportData.getTopic();

        chartLabels.clear();
        chartData.clear();

        //标签对应的柱形数据集
        List<Double> dataSeriesA= new LinkedList<Double>();
        //依数据值确定对应的柱形颜色.
        List<Integer> dataColorA= new LinkedList<Integer>();

        for(int i=0;i<entities.size();i++)
        {
            ReportFactorEntity rfe = entities.get(i);
            if(isTopic){
                if(getCurSelectLabel() == i){
                    chartLabels.add(String.valueOf(i+1)+"qs_sel");
                }else{
                    chartLabels.add(String.valueOf(i+1)+"qs_nor");
                }
            }else{
                chartLabels.add(rfe.getItemName());
            }

            dataSeriesA.add(Double.valueOf(rfe.getChildScore()));
            dataColorA.add(Color.parseColor("#6cdaf3"));

        }
        //此地的颜色为Key值颜色及柱形的默认颜色
        BarData BarDataA = new BarData("",dataSeriesA,dataColorA,
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
