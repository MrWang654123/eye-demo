package com.cheersmind.cheersgenie.main.view.qsdialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.main.util.FileUtil;

/**
 * Created by Administrator on 2018/7/17.
 */

public class AppUpdateDialog extends Dialog {

    private Context context;
    private int forceUpdate;
    private ProgressBar downloadPb;
    private TextView tvProgress;

    public AppUpdateDialog(@NonNull Context context,int forceUpdate) {
        super(context);
        this.context = context;
        this.forceUpdate = forceUpdate;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_app_update);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        if(forceUpdate == 1){
            //强制更新
        }
        setCanceledOnTouchOutside(false);
        setCancelable(false);

        initView();
    }

    private void initView(){
        downloadPb = (ProgressBar)findViewById(R.id.download_pb);
        tvProgress = (TextView)findViewById(R.id.tv_progress);
    }

    public void setProgress(int done, int total){

        if(downloadPb != null){
            downloadPb.setMax(total);
            downloadPb.setProgress(done);
        }

        if(tvProgress!=null){
            tvProgress.setText(FileUtil.formetFileSize(done) + "/" + FileUtil.formetFileSize(total));
        }
    }
}
