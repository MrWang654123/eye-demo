package com.cheersmind.smartbrain.main.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cheersmind.smartbrain.R;
import com.cheersmind.smartbrain.main.Exception.QSCustomException;
import com.cheersmind.smartbrain.main.activity.MainActivity;
import com.cheersmind.smartbrain.main.dao.ChildInfoDao;
import com.cheersmind.smartbrain.main.entity.DimensionInfoEntity;
import com.cheersmind.smartbrain.main.entity.ReportItemEntity;
import com.cheersmind.smartbrain.main.entity.ReportResultEntity;
import com.cheersmind.smartbrain.main.entity.ReportRootEntity;
import com.cheersmind.smartbrain.main.entity.TopicInfoEntity;
import com.cheersmind.smartbrain.main.helper.ChartViewHelper;
import com.cheersmind.smartbrain.main.helper.MPChartViewHelper;
import com.cheersmind.smartbrain.main.service.BaseService;
import com.cheersmind.smartbrain.main.service.DataRequestService;
import com.cheersmind.smartbrain.main.util.InjectionWrapperUtil;
import com.cheersmind.smartbrain.main.util.JsonUtil;
import com.cheersmind.smartbrain.main.util.LogUtils;
import com.cheersmind.smartbrain.main.util.OnMultiClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/8/3.
 */

public class TopicDimensionReportFragmet extends Fragment {

    public final static String TOPIC_DIM_REPORT = "topic_report";

    private View contentView;
    private ListView lvReport;
    private RelativeLayout rtNoReport;

    private View headView;
    private RelativeLayout rtRootReport;
    private LinearLayout llChart;
    private LinearLayout llTopicResult;
    private TextView tvTopicHeader;
    private TextView tvTopicContent;
    private TextView tvTopicResult;

    private TopicInfoEntity topicInfoEntity;
    private ReportRootEntity reportData;
    List<ReportItemEntity> topicReports = new ArrayList<>();
    List<List<ReportItemEntity>> dimensionReports = new ArrayList<>();//每个量表可能不只一个图表

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(contentView == null){
            contentView = inflater.inflate(R.layout.fragment_topic_dimension_report, container,
                    false);
        }

        initView();
        initData();
        return contentView;
    }

    private void initView(){
        headView = View.inflate(getActivity(),R.layout.fragment_topic_dimension_report_head,null);
        rtRootReport = (RelativeLayout)headView.findViewById(R.id.rt_report);
        llChart = (LinearLayout)headView.findViewById(R.id.ll_chart);
        llTopicResult = (LinearLayout)headView.findViewById(R.id.ll_topic_result);
        tvTopicHeader = (TextView)headView.findViewById(R.id.tv_topic_header);
        tvTopicContent = (TextView)headView.findViewById(R.id.tv_topic_content);
        tvTopicResult = (TextView)headView.findViewById(R.id.tv_topic_result);

        rtNoReport = (RelativeLayout)contentView.findViewById(R.id.rt_no_report);
        lvReport = (ListView)contentView.findViewById(R.id.lv_report);
        lvReport.addHeaderView(headView);
        lvReport.setAdapter(adapter);
    }

    private void initData(){
        Bundle bundle = getArguments();
        if(bundle!=null){
            topicInfoEntity = (TopicInfoEntity) bundle.getSerializable("topic_report");
            loadReportData(topicInfoEntity);
        }
    }

    BaseAdapter adapter = new BaseAdapter() {
        @Override
        public int getCount() {
            if(dimensionReports!=null && dimensionReports.size()>0
                    && dimensionReports.get(0).get(0).getItems()!=null
                    && dimensionReports.get(0).get(0).getItems().size()>0){
                return dimensionReports.size();
            }else{
                return 0;
            }
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null){
                convertView = View.inflate(getActivity(),R.layout.item_topic_dimension_report,null);
                holder = new ViewHolder();
                holder.initView(convertView);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder)convertView.getTag();
            }

            holder.tvStage.setText(String.valueOf(position+1));

            final List<ReportItemEntity> reportItemEntities = dimensionReports.get(position);

            ReportItemEntity reportItemEntity = reportItemEntities.get(0);
            String dimensionName = reportItemEntity.getChartItemName();
            holder.tvDimensionName.setText(getActivity().getResources().getString(R.string.qs_dimension_report_reuslt,dimensionName));

            ReportResultEntity reportResultEntity = reportItemEntity.getReportResult();
            if(reportResultEntity!=null){
                if(!TextUtils.isEmpty(reportResultEntity.getTitle())){
                    holder.tvResultTitle.setText(Html.fromHtml(reportResultEntity.getTitle()));
                }else{
                    holder.tvResultTitle.setText("");
                }
                if(!TextUtils.isEmpty(reportResultEntity.getColor())){
                    holder.tvResultTitle.setTextColor(Color.parseColor(reportResultEntity.getColor()));
                }

                if(!TextUtils.isEmpty(reportResultEntity.getContent())){
                    holder.tvResultContent.setText(Html.fromHtml(reportResultEntity.getContent()));
                }else{
                    holder.tvResultContent.setText("");
                }
            }else{
                holder.tvResultTitle.setText("");
                if(topicInfoEntity.getDimensions()!=null && topicInfoEntity.getDimensions().size()>position){
                    String resu = topicInfoEntity.getDimensions().get(position).getDefinition();
                    holder.tvResultContent.setText(resu);
                }else{
                    holder.tvResultContent.setText("暂无文字评价，请查看图表");
                }

            }

            holder.tvLockDetail.setOnClickListener(new OnMultiClickListener() {
                @Override
                public void onMultiClick(View view) {
//                    DimensionReportDialog dimensionReportDialog = new DimensionReportDialog(getActivity(), reportItemEntities,new DimensionInfoEntity(), new DimensionReportDialog.DimensionReportCallback() {
//                        @Override
//                        public void onClose() {
//
//                        }
//                    });
//                    dimensionReportDialog.show();
                    showReportPanel(reportItemEntities,new DimensionInfoEntity());
                }
            });
            return convertView;
        }
    };

    class ViewHolder{

        TextView tvStage;
        TextView tvDimensionName;
        TextView tvResultTitle;
        TextView tvResultStatus;
        TextView tvResultContent;
        TextView tvLockDetail;
        LinearLayout llReport;

        void initView(View view){
            llReport = (LinearLayout)view.findViewById(R.id.ll_report);
            tvStage = (TextView)view.findViewById(R.id.tv_stage);
            tvDimensionName = (TextView)view.findViewById(R.id.tv_dimension_name);
            tvResultTitle = (TextView)view.findViewById(R.id.tv_result_title);
            tvResultStatus = (TextView)view.findViewById(R.id.tv_result_status);
            tvResultContent = (TextView)view.findViewById(R.id.tv_result_content);
            tvLockDetail = (TextView)view.findViewById(R.id.tv_lock_detail);
        }
    }

    private void showReportPanel(List<ReportItemEntity> dimensionReports, DimensionInfoEntity entity){
        if(getActivity() instanceof MainActivity){
            MainActivity mainActivity = (MainActivity)getActivity();
            mainActivity.showReportPanel(dimensionReports,entity);
        }
    }

    private void updateHeadView(){
        if(topicReports!=null && topicReports.size()>0
                && topicReports.get(0).getItems()!=null && topicReports.get(0).getItems().size()>0){
            rtRootReport.setVisibility(View.VISIBLE);

            ReportItemEntity reportItemEntity = topicReports.get(0);
            if(topicReports.size()>1){
                for(int i=1;i<topicReports.size();i++){
                    reportItemEntity.getItems().addAll(topicReports.get(i).getItems());
                }
            }
            if(reportItemEntity.getItems()!=null && reportItemEntity.getItems().size()>0){
                llChart.removeAllViews();
                List<ReportItemEntity> reportItemEntities = new ArrayList<>();
                reportItemEntities.add(reportItemEntity);
                MPChartViewHelper.addMpChartView(getActivity(), llChart, reportItemEntities);
            }

            ReportResultEntity topicResult = topicReports.get(0).getReportResult();
            if(topicResult != null){
                llTopicResult.setVisibility(View.VISIBLE);
                if(!TextUtils.isEmpty(topicResult.getHeader())){
                    tvTopicHeader.setText(topicResult.getHeader());
                }

                if(!TextUtils.isEmpty(topicResult.getTitle())){
                    tvTopicContent.setText(Html.fromHtml(topicResult.getTitle()));
                }

                if(!TextUtils.isEmpty(topicResult.getContent())){
                    tvTopicResult.setText(Html.fromHtml(topicResult.getContent()));
                }
            }else{
                llTopicResult.setVisibility(View.GONE);
            }
        }else{
            rtRootReport.setVisibility(View.GONE);
        }

        if(dimensionReports==null || dimensionReports.size()==0
                || dimensionReports.get(0).get(0).getItems()==null
                || dimensionReports.get(0).get(0).getItems().size()==0){
            if(rtRootReport.getVisibility() == View.GONE){
                rtNoReport.setVisibility(View.VISIBLE);
            }else{
                rtNoReport.setVisibility(View.GONE);
            }
        }else{
            rtNoReport.setVisibility(View.GONE);
        }
    }

    private void initReportData(ReportRootEntity reportData){
        this.reportData = reportData;
        topicReports.clear();
        dimensionReports.clear();
        List<ReportItemEntity> reportItemEntities = setReportResult(reportData.getChartDatas(),reportData.getReportResults());
        if(reportItemEntities!=null){
            for(ReportItemEntity entity:reportItemEntities){
                if(entity.getTopic()){
                    topicReports.add(entity);
                }else{
                    addDimensionReport(entity);
                }
            }
        }

        updateHeadView();
        adapter.notifyDataSetChanged();

    }

    //设置对应result
    private List<ReportItemEntity>  setReportResult(List<ReportItemEntity> reportItemEntities,List<ReportResultEntity> reportResultEntities){
        for(int i=0;i<reportItemEntities.size();i++){
            ReportItemEntity reportItemEntity = reportItemEntities.get(i);
            for(int j=0;j<reportResultEntities.size();j++){
                ReportResultEntity reportResultEntity = reportResultEntities.get(j);
                if(reportItemEntity.getChartItemId().equals(reportResultEntity.getRelationId())){
                    if(reportItemEntity.getReportResult() == null){
                        reportItemEntity.setReportResult(reportResultEntity);
                    }
                }
            }
        }
        return reportItemEntities;
    }

    private void addDimensionReport(ReportItemEntity entity){
        for(int i=0;i<dimensionReports.size();i++){
            List<ReportItemEntity> list = dimensionReports.get(i);
            if(list!=null && list.size()>0){
                if(list.get(0).getChartItemId().equals(entity.getChartItemId())){
                    list.add(entity);
                    return;
                }
            }
        }
        List<ReportItemEntity> newList = new ArrayList<>();
        newList.add(entity);
        dimensionReports.add(newList);
    }

    private void loadReportData(TopicInfoEntity entity){

        DataRequestService.getInstance().getTopicReportByRelation(ChildInfoDao.getDefaultChildId(),
                entity.getExamId(),
                entity.getTopicId(),
                ChartViewHelper.REPORT_RELATION_TOPIC,
                "0", new BaseService.ServiceCallback() {
                    @Override
                    public void onFailure(QSCustomException e) {
                        rtNoReport.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onResponse(Object obj) {
                        if (obj != null) {
                            LogUtils.w("topic_report:", obj.toString());
                            Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                            ReportRootEntity data = InjectionWrapperUtil.injectMap(dataMap, ReportRootEntity.class);
                            if (data != null) {
                                rtNoReport.setVisibility(View.GONE);
                                initReportData(data);
                            }else {
                                rtNoReport.setVisibility(View.VISIBLE);
                            }
                        }else{
                            rtNoReport.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }
}
