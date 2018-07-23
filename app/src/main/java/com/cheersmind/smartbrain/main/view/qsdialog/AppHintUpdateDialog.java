package com.cheersmind.smartbrain.main.view.qsdialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.cheersmind.smartbrain.R;
import com.cheersmind.smartbrain.main.entity.VersionEntity;

/**
 * Created by Administrator on 2018/7/18.
 */

public class AppHintUpdateDialog extends Dialog implements View.OnClickListener{

    private Context context;
    private VersionEntity versionEntity;
    private TextView tvDesc;
    private Button btnCancel;
    private Button btnSure;

    private AppHintUpdateCallback callback;

    public AppHintUpdateDialog(@NonNull Context context, VersionEntity versionEntity,AppHintUpdateCallback callback) {
        super(context);
        this.context = context;
        this.versionEntity = versionEntity;
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_app_hint_update);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        if(versionEntity.getForceUpdate() == 1){
            //强制更新
            setCanceledOnTouchOutside(false);
        }
        setCancelable(false);

        initView();
    }

    private void initView(){
        tvDesc = (TextView) findViewById(R.id.tv_desc);
        btnCancel = (Button)findViewById(R.id.btn_cancel);
        btnSure = (Button)findViewById(R.id.btn_sure);
        btnCancel.setOnClickListener(this);
        btnSure.setOnClickListener(this);

        if(!TextUtils.isEmpty(versionEntity.getDescription())){
            tvDesc.setText(Html.fromHtml(versionEntity.getDescription()));
        }

        if(versionEntity.getForceUpdate() == 1){
            btnCancel.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if(v == btnCancel){
            if(callback!=null){
                callback.onCancel();
            }
            onDissmis();
        }else if(v == btnSure){
            if(callback!=null){
                callback.onSure();
            }
            onDissmis();
        }
    }

    public void onDissmis(){
        this.dismiss();
    }

    public interface AppHintUpdateCallback{
        void onCancel();
        void onSure();
    }

}
