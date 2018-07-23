package com.cheersmind.smartbrain.main.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.cheersmind.smartbrain.R;

/**
 * Created by Administrator on 2018/6/14.
 */

public class NormalityView extends View {

    Paint mPaint;
    Paint mPaintText;
    int screenWidth;//手机屏幕宽度
    int viewWidth;//view的宽度
    int viewHeight;//view的高度
    private int mOffset = (int) (Math.PI / 4);
    private int progress;
    private int progressPercent;//外部传进来进度（0-100）
    private Bitmap bitmap;
    private int offsetY;//垂直方向偏移量

    public NormalityView(Context context) {
        super(context);
    }

    public NormalityView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintText.setTextSize(dp2px(10));
        mPaintText.setColor(Color.parseColor("#ffffff"));
        mPaintText.setStyle(Paint.Style.FILL);
        screenWidth = dm.widthPixels - dp2px(40);
        viewWidth = screenWidth * 3 / 4;
        viewHeight = screenWidth / 3;
//        progress = -viewWidth * 1 / 4;
        setProgress(50);

        offsetY = dp2px(16);
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.qs_factor_report_icon_bg);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCurve(canvas);
    }

    private void drawCurve(Canvas canvas) {
        canvas.save();
        float amplitude = viewHeight / 2 - dp2px(10);//波峰高度
        double defaultAngularFrequency = 2.0f * Math.PI / viewWidth;
        int i = -viewWidth * 1 / 4;
        int end = viewWidth * 3 / 4;


        for (; i <= end; i++) {
            double wx = defaultAngularFrequency * (i - mOffset);
            float curY = (float) (viewHeight - amplitude - amplitude * Math.sin(wx)) + offsetY;
            int startX = i + viewWidth * 1 / 4 + screenWidth / 8;
            if (i <= progress) {
                mPaint.setColor(0xcc1FB8CA);
                canvas.drawLine(startX, curY, startX, viewHeight + offsetY, mPaint);
            } else {
                mPaint.setColor(0x351FB8CA);
                canvas.drawLine(startX, curY, startX, viewHeight + offsetY, mPaint);
            }
            mPaint.setColor(0xff1FB8CA);
            canvas.drawPoint(startX, curY, mPaint);
            if (i == progress) {
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle((float) (startX + Math.PI / 4), curY, dp2px(3), mPaint);
                if (bitmap != null) {
                    canvas.drawBitmap(bitmap, startX - dp2px(40), curY - dp2px(30), new Paint());

                    String showText = String.valueOf(progressPercent)+"%";
                    float labelWidth = mPaint.measureText(showText);
                    int textOffset ;
                    if(showText.length() == 2){
                        textOffset = dp2px(15);
                    }else if(showText.length() == 4){
                        textOffset = dp2px(11);
                    }else{
                        textOffset = dp2px(13);
                    }

                    canvas.drawText(showText, startX - labelWidth - textOffset, curY - dp2px(17), mPaintText);
                }
            }

        }
        mPaint.setTextSize(dp2px(10));
        mPaint.setColor(0x99000000);
        mPaint.setStyle(Paint.Style.FILL);
//        float labelWidth = mPaint.measureText("100");
//        canvas.drawText("1", screenWidth / 8, viewHeight - dp2px(10), mPaint);
//        canvas.drawText("100", screenWidth * 7 / 8 - labelWidth, viewHeight - dp2px(10), mPaint);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(screenWidth, screenWidth *2/ 5);
    }

    private int dp2px(float dp) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     *
     * @param progressPercent(0-100)
     */
    public void setProgress(int progressPercent) {
        if(progressPercent>100){
            progressPercent = 100;
        }else if(progressPercent<0){
            progressPercent = 0;
        }
        this.progressPercent = progressPercent;
        this.progress = progressPercent * viewWidth/100 - viewWidth/4;
        invalidate();
    }
}
