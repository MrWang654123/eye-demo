package com.cheersmind.smartbrain.main.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class FactorsCharView extends View {

    private int viewWidth = 720;
    private int viewHeight = 300;

    private int curIndex = 0;
    private String showText = "0%";
    private boolean isHundred;//一百特殊处理

    private float circleX;
    private float circleY;

    public FactorsCharView(Context context, AttributeSet attrs) {
        super(context, attrs);
// TODO Auto-generated constructor stub
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        final int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        final int heightSpecSize = MeasureSpec.getMode(heightMeasureSpec);
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(viewWidth, viewHeight);
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(viewWidth, heightSpecSize);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, viewHeight);
        }
    }

    //重写onDraw方法
    @SuppressLint({"DrawAllocation", "NewApi"})
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制sin曲线
        Paint sinPaint = new Paint();
        sinPaint.setStyle(Paint.Style.FILL);
        sinPaint.setColor(Color.parseColor("#a2e4ff"));
        sinPaint.setAntiAlias(true);
        sinPaint.setStrokeWidth(2);

        //绘制填充线
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(3);
        paint.setColor(Color.parseColor("#a2e4ff"));

        for (int i = 0; i < 360; i++) {
            double x = -1.5*Cos(i);//获取sin值
            double y = -1.5*Cos(i + 1);
            canvas.drawLine((float) ((float) i * (float) ((viewWidth * 1.0) / 360)), (float) (180 - x * 80), (float) ((float) (i + 1) * (float) ((viewWidth * 1.0) / 360)), (float) (180 - y * 80), sinPaint);
            canvas.drawLine((float) ((float) i * (float) ((viewWidth * 1.0) / 360)), (float) (180 - x * 80), (float) ((float) (i + 1) * (float) ((viewWidth * 1.0) / 360)), viewHeight, paint);
            if(curIndex==100){
                curIndex = 99;
            }
            int index = (int) (curIndex * 3.6);
            if (i == index) {
                circleX = ((float) i * (float) ((viewWidth * 1.0) / 360));
                circleY = (float) (180 - x * 80);

                Paint textPaint = new Paint();
                textPaint.setStyle(Paint.Style.STROKE);
                textPaint.setAntiAlias(true);
                textPaint.setColor(Color.parseColor("#ffd262"));
                textPaint.setStrokeWidth(2);
                textPaint.setTextSize(30f);
                if(index <=18 ){
                    canvas.drawText(showText, ((float) i * (float) ((viewWidth * 1.0) / 360)), (float) (180 - x * 80)-30,textPaint);
                }else if(index>340){
                    if(isHundred){
                        canvas.drawText(showText, ((float) i * (float) ((viewWidth * 1.0) / 360))-70, (float) (180 - x * 80)-30,textPaint);
                    }else{
                        canvas.drawText(showText, ((float) i * (float) ((viewWidth * 1.0) / 360))-50, (float) (180 - x * 80)-30,textPaint);
                    }
                }else{
                    canvas.drawText(showText, ((float) i * (float) ((viewWidth * 1.0) / 360))-20, (float) (180 - x * 80)-30,textPaint);

                }
            }
        }

        Paint circlePaint = new Paint();
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setAntiAlias(true);
        circlePaint.setStrokeWidth(2);
        circlePaint.setColor(Color.WHITE);
        canvas.drawLine(circleX,circleY,circleX,viewHeight,circlePaint);
        canvas.drawCircle(circleX,circleY, 6, circlePaint);
    }

    public double Sin(int i) {
        double result = 0;
        //在这里我是写sin函数，其实可以用cos，tan等函数的，不信大家尽管试试
        //result = Math.cos(i * Math.PI / 180);
        result = Math.sin(i * Math.PI / 180);
        //result = Math.tan(i * Math.PI / 180);
        return result;
    }

    public double Cos(int i) {
        double result = 0;
        //在这里我是写sin函数，其实可以用cos，tan等函数的，不信大家尽管试试
        //result = Math.cos(i * Math.PI / 180);
        result = Math.cos(i * Math.PI / 180);
        //result = Math.tan(i * Math.PI / 180);
        return result;
    }

    public int getCurIndex() {
        return curIndex;
    }

    public void setCurIndex(int curIndex) {
        this.curIndex = curIndex;
        if(curIndex == 100){
            isHundred = true;
        }else{
            isHundred = false;
        }
    }

    public String getShowText() {
        return showText;
    }

    public void setShowText(String showText) {
        this.showText = showText;
    }
}
