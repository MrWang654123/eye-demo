package com.cheersmind.cheersgenie.features.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.main.util.RepetitionClickUtil;

/**
 * 答题退出对话框
 */
public class QuestionQuitDialog extends Dialog implements View.OnClickListener{

    public static final int QUIT_TYPE_STOP = 1234;
    public static final int QUIT_TYPE_TIMEOUT = 1235;

    private ImageView ivIcon;
    private TextView tvExit;
    private TextView tvContinue;
    //提示内容
    private TextView tvContent;

    private Context context;
    private int type;
    private OnOperationListener listener;

    public QuestionQuitDialog(@NonNull Context context, int type, OnOperationListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
        this.type = type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.qs_dialog_ques_timeout);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setCanceledOnTouchOutside(true);
        setCancelable(true);

        initView();

    }

    private void initView(){
        ivIcon = (ImageView)findViewById(R.id.iv_icon);
        tvExit = (TextView)findViewById(R.id.tv_exit);
        tvExit.setOnClickListener(this);
        tvContinue = (TextView)findViewById(R.id.tv_continue);
        tvContinue.setOnClickListener(this);
        tvContent = findViewById(R.id.tv_content);

        if (type == QUIT_TYPE_STOP) {
            tvContent.setText(context.getResources().getString(R.string.question_stop_tip));
        } else {
            tvContent.setText(context.getResources().getString(R.string.question_timeout_tip));
        }
    }


    @Override
    public void onClick(View v) {
        if(!RepetitionClickUtil.isFastClick()){
            return;
        }
        if(v == tvExit){
            if(listener != null){
                listener.onQuesExit();
            }
            onDismiss();
        }else if(v == tvContinue){
            if(listener != null){
                listener.onQuesContinue();
            }
            onDismiss();
        }
    }

    private void onDismiss(){
        this.dismiss();
    }

    public interface OnOperationListener{

        void onQuesExit();

        void onQuesContinue();
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
