package com.cheersmind.cheersgenie.features.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.main.util.RepetitionClickUtil;

/**
 * 答题完成对话框
 */
public class QuestionCompleteXDialog extends Dialog implements View.OnClickListener{

    private TextView tvCancel;
    private TextView tvComfirm;

    //操作监听
    private OnOperationListener listener;

    public QuestionCompleteXDialog(@NonNull Context context, OnOperationListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_question_complete);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setCanceledOnTouchOutside(true);
        setCancelable(true);

        initView();

    }

    private void initView(){
        tvCancel = (TextView)findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(this);
        tvComfirm = (TextView)findViewById(R.id.tv_confirm);
        tvComfirm.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(!RepetitionClickUtil.isFastClick()){
            return;
        }
        if(v == tvCancel){
            onDismiss();
            if(listener != null){
                listener.onCancel();
            }
        }else if(v == tvComfirm){
            onDismiss();
            if(listener != null){
                listener.onConfirm();
            }
        }
    }


    public void onDismiss(){
        this.dismiss();
    }

    //操作监听
    public interface OnOperationListener {

        //取消操作
        void onCancel();
        //确定操作
        void onConfirm();
    }

    /**
     * 清空监听
     */
    public void clearListener() {
        listener = null;
        setOnDismissListener(null);
        setOnCancelListener(null);
        setOnShowListener(null);
    }

}
