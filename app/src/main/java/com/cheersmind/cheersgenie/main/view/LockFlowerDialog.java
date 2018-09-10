package com.cheersmind.cheersgenie.main.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;

/**
 * Created by Administrator on 2017/10/7.
 */

public class LockFlowerDialog extends Dialog {

    private View.OnClickListener clickListener;

    private Button btnCancel;
    private Button btnSure;
    private TextView tvUnlockFlower;
    private int flower;

    public LockFlowerDialog(Context context, View.OnClickListener clickListener,int flower) {
        super(context);
        this.clickListener = clickListener;
        this.flower = flower;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_lock_flower);
        initView();
    }

    private void initView(){
        btnCancel = (Button)findViewById(R.id.btn_cancel);
        btnSure = (Button)findViewById(R.id.btn_sure);
        btnCancel.setOnClickListener(clickListener);
        btnSure.setOnClickListener(clickListener);
        tvUnlockFlower = (TextView)findViewById(R.id.unlock_flower);
        setUnlockFlower(flower);
    }

    public Button getBtnCancel() {
        return btnCancel;
    }

    public Button getBtnSure() {
        return btnSure;
    }

    public void setUnlockFlower(int flower){
        if(tvUnlockFlower!=null){
            tvUnlockFlower.setText(String.valueOf(flower));
        }
    }
}
