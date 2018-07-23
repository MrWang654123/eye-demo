package com.cheersmind.smartbrain.main.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cheersmind.smartbrain.R;
import com.cheersmind.smartbrain.main.entity.DimensionInfoEntity;
import com.cheersmind.smartbrain.main.entity.ReportItemEntity;
import com.cheersmind.smartbrain.main.entity.ReportResultEntity;
import com.cheersmind.smartbrain.main.util.OnMultiClickListener;
import com.cheersmind.smartbrain.main.view.qsdialog.DimensionReportDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/6/13.
 */

public class DimensionReportFragment extends Fragment {

    public static final String DIMENSION_REPORT_DATA_KEY = "dimension_report_data_key";
    public static final String DIMENSION_REPORT_STAGE_KEY = "dimension_report_stage_key";

    private View contentView;

    private TextView tvStage;
    private TextView tvDimensionName;
    private TextView tvResultTitle;
    private TextView tvResultStatus;
    private TextView tvResultContent;
    private TextView tvLockDetail;
    private TextView tvNone;
    private LinearLayout llReport;

    private List<ReportItemEntity> dimensionReports = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(contentView == null){
            contentView = inflater.inflate(R.layout.qs_fragment_dimension_report, container,
                    false);
        }

        initView();
        initData();
        return contentView;
    }

    private void initView(){
        llReport = (LinearLayout)contentView.findViewById(R.id.ll_report);
        tvStage = (TextView)contentView.findViewById(R.id.tv_stage);
        tvDimensionName = (TextView)contentView.findViewById(R.id.tv_dimension_name);
        tvResultTitle = (TextView)contentView.findViewById(R.id.tv_result_title);
        tvResultStatus = (TextView)contentView.findViewById(R.id.tv_result_status);
        tvResultContent = (TextView)contentView.findViewById(R.id.tv_result_content);
        tvNone = (TextView)contentView.findViewById(R.id.tv_none);
        tvLockDetail = (TextView)contentView.findViewById(R.id.tv_lock_detail);
        tvLockDetail.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                DimensionReportDialog dimensionReportDialog = new DimensionReportDialog(getActivity(), dimensionReports,new DimensionInfoEntity(), new DimensionReportDialog.DimensionReportCallback() {
                    @Override
                    public void onClose() {

                    }
                });
                dimensionReportDialog.show();
            }
        });
    }

    private void initData(){
        Bundle bundle = getArguments();
        if(bundle!=null){
            String stage = bundle.getString(DIMENSION_REPORT_STAGE_KEY);
            if(!TextUtils.isEmpty(stage)){
                tvStage.setText(stage);
            }
            List<ReportItemEntity> dimensionReports = (List<ReportItemEntity>) bundle.getSerializable(DIMENSION_REPORT_DATA_KEY);
            updateData(dimensionReports);
        }else{
            llReport.setVisibility(View.GONE);
            tvNone.setVisibility(View.VISIBLE);
        }
    }

    public void updateData(List<ReportItemEntity> dimensionReports){
        this.dimensionReports = dimensionReports;
        if(dimensionReports!=null && dimensionReports.size()>0){

            ReportItemEntity reportItemEntity = dimensionReports.get(0);
            String dimensionName = reportItemEntity.getChartItemName();
            tvDimensionName.setText(getActivity().getResources().getString(R.string.qs_dimension_report_reuslt,dimensionName));

            if(dimensionReports.get(0).getItems()==null
                    || dimensionReports.get(0).getItems().size()==0
                    || dimensionReports.get(0).getReportResult()==null){
                tvNone.setVisibility(View.VISIBLE);
                llReport.setVisibility(View.GONE);
                return;
            }

            llReport.setVisibility(View.VISIBLE);
            tvNone.setVisibility(View.GONE);

            ReportResultEntity reportResultEntity = reportItemEntity.getReportResult();
            if(reportResultEntity!=null){
                if(!TextUtils.isEmpty(reportResultEntity.getTitle())){
                    tvResultTitle.setText(reportResultEntity.getTitle());
                }
                if(!TextUtils.isEmpty(reportResultEntity.getColor())){
                    tvResultTitle.setTextColor(Color.parseColor(reportResultEntity.getColor()));
                }

                if(!TextUtils.isEmpty(reportResultEntity.getContent())){
                    tvResultContent.setText(reportResultEntity.getContent());
                }
            }
        }else{
            llReport.setVisibility(View.GONE);
            tvNone.setVisibility(View.VISIBLE);
        }
    }

}

