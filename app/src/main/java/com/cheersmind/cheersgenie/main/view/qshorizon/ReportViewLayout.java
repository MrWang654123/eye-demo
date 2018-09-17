package com.cheersmind.cheersgenie.main.view.qshorizon;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.dao.ChildInfoDao;
import com.cheersmind.cheersgenie.main.entity.DimensionInfoEntity;
import com.cheersmind.cheersgenie.main.entity.ReportFactorEntity;
import com.cheersmind.cheersgenie.main.entity.ReportItemEntity;
import com.cheersmind.cheersgenie.main.entity.ReportResultEntity;
import com.cheersmind.cheersgenie.main.entity.ReportRootEntity;
import com.cheersmind.cheersgenie.main.helper.MPChartViewHelper;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.ToastUtil;
import com.cheersmind.cheersgenie.main.view.LoadingView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/8/7.
 */

public class ReportViewLayout extends RelativeLayout {

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
    private TextView tvNone;

    private List<ReportItemEntity> dimensionReports;
    private List<ReportFactorEntity> factorEntities = new ArrayList<>();

    private DimensionReportCallback callback;

    public ReportViewLayout(Context context,List<ReportItemEntity> dimensionReports,final DimensionInfoEntity entity,DimensionReportCallback callback) {
        super(context);
        this.context = context;
        this.dimensionReports = dimensionReports;
        this.entity = entity;
        this.callback = callback;
        initView();
        if(dimensionReports != null){
            initData();
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getReportData(entity);
                }
            },300);

        }
    }

    private void initView(){
        View view = View.inflate(context, R.layout.report_view_layout,null);
        this.addView(view);

        ivClose = (ImageView)findViewById(R.id.iv_close);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(callback!=null){
                    callback.onClose();
                }
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
        tvNone = (TextView)findViewById(R.id.tv_none);
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
                tvDesc.setVisibility(View.VISIBLE);
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
                tvDesc.setVisibility(View.VISIBLE);
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

    private void getReportData(DimensionInfoEntity dimensionInfoEntity){
        LoadingView.getInstance().show(context);
        DataRequestService.getInstance().getTopicReportByRelation(ChildInfoDao.getDefaultChildId(),
                dimensionInfoEntity.getChildDimension().getExamId(),
                dimensionInfoEntity.getTopicDimensionId(),
                MPChartViewHelper.REPORT_RELATION_TOPIC_DIMENSION,
                "0", new BaseService.ServiceCallback() {
                    @Override
                    public void onFailure(QSCustomException e) {
                        LoadingView.getInstance().dismiss();
                        ToastUtil.showShort(context,"获取量表报告失败");
                    }

                    @Override
                    public void onResponse(Object obj) {
                        LoadingView.getInstance().dismiss();
                        if(obj!=null){
                            Map dataMap = JsonUtil.fromJson(obj.toString(),Map.class);
                            ReportRootEntity data = InjectionWrapperUtil.injectMap(dataMap,ReportRootEntity.class);
                            if(data!=null && data.getChartDatas()!=null){

                                if(data.getChartDatas().size()==0 && data.getReportResults().size()==0){
//                                    ToastUtil.showShort(context,"感谢您的信息搜集");
                                    tvNone.setVisibility(View.VISIBLE);
                                    return;
                                }
                                List<ReportResultEntity> reportResultEntities = data.getReportResults();
                                List<ReportItemEntity>  dimensionReports = data.getChartDatas();
                                if(dimensionReports!=null && dimensionReports.size()>0) {
                                    for (int i = 0; i < dimensionReports.size(); i++) {
                                        if (reportResultEntities != null && reportResultEntities.size() > 0) {
                                            if (dimensionReports.get(i).getReportResult() == null) {
                                                if(dimensionReports.get(i).getReportResult()==null){
                                                    dimensionReports.get(i).setReportResult(reportResultEntities.get(0));
                                                }
                                            }
                                        }
                                    }
                                }

                                setDimensionReports(dimensionReports);

                            }

                        }
                    }
                });
    }

    public void setDimensionReports(List<ReportItemEntity> dimensionReports) {
        this.dimensionReports = dimensionReports;
        initData();
    }

    public interface DimensionReportCallback{
        void onClose();
    }


}
