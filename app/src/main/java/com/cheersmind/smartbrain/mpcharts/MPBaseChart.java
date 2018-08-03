package com.cheersmind.smartbrain.mpcharts;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.cheersmind.smartbrain.main.entity.ReportItemEntity;

/**
 * Created by Administrator on 2018/8/2.
 */

public class MPBaseChart extends LinearLayout {

    protected Context context;
    protected ReportItemEntity reportData;

    protected String[] mMonths = new String[] {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
    };

    protected String[] mParties = new String[] {
            "Party A", "Party B", "Party C", "Party D", "Party E", "Party F", "Party G", "Party H",
            "Party I", "Party J", "Party K", "Party L", "Party M", "Party N", "Party O", "Party P",
            "Party Q", "Party R", "Party S", "Party T", "Party U", "Party V", "Party W", "Party X",
            "Party Y", "Party Z"
    };

    protected Typeface mTfRegular;
    protected Typeface mTfLight;

    public MPBaseChart(Context context,ReportItemEntity reportData) {
        super(context);
        this.context = context;
        this.reportData = reportData;
        initdata();
        initChart();
    }

    public MPBaseChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initdata();
        initChart();
    }

    public MPBaseChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initdata();
        initChart();
    }

    public void initdata(){
        mTfRegular = Typeface.createFromAsset(context.getAssets(), "OpenSans-Regular.ttf");
        mTfLight = Typeface.createFromAsset(context.getAssets(), "OpenSans-Light.ttf");
    }

    protected void initChart(){

    }

}
