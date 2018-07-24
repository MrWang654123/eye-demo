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
 * @Description Android图表基类库演示
 * @author XiongChuanLiang<br/>(xcl_168@aliyun.com)
 * @license http://www.apache.org/licenses/  Apache v2 License
 * @version 1.7
 */
package com.cheersmind.smartbrain.xclcharts;

import android.content.Context;
import android.util.AttributeSet;

import com.cheersmind.smartbrain.main.entity.ReportFactorEntity;
import com.cheersmind.smartbrain.main.entity.ReportItemEntity;

import org.xclcharts.common.DensityUtil;
import org.xclcharts.view.ChartView;

import java.util.List;

/**
 * @ClassName DemoView
 * @Description  各个例子view的view基类
 * @author XiongChuanLiang<br/>(xcl_168@aliyun.com)
 */
public class DemoView extends ChartView {

	private MoveChartCallBack moveChartCallBack;

	private int curSelectLabel;

	public DemoView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	 public DemoView(Context context, AttributeSet attrs){   
	        super(context, attrs);   
	        
	 }
	 
	 public DemoView(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
		
	 }
	
	//Demo中bar chart所使用的默认偏移值。
	//偏移出来的空间用于显示tick,axistitle....
	protected int[] getBarLnDefaultSpadding()
	{
		int [] ltrb = new int[4];
		ltrb[0] = DensityUtil.dip2px(getContext(), 40); //left
		ltrb[1] = DensityUtil.dip2px(getContext(), 60); //top
		ltrb[2] = DensityUtil.dip2px(getContext(), 20); //right
		ltrb[3] = DensityUtil.dip2px(getContext(), 40); //bottom
		return ltrb;
	}
	
	protected int[] getPieDefaultSpadding()
	{
		int [] ltrb = new int[4];
		ltrb[0] = DensityUtil.dip2px(getContext(), 20); //left
		ltrb[1] = DensityUtil.dip2px(getContext(), 65); //top
		ltrb[2] = DensityUtil.dip2px(getContext(), 20); //right
		ltrb[3] = DensityUtil.dip2px(getContext(), 20); //bottom
		return ltrb;
	}

	public void updateChart(int curSelect){

	}

	public ReportItemEntity resetMax(ReportItemEntity reportData){
		if(reportData!=null){
			List<ReportFactorEntity> reportFactorEntities = reportData.getItems();
			double max = 0;
			if(reportFactorEntities!=null){
				for(ReportFactorEntity reportFactorEntity:reportFactorEntities){
					double childScore = Math.abs(reportFactorEntity.getChildScore());
					if(childScore > max){
						max = childScore;
					}
				}

				int offset = 10;

				if(max <=3){
					max = 5;
				}else if(max <=5){
					max = 10;
				}else if(max <=10){
					max  = 16;
				}else if(max <=20){
					max  = 20;
				}else if(max <=30){
					max  = 30;
				}else if(max <=40){
					max  = 40;
				}else if(max <=50){
					max  = 50;
				}else if(max <=60){
					max  = 60;
				}else if(max <=70){
					max  = 70;
				}else if(max <=80){
					max  = 80;
				}else if(max <=90){
					max  = 90;
				}else{
					max = 100;
				}
			}

			if(max>=20){
				max = max + 10;
			}

			reportData.setMaxScore((int)max);
			if(reportData.getMinScore()<0){
				reportData.setMinScore(-(int)max);
			}
		}
		return reportData;
	}
		
	@Override  
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {  
        super.onSizeChanged(w, h, oldw, oldh);  
    
    }

	public int getCurSelectLabel() {
		return curSelectLabel;
	}

	public void setCurSelectLabel(int curSelectLabel) {
		this.curSelectLabel = curSelectLabel;
	}

	public MoveChartCallBack getMoveChartCallBack() {
		return moveChartCallBack;
	}

	public void setMoveChartCallBack(MoveChartCallBack moveChartCallBack) {
		this.moveChartCallBack = moveChartCallBack;
	}


	public interface MoveChartCallBack{
		void onMove(float x,float y);
	}

}
