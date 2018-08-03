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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cheersmind.smartbrain.R;
import com.cheersmind.smartbrain.main.entity.DimensionInfoEntity;
import com.cheersmind.smartbrain.main.entity.ReportFactorEntity;
import com.cheersmind.smartbrain.main.entity.ReportItemEntity;
import com.cheersmind.smartbrain.main.entity.ReportResultEntity;
import com.cheersmind.smartbrain.main.helper.MPChartViewHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/6/8.
 */

public class DimensionReportDialog extends Dialog {

    private Context context;
    private DimensionInfoEntity entity;

    private ImageView ivClose;
    private TextView tvTitle;
    private TextView tvContent;
    private LinearLayout llStage;
    private TextView tvResultHead;
    private TextView tvStatus;
    private TextView tvStatusReuslt;
    private TextView tvResult;
    private TextView tvDesc;
    private LinearLayout llResult;

    private LinearLayout llChart;

    private List<ReportItemEntity> dimensionReports;
//    private List<ReportResultEntity> reportResultEntities;
    private List<ReportFactorEntity> factorEntities = new ArrayList<>();
//    private ReportRootEntity reportRootEntity;

    private DimensionReportCallback callback;

    public DimensionReportDialog(@NonNull Context context, List<ReportItemEntity> dimensionReports,DimensionInfoEntity entity,DimensionReportCallback callback) {
        super(context);
        this.context = context;
        this.dimensionReports = dimensionReports;
        this.entity = entity;
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.qs_dialog_dimension_report);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setCanceledOnTouchOutside(false);
        setCancelable(false);
        initView();
        initData();
    }

    private void initView(){
        ivClose = (ImageView)findViewById(R.id.iv_close);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(callback!=null){
                    callback.onClose();
                }
                onDismiss();
            }
        });
        tvTitle = (TextView)findViewById(R.id.tv_title);
        tvContent = (TextView)findViewById(R.id.tv_content);
        llStage = (LinearLayout)findViewById(R.id.ll_stage);
        tvResult = (TextView)findViewById(R.id.tv_result);
        tvStatus = (TextView)findViewById(R.id.tv_status);
        tvResultHead = (TextView)findViewById(R.id.tv_result_head);
        tvStatusReuslt = (TextView)findViewById(R.id.tv_status_result);
        tvDesc = (TextView)findViewById(R.id.tv_desc);

        llChart = (LinearLayout) findViewById(R.id.ll_chart);
        llResult = (LinearLayout)findViewById(R.id.ll_result);

    }

    private void initData(){

        if(dimensionReports!=null && dimensionReports.size()>0){
            for(int i=0;i<dimensionReports.size();i++){
                if(dimensionReports.get(i).getItems()!=null && dimensionReports.get(i).getItems().size()>0){
                    factorEntities.addAll(dimensionReports.get(i).getItems());
                }
            }
            String chartName = dimensionReports.get(0).getChartItemName();
            if(!TextUtils.isEmpty(chartName)){
                tvTitle.setText(chartName);
            }else{
                if(entity!=null && !TextUtils.isEmpty(entity.getDimensionName())){
                    tvTitle.setText(entity.getDimensionName());
                }else{
                    tvTitle.setText("");
                }
            }

            tvContent.setText(context.getResources().getString(R.string.qs_dimension_report_hint,
                    String.valueOf(factorEntities.size())));
            if(!TextUtils.isEmpty(dimensionReports.get(0).getChartDescription())){
                tvDesc.setText(Html.fromHtml(dimensionReports.get(0).getChartDescription()));
            }else{
                tvDesc.setVisibility(View.GONE);
            }
            ReportResultEntity reportResultEntity =  dimensionReports.get(0).getReportResult();
            if(reportResultEntity!=null){
                llResult.setVisibility(View.VISIBLE);
                if(!TextUtils.isEmpty(reportResultEntity.getHeader())){
                    tvResultHead.setText(reportResultEntity.getHeader());
                }
                if(!TextUtils.isEmpty(reportResultEntity.getTitle())){
                    tvStatus.setText(Html.fromHtml(reportResultEntity.getTitle()));
                }else{
                    tvStatus.setVisibility(View.GONE);
                }
                if(!TextUtils.isEmpty(reportResultEntity.getResult())){
                    tvStatusReuslt.setText(Html.fromHtml(reportResultEntity.getResult()));
                }else{
                    tvStatusReuslt.setVisibility(View.GONE);
                }
                if(!TextUtils.isEmpty(reportResultEntity.getColor())){
                    tvStatus.setTextColor(Color.parseColor(reportResultEntity.getColor()));
                }
                if(!TextUtils.isEmpty(reportResultEntity.getContent())){
                    tvResult.setText(Html.fromHtml(reportResultEntity.getContent()));
                }else{
                    tvResult.setVisibility(View.GONE);
                }
            }else{
                llResult.setVisibility(View.GONE);
            }
            updateStageLayout(llStage);
//            ReportItemEntity reportItemEntity = dimensionReports.get(0);
//            reportItemEntity.setItems(factorEntities);
//            ChartViewHelper.addChartView(context,llChart,dimensionReports);
            MPChartViewHelper.addMpChartView(context,llChart,dimensionReports);
        }else{
            if(entity==null){
                return;
            }
            if(TextUtils.isEmpty(entity.getDimensionName())){
                tvTitle.setText("无");
            }else{
                tvTitle.setText(entity.getDimensionName());
            }

            tvContent.setText(context.getResources().getString(R.string.qs_dimension_report_hint,String.valueOf(entity.getFactorCount())));

            updateStageLayout(llStage);

            if(TextUtils.isEmpty(entity.getDefinition())){
                tvDesc.setText("无");
            }else{
                tvDesc.setText(entity.getDefinition());
            }

            tvStatus.setText("");
        }
    }

    private void updateStageLayout(LinearLayout llStage){
        llStage.removeAllViews();
        for(int i=0;i<factorEntities.size();i++){
            View itemView = View.inflate(context,R.layout.qs_factor_stage_item,null);
            TextView tv = (TextView) itemView.findViewById(R.id.tv_bg);
            tv.setText(String.valueOf(i+1));
            tv.setBackgroundResource(R.mipmap.qs_number_bg_select);
            llStage.addView(itemView);
        }
    }

    private String changeTowLineText(String str){
        StringBuffer stringBuffer = new StringBuffer();
        char[] chars = str.toCharArray();
        if(chars.length == 1){
            return str;
        }
        int middle = chars.length/2;
        for(int i=0;i<chars.length;i++){
            stringBuffer.append(chars[i]);
            if(i == middle-1){
                stringBuffer.append("\n");
            }
        }

        return stringBuffer.toString();
    }

    public void onDismiss(){
        this.dismiss();
    }

    public interface DimensionReportCallback{
        void onClose();
    }
}
