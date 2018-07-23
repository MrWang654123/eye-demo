/**
 * Copyright 2014  XCL-Charts
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 	
 * @Project XCL-Charts 
 * @Description Android图表基类库
 * @author XiongChuanLiang<br/>(xcl_168@aliyun.com)
 * @Copyright Copyright (c) 2014 XCL-Charts (www.xclcharts.com)
 * @license http://www.apache.org/licenses/  Apache v2 License
 * @version 1.0
 */
package com.cheersmind.smartbrain.xclcharts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import org.xclcharts.chart.CustomLineData;
import org.xclcharts.chart.LineChart;
import org.xclcharts.chart.LineData;
import org.xclcharts.common.DensityUtil;
import org.xclcharts.common.IFormatterDoubleCallBack;
import org.xclcharts.common.IFormatterTextCallBack;
import org.xclcharts.renderer.XEnum;
import org.xclcharts.renderer.info.AnchorDataPoint;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @ClassName LineChart01View
 * @Description  折线图的例子 <br/>
 *  * 	~_~
 * @author XiongChuanLiang<br/>(xcl_168@aliyun.com)
 */
public class LineChart02View extends DemoView implements Runnable{
	
	private String TAG = "LineChart02View";
	private LineChart chart = new LineChart();
	
	//标签集合
	private LinkedList<String> labels = new LinkedList<String>();
	private LinkedList<LineData> chartData = new LinkedList<LineData>();
	private List<CustomLineData> mCustomLineDataset = new LinkedList<CustomLineData>();
	
	//批注
	List<AnchorDataPoint> mAnchorSet = new ArrayList<AnchorDataPoint>();

	public LineChart02View(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView();
	}
	
	public LineChart02View(Context context, AttributeSet attrs){   
        super(context, attrs);   
        initView();
	 }
	 
	 public LineChart02View(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
			initView();
	 }
	 
	 private void initView()
	 {
		 	chartLabels();
			chartDataSet();	
			chartDesireLines();
			chartRender();
			new Thread(this).start();
			
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
			chart.setPadding(DensityUtil.dip2px(getContext(), 45),ltrb[1], ltrb[2],  ltrb[3]);
		
			
			//设定数据源
			chart.setCategories(labels);								
		//	chart.setDataSource(chartData);
			//chart.setCustomLines(mCustomLineDataset);
			
			//数据轴最大值
			chart.getDataAxis().setAxisMax(100);
			//数据轴刻度间隔
			chart.getDataAxis().setAxisSteps(20);
			//指隔多少个轴刻度(即细刻度)后为主刻度
			chart.getDataAxis().setDetailModeSteps(0);
					
			//背景网格
			chart.getPlotGrid().showHorizontalLines(); 
			
			//标题
			chart.setTitle("");
			chart.addSubtitle("");
			
			//隐藏顶轴和右边轴
			//chart.hideTopAxis();
			//chart.hideRightAxis();

			chart.getBorder().setBorderLineColor(Color.parseColor("#f7f7f7"));
			//设置轴风格
			chart.getPlotGrid().getHorizontalLinePaint().setColor(Color.parseColor("#f7f7f7"));
		
			//chart.getDataAxis().setTickMarksVisible(false);
			chart.getDataAxis().getAxisPaint().setStrokeWidth(2);
			chart.getDataAxis().getTickMarksPaint().setStrokeWidth(2);
			chart.getDataAxis().showAxisLabels();
			
			chart.getCategoryAxis().getAxisPaint().setStrokeWidth(2);
			chart.getCategoryAxis().hideTickMarks();
												
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
			
			
			//定义线上交叉点标签显示格式
			chart.setItemLabelFormatter(new IFormatterDoubleCallBack() {
				@Override
				public String doubleFormatter(Double value) {
					// TODO Auto-generated method stub
					DecimalFormat df=new DecimalFormat("#0");					 
					String label = df.format(value).toString();
					return label;
				}});
			
			//chart.setItemLabelFormatter(callBack)
						
			//允许线与轴交叉时，线会断开
			chart.setLineAxisIntersectVisible(false);
			
			//chart.setDataSource(chartData); 
			//动态线									
			chart.showDyLine();
			
			//不封闭
			chart.setAxesClosed(false);
			
			//扩展绘图区右边分割的范围，让定制线的说明文字能显示出来
			chart.getClipExt().setExtRight(150.f);
			
			//设置标签交错换行显示
			chart.getCategoryAxis().setLabelLineFeed(XEnum.LabelLineFeed.ODD_EVEN);
			
			//仅能横向移动
			chart.setPlotPanMode(XEnum.PanMode.HORIZONTAL);
			
			
			//chart.getDataAxis().hide();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e(TAG, e.toString());
		}
	}
	
	//计算下平均线
	private double calcAvg()
	{
//		double total = 400d + 480d + 500d + 560d + 800d + 950d +1200d + 630d + 710d;
//		double yearNumber = 5d;
//
//		return (total/yearNumber);
		return 0;
	}
	
	private void chartDataSet()
	{
		//Line 1
		LinkedList<Double> dataSeries1= new LinkedList<Double>();	
		dataSeries1.add(30d);
		dataSeries1.add(40d);
		dataSeries1.add(50d);
		dataSeries1.add(60d);
		dataSeries1.add(70d);
		LineData lineData1 = new LineData("",dataSeries1,Color.rgb(234, 83, 71));
		lineData1.setDotStyle(XEnum.DotStyle.HIDE);

		//Line 2
		LinkedList<Double> dataSeries2= new LinkedList<Double>();
		dataSeries2.add((double)60);
		dataSeries2.add((double)60);
		dataSeries2.add((double)60);
		dataSeries2.add((double)60);
		dataSeries2.add((double)60);

		LineData lineData2 = new LineData("",dataSeries2,Color.rgb(75, 166, 51));
		lineData2.setDotStyle(XEnum.DotStyle.HIDE);
//		lineData2.getPlotLine().getDotPaint().setColor(Color.rgb(234, 142, 43));
//		lineData2.getDotLabelPaint().setColor(Color.rgb(234, 142, 43));
//		lineData2.setLabelVisible(true);
//		lineData2.getLabelOptions().getBox().getBackgroundPaint().setColor(Color.rgb(76, 76, 76));
		//lineData2.getPlotLabel().hideBox();
	
		chartData.add(lineData1);
		chartData.add(lineData2);


		//批注
		//List<AnchorDataPoint> mAnchorSet = new ArrayList<AnchorDataPoint>();
//		AnchorDataPoint an4 = new AnchorDataPoint(0,2, XEnum.AnchorStyle.CAPRECT);
//		an4.setAnchor("批注");
//		an4.setBgColor(Color.rgb(255, 145, 126));
//		mAnchorSet.add(an4);
//		chart.setAnchorDataPoint(mAnchorSet);
	}
	
	private void chartLabels()
	{
		labels.add("");
		labels.add("");
		labels.add("");
		labels.add("");
		labels.add("");
	}
	
	/**
	 * 期望线/分界线
	 */
	private void chartDesireLines()
	{				
//		mCustomLineDataset.add(new CustomLineData("稍好",1500d,Color.rgb(35, 172, 57),5));
//		mCustomLineDataset.add(new CustomLineData("舒适",3000d,Color.rgb(69, 181, 248),5));
//		mCustomLineDataset.add(new CustomLineData("[个人均线]",calcAvg(),Color.rgb(251, 79, 128),6));
	}
	
	@Override
    public void render(Canvas canvas) {
        try{
            chart.render(canvas);
        } catch (Exception e){
        	Log.e(TAG, e.toString());
        }
    }


	@Override
	public void run() {
		// TODO Auto-generated method stub
		 try {          
	         	chartAnimation();         	
	         }
	         catch(Exception e) {
	             Thread.currentThread().interrupt();
	         }  
	}
	
	private void chartAnimation()
	{
		  try { 
          	int count =  chartData.size();
          	for(int i=0;i< count ;i++)
          	{
          		Thread.sleep(150);
          		LinkedList<LineData> animationData = new LinkedList<LineData>();
          		for(int j=0;j<=i;j++)
                {            			            			
          			animationData.add(chartData.get(j));          			
                }   
         
          		//Log.e(TAG,"size = "+animationData.size());
          		chart.setDataSource(animationData);          		
          		if(i == count - 1)
          		{
          			chart.getDataAxis().show();
          			chart.getDataAxis().showAxisLabels();   
          			
          			chart.setCustomLines(mCustomLineDataset);
          		}
          		postInvalidate();            		
          	}            	
          }
          catch(Exception e) {
              Thread.currentThread().interrupt();
          }            
	}
			
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub		
		
		super.onTouchEvent(event);
				
		if(event.getAction() == MotionEvent.ACTION_UP) 
		{			
			//交叉线
			if(chart.getDyLineVisible())
			{
				chart.getDyLine().setCurrentXY(event.getX(),event.getY());
				if(chart.getDyLine().isInvalidate())this.invalidate();
			}
		}
		return true;
	}
	
}
