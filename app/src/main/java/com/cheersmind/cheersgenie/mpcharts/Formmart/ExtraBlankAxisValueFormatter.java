package com.cheersmind.cheersgenie.mpcharts.Formmart;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DecimalFormat;
import java.util.Collection;

/**
 * 左侧额外添加空格
 */
public class ExtraBlankAxisValueFormatter implements IAxisValueFormatter {

    //空格（宽）
//    String blank = "\u3000";
    String blank = " ";

    //最大长度
    int maxLen = 0;
    //额外空格数量
    private static final int extraBlankCount = 2;


    private String[] mValues = new String[] {};
    private int mValueCount = 0;


    /**
     * Constructor that specifies axis labels.
     *
     * @param values The values string array
     */
    public ExtraBlankAxisValueFormatter(String[] values) {
        if (values != null)
            setValues(values);
    }

    /**
     * Constructor that specifies axis labels.
     *
     * @param values The values string array
     */
    public ExtraBlankAxisValueFormatter(Collection<String> values) {
        if (values != null)
            setValues(values.toArray(new String[values.size()]));
    }

    public String getFormattedValue(float value, AxisBase axis) {
        int index = Math.round(value);

        if (index < 0 || index >= mValueCount || index != (int)value)
            return "";

        return " " +  blank + blank + mValues[index] + "";
    }

    public String[] getValues()
    {
        return mValues;
    }

    public void setValues(String[] values)
    {
        if (values == null)
            values = new String[] {};

        this.mValues = values;
        this.mValueCount = values.length;
    }

}
