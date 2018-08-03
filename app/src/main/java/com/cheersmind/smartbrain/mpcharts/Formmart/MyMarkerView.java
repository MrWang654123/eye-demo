package com.cheersmind.smartbrain.mpcharts.Formmart;

import android.content.Context;
import android.widget.TextView;

import com.cheersmind.smartbrain.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/8/2.
 */

public class MyMarkerView extends MarkerView {

    private TextView tvContent;
    private List<String> xLabels = new ArrayList<>();

    public MyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        tvContent = (TextView) findViewById(R.id.tvContent);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        if (e instanceof CandleEntry) {

            CandleEntry ce = (CandleEntry) e;

            tvContent.setText("" + Utils.formatNumber(ce.getHigh(), 0, true));
        } else if(e instanceof RadarEntry){
            int index = (int) highlight.getX();
            StringBuffer stringBuffer = new StringBuffer("");
            if(xLabels!=null && xLabels.size()>0){
                stringBuffer.append(xLabels.get(index));
                stringBuffer.append(":");
            }
            stringBuffer.append(String.valueOf(e.getY()));
            tvContent.setText(stringBuffer.toString());
        }else {
            StringBuffer stringBuffer = new StringBuffer("");
            if(xLabels!=null && xLabels.size()>0){
                stringBuffer.append(xLabels.get((int)e.getX()));
                stringBuffer.append(":");
            }
            stringBuffer.append(String.valueOf(e.getY()));
            tvContent.setText(stringBuffer.toString());
        }

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }

    public List<String> getxLabels() {
        return xLabels;
    }

    public void setxLabels(List<String> xLabels) {
        this.xLabels = xLabels;
    }
}
