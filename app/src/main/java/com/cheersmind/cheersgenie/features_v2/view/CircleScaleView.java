package com.cheersmind.cheersgenie.features_v2.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.cheersmind.cheersgenie.R;

/**
 * 比例圆环
 */
public class CircleScaleView extends View {
    private int firstColor;//第一段颜色
    private int secondColor;//第二段颜色
    private int thirdlyColor;//第三段颜色
    private Paint mPaint;//画笔
    private int mRadius;//半径
    private float mCircleWidth;//圆环的宽度
    private int mWidth;
    private int mHeight;
    private RectF mRectF;
    private float firstPercent;//第一段比例
    private float secondPercent;//第二段比例
    private float thirdlyPercent;//第三段比例
    private int count = 0;//数量（2||3）

    public CircleScaleView(Context context) {
        this(context, null);
    }

    public CircleScaleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleScaleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleScaleView, defStyleAttr, 0);
        int n = array.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = array.getIndex(i);
            switch (attr) {
                case R.styleable.CircleScaleView_oneColor:
                    firstColor = array.getColor(attr, Color.parseColor("#fac62d"));
                    break;
                case R.styleable.CircleScaleView_twoColor:
                    secondColor = array.getColor(attr, Color.parseColor("#65cff6"));
                    break;
                case R.styleable.CircleScaleView_threeColor:
                    thirdlyColor = array.getColor(attr, Color.parseColor("#fe9a9c"));
                    break;
                case R.styleable.CircleScaleView_ringWidth:
                    mCircleWidth = array.getDimensionPixelSize(attr, dip2px(context, 20));
                    break;
                case R.styleable.CircleScaleView_radius:
                    mRadius = array.getDimensionPixelSize(attr, dip2px(context, 20));
                    break;
            }
        }
        array.recycle();
        initPaint();
        mRectF = new RectF(mCircleWidth / 2, mCircleWidth / 2, mRadius * 2 - mCircleWidth / 2, mRadius * 2 - mCircleWidth / 2);
    }

    //初始化画笔
    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    //设置圆环各段比例
    public void setCirclePercent(float first, float second, float thirdly) {
        float total = first + second + thirdly;
        firstPercent = first / total;
        secondPercent = second / total;
        thirdlyPercent = thirdly / total;
        count = 3;
        invalidate();//重绘
    }

    //设置圆环各段比例
    public void setCirclePercent(float first, float second) {
        float total = first + second;
        firstPercent = first / total;
        secondPercent = second / total;
        count = 2;
        invalidate();//重绘
    }

    //绘制圆环
    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setStrokeWidth(mCircleWidth);
//        mRectF = new RectF(mCircleWidth / 2, mCircleWidth / 2, mRadius * 2 - mCircleWidth / 2, mRadius * 2 - mCircleWidth / 2);
        //第一段圆环绘制
        float startAngle = -90;
        //绘制圆环的角度
        float sweepAngle = 360 * firstPercent;
        mPaint.setColor(firstColor);
        canvas.drawArc(mRectF, startAngle, sweepAngle, false, mPaint);
        //第二段圆环绘制
        startAngle = startAngle + sweepAngle;
        sweepAngle = 360 * secondPercent;
        mPaint.setColor(secondColor);
        canvas.drawArc(mRectF, startAngle, sweepAngle, false, mPaint);

        if (count == 3) {
            //第三段圆环绘制
            startAngle = startAngle + sweepAngle;
            sweepAngle = 360 * thirdlyPercent;
            mPaint.setColor(thirdlyColor);
            canvas.drawArc(mRectF, startAngle, sweepAngle, false, mPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //设置宽度
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY)// match_parent , accurate
        {
            mWidth = (int) (specSize + mCircleWidth);
        } else {
            if (specMode == MeasureSpec.AT_MOST)// wrap_content
            {
                mWidth = (mRadius * 2);
            }
        }
        //设置高度
        specMode = MeasureSpec.getMode(heightMeasureSpec);
        specSize = MeasureSpec.getSize(heightMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY)// match_parent , accurate
        {
            mHeight = (int) (specSize + mCircleWidth);
        } else {
            if (specMode == MeasureSpec.AT_MOST)// wrap_content
            {
                mHeight = (mRadius * 2);
            }
        }
        setMeasuredDimension(mWidth + 10, mHeight + 10);
    }

    //根据手机的分辨率从 dp 的单位 转成为 px(像素)
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    //设置圆环第二颜色
    public void setSecondColor(int secondColor) {
        this.secondColor = secondColor;
//        invalidate();//重绘
    }

}
