package com.cheersmind.smartbrain.main.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.cheersmind.smartbrain.R;

/**
 * Created by Administrator on 2017/10/7.
 */

public class TimeOutDialog extends Dialog {

    private View.OnClickListener clickListener;
    private TextView tvTimeOut;

    public TimeOutDialog(Context context) {
        super(context);
    }

    public TimeOutDialog(Context context, View.OnClickListener clickListener) {
        super(context);
        this.clickListener = clickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_time_out);
        initView();
    }

    private void initView(){
        tvTimeOut = (TextView)findViewById(R.id.tv_time_out);
        tvTimeOut.setOnClickListener(clickListener);
    }

    public TextView getTvTimeOut() {
        return tvTimeOut;
    }
}
