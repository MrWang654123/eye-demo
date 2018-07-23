package com.cheersmind.smartbrain.main.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Administrator on 2017/11/4.
 */

public class VerticalProgressBar extends View {

    private Paint paint;// 画笔
    private int progress;// 进度值
    private int width;// 宽度值
    private int height;// 高度值

    private int colorPaint;

    public VerticalProgressBar(Context context, AttributeSet attrs,
                               int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public VerticalProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VerticalProgressBar(Context context) {
        super(context);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.rgb(245, 164, 0));// 设置画笔颜色
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth() - 1;// 宽度值
        height = getMeasuredHeight() - 1;// 高度值
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        paint.setColor(Color.rgb(245, 164, 0));// 设置画笔颜色
//        paint.setColor(Color.rgb(18, 178, 244));// 设置画笔颜色

        RectF rect = new RectF();
        rect.left = 0;
        rect.top = (int) (height - progress / 100f * height);
        rect.right = width;
        rect.bottom = height;
        canvas.drawRoundRect(rect,10,10, paint);// 画圆角矩形
//        canvas.drawRect(0, height - progress / 100f * height, width, height, paint);// 画矩形

//        canvas.drawLine(0, 0, width, 0, paint);// 画顶边
//        canvas.drawLine(0, 0, 0, height, paint);// 画左边
//        canvas.drawLine(width, 0,
//        canvas.drawLine(0, height, width, height, paint);// 画底边 width, height, paint);// 画右边

//        paint.setColor(Color.RED);// 设置画笔颜色为红色
//        paint.setTextSize(width / 3);// 设置文字大小
//        canvas.drawText(progress + "%",
//                (width - getTextWidth(progress + "%")) / 2, height / 2, paint);// 画文字
        super.onDraw(canvas);
    }

    /**
     * 拿到文字宽度
     * @param str 传进来的字符串
     * return 宽度
     */
    private int getTextWidth(String str) {
        // 计算文字所在矩形，可以得到宽高
        Rect rect = new Rect();
        paint.getTextBounds(str, 0, str.length(), rect);
        return rect.width();
    }

    /** 设置progressbar进度 */
    public void setProgress(int progress) {
        this.progress = progress;
        postInvalidate();
    }

    public int getColorPaint() {
        return colorPaint;
    }

    public void setColorPaint(int colorPaint) {
        this.colorPaint = colorPaint;
        paint.setColor(colorPaint);
        this.invalidate();
    }
}
