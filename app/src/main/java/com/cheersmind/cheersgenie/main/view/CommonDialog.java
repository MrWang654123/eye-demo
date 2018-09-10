package com.cheersmind.cheersgenie.main.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;

/**
 * Created by Administrator on 2017/9/26.
 */

public class CommonDialog extends Dialog {

    private Context context;
    private String title;
    private String message;
    private String positiveButtonText;
    private String negativeButtonText;

    private TextView tvTitle;
    private TextView tvMessage;
    private Button positiveButton;
    private Button negativeButton;
    private View.OnClickListener negativeClick;
    private View.OnClickListener positiveClick;

    public CommonDialog(Context context) {
        super(context);
        this.context = context;
    }

    public CommonDialog(Context context,String title,String message,String negativeButtonText,String positiveButtonText) {
            super(context);
        this.title = title;
        this.message = message;
        this.negativeButtonText = negativeButtonText;
        this.positiveButtonText = positiveButtonText;
    }

    public CommonDialog(Context context, String title, String message, String negativeButtonText, String positiveButtonText,
                        View.OnClickListener negativeClick, View.OnClickListener positiveClick) {
        super(context);
        this.title = title;
        this.message = message;
        this.negativeButtonText = negativeButtonText;
        this.positiveButtonText = positiveButtonText;
        this.negativeClick = negativeClick;
        this.positiveClick = positiveClick;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_common);
        initView();
    }

    private void initView() {
        tvTitle = (TextView)findViewById(R.id.title);
        tvMessage = (TextView)findViewById(R.id.message);
        positiveButton = (Button)findViewById(R.id.positiveButton);
        negativeButton = (Button)findViewById(R.id.negativeButton);

        if(TextUtils.isEmpty(title)){
            tvTitle.setVisibility(View.GONE);
        }else{
            tvTitle.setText(title);
        }

        if(TextUtils.isEmpty(message)){
            tvMessage.setText("");
        }else{
            tvMessage.setText(message);
        }

        if(TextUtils.isEmpty(negativeButtonText)){
            negativeButton.setVisibility(View.GONE);
        }else{
            negativeButton.setText(negativeButtonText);
            negativeButton.setVisibility(View.VISIBLE);
            if(negativeClick==null){
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });
            }else{
                negativeButton.setOnClickListener(negativeClick);
            }

        }

        if(TextUtils.isEmpty(positiveButtonText)){
            positiveButton.setVisibility(View.GONE);
        }else{
            positiveButton.setText(positiveButtonText);
            positiveButton.setVisibility(View.VISIBLE);
            if(positiveClick==null){
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });
            }else{
                positiveButton.setOnClickListener(positiveClick);
            }

        }
    }
}