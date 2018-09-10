package com.cheersmind.cheersgenie.main.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;

/**
 * Created by Administrator on 2017/10/7.
 */

public class ContinueDialog extends Dialog {

    private View.OnClickListener clickListener;
    private TextView tvContinue;
    private TextView tvQuit;

    public ContinueDialog(Context context) {
        super(context);
    }

    public ContinueDialog(Context context, View.OnClickListener clickListener) {
        super(context);
        this.clickListener = clickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_continue);
        initView();
    }

    private void initView(){
        tvContinue = (TextView)findViewById(R.id.tv_continue);
        tvQuit = (TextView)findViewById(R.id.tv_quit);
        tvContinue.setOnClickListener(clickListener);
        tvQuit.setOnClickListener(clickListener);
    }

    public TextView getTvContinue() {
        return tvContinue;
    }

    public TextView getTvQuit() {
        return tvQuit;
    }
}
