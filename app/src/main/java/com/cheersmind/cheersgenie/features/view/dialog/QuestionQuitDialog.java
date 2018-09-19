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

    private Context context;
    private int type;
    private QuestionQuitDialogCallback callback;

    public QuestionQuitDialog(@NonNull Context context, int type, QuestionQuitDialogCallback callback) {
        super(context);
        this.context = context;
        this.callback = callback;
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

    }

    public void setType(int type) {
        this.type = type;
        if(type == QUIT_TYPE_TIMEOUT){
            ivIcon.setImageResource(R.mipmap.dimension_icon_default);
        }else{
            ivIcon.setImageResource(R.mipmap.qs_factor_timeout_icon);
        }
    }

    @Override
    public void onClick(View v) {
        if(!RepetitionClickUtil.isFastClick()){
            return;
        }
        if(v == tvExit){
            if(callback != null){
                callback.onQuesExit();
            }
            onDismiss();
        }else if(v == tvContinue){
            if(callback != null){
                callback.onQuesContinue();
            }
            onDismiss();
        }
    }

    public void onDismiss(){
        this.dismiss();
    }

    public interface QuestionQuitDialogCallback{

        void onQuesExit();

        void onQuesContinue();
    }
}
