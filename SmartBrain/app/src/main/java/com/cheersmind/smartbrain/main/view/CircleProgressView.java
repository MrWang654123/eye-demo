package com.cheersmind.smartbrain.main.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.cheersmind.smartbrain.R;
import com.cheersmind.smartbrain.main.util.DensityUtil;

/**
 * Created by goodm on 2017/4/24.
 */
public class CircleProgressView extends View {
    private static final String TAG = "CircleProgressBar";

    private int mMaxProgress = 100;
    private int mProgress = 0;
    private final int mCircleLineStrokeWidth = 2;
    private final int mTxtStrokeWidth = 2;

    // 画圆所在的距形区域
    private final RectF mRectF;
    private final Paint mPaint;
    private final Paint mPaint2;

    private final Context mContext;

    private int colorProgress;

    public CircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //setBackgroundResource(R.mipmap.ic_launcher);
        mContext = context;
        mRectF = new RectF();
        mPaint = new Paint();
        mPaint2 = new Paint();
        colorProgress = mContext.getResources().getColor(R.color.colorPrimary);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = this.getWidth();
        int height = this.getHeight();

        if (width != height) {
            int min = Math.min(width, height);
            width = min;
            height = min;
        }

        // 设置画笔相关属性
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.rgb(0xe9, 0xe9, 0xe9));
        canvas.drawColor(Color.TRANSPARENT);
        mPaint.setStrokeWidth(mCircleLineStrokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        // 位置
        mRectF.left = mCircleLineStrokeWidth / 2; // 左上角x
        mRectF.top = mCircleLineStrokeWidth / 2; // 左上角y
        mRectF.right = width - mCircleLineStrokeWidth / 2; // 左下角x
        mRectF.bottom = height - mCircleLineStrokeWidth / 2; // 右下角y

        // 绘制圆圈，进度条背景
        canvas.drawArc(mRectF, -90, 360, false, mPaint);
        //mPaint.setColor(Color.rgb(0xf8, 0x60, 0x30));
        mPaint.setColor(colorProgress);
        canvas.drawArc(mRectF, -90, ((float) mProgress / mMaxProgress) * 360, false, mPaint);

        // 绘制进度文案显示
//        mPaint.setStrokeWidth(mTxtStrokeWidth);
//        String text = mProgress + "%";
//        int textHeight = height / 4;
//        mPaint.setTextSize(textHeight);
//        int textWidth = (int) mPaint.measureText(text, 0, text.length());
//        mPaint.setStyle(Paint.Style.FILL);
//        canvas.drawText(text, width / 2 - textWidth / 2, height / 2 + textHeight / 2, mPaint);
//        canvas.drawText("已完成",getWidth()/2,height/2,mPaint);
//        mPaint.setTextSize(textHeight+20);
//        canvas.drawText(text,getWidth()/2,height*3/4,mPaint);

        mPaint.setStrokeWidth(mTxtStrokeWidth);
        String completedText = "已完成";
        //int textHeight = height/4;
        mPaint.setTextSize(DensityUtil.sp2px(mContext,12));
        int completedTextWidth = (int) mPaint.measureText(completedText, 0, completedText.length());
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawText(completedText, width + completedTextWidth / 2-25, height / 2 - 32, mPaint);

        mPaint2.setStrokeWidth(mTxtStrokeWidth);
        mPaint2.setColor(colorProgress);
        String progressText = mProgress + "%";
        //int textHeight = height/4;
        mPaint2.setTextSize(DensityUtil.sp2px(mContext,18));
        int progressTextWidth = (int) mPaint2.measureText(progressText, 0, progressText.length());
        mPaint2.setStyle(Paint.Style.FILL);
        if(mProgress==100){
            canvas.drawText(progressText, width + progressTextWidth / 2-45, height / 2 + 32, mPaint2);
        }else {
            canvas.drawText(progressText, width + progressTextWidth / 2-5, height / 2 + 32, mPaint2);
        }

    }

    public int getMaxProgress() {
        return mMaxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.mMaxProgress = maxProgress;
    }

    public void setProgress(int progress) {
        this.mProgress = progress;
        this.invalidate();
    }

    public int getColorProgress() {
        return colorProgress;
    }

    public void setColorProgress(int colorProgress) {
        this.colorProgress = colorProgress;
    }

    public void setProgressNotInUiThread(final int progress) {
        //this.mProgress = progress;
        //this.postInvalidate();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i=0;i<=progress;i++) {
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mProgress = i;
                    postInvalidate();
                }
            }
        }).start();
    }
}
