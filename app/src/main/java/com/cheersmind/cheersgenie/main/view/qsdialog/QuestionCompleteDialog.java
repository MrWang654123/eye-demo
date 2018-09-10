package com.cheersmind.cheersgenie.main.view.qsdialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.main.entity.FactorInfoChildEntity;
import com.cheersmind.cheersgenie.main.entity.FactorInfoEntity;
import com.cheersmind.cheersgenie.main.util.RepetitionClickUtil;

/**
 * Created by Administrator on 2018/6/5.
 */

public class QuestionCompleteDialog extends Dialog implements View.OnClickListener{

    private TextView tvContent;
    private TextView tvModify;
    private TextView tvSure;

    private Context context;
    private FactorInfoChildEntity factorInfoChildEntity;
    private FactorInfoEntity factorInfoEntity;

    private FactorCompleteDialogCallback callback;

    public QuestionCompleteDialog(@NonNull Context context, FactorInfoEntity factorInfoEntity, FactorCompleteDialogCallback callback) {
        super(context);
        this.context = context;
        this.factorInfoEntity = factorInfoEntity;
        this.factorInfoChildEntity = factorInfoEntity.getChildFactor();
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.qs_dialog_ques_complete);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setCanceledOnTouchOutside(false);
        setCancelable(false);

        initView();

    }

    private void initView(){
        tvContent = (TextView)findViewById(R.id.tv_content);
        tvModify = (TextView)findViewById(R.id.tv_modify);
        tvModify.setOnClickListener(this);
        tvSure = (TextView)findViewById(R.id.tv_sure);
        tvSure.setOnClickListener(this);

        setTvContentText();
    }

    @Override
    public void onClick(View v) {
        if(!RepetitionClickUtil.isFastClick()){
            return;
        }
        if(v == tvModify){
            if(callback != null){
                callback.onBackModify();
            }
            onDismiss();
        }else if(v == tvSure){
            if(callback != null){
                callback.onSureCommit();
            }
            onDismiss();
        }
    }

    private void setTvContentText(){
        String stage = String.valueOf(factorInfoEntity.getStage());
        String text = context.getResources().getString(R.string.qs_factor_complete_hint,stage,factorInfoChildEntity.getFactorName());
        int length = String.valueOf(factorInfoEntity.getStage()).length();
        SpannableString spanString = new SpannableString(text);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#ffa200"));
        spanString.setSpan(colorSpan, 6, 6+length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tvContent.setText(spanString);
    }

    public void setFactorInfoChildEntity(FactorInfoChildEntity factorInfoChildEntity) {
        this.factorInfoChildEntity = factorInfoChildEntity;
        setTvContentText();
    }

    public void onDismiss(){
        this.dismiss();
    }

    public interface FactorCompleteDialogCallback{

        void onBackModify();

        void onSureCommit();
    }
}
