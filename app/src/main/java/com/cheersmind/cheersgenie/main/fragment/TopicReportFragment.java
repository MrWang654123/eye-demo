package com.cheersmind.cheersgenie.main.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.adapter.ReportRootAdapter;
import com.cheersmind.cheersgenie.main.dao.ChildInfoDao;
import com.cheersmind.cheersgenie.main.entity.ReportItemEntity;
import com.cheersmind.cheersgenie.main.entity.ReportResultEntity;
import com.cheersmind.cheersgenie.main.entity.ReportRootEntity;
import com.cheersmind.cheersgenie.main.entity.TopicInfoEntity;
import com.cheersmind.cheersgenie.main.helper.MPChartViewHelper;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.DensityUtil;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.LogUtils;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;
import com.cheersmind.cheersgenie.main.view.CustomReportViewPager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/6/14.
 */

public class TopicReportFragment extends Fragment {

    public final static String TOPIC_REPORT = "topic_report";
    private final int REPORT_TYPE_COUNTYR = 5555;
    private final int REPORT_TYPE_CLASS = 5556;
    private final int REPORT_TYPE_GRADE = 5557;

    private View contentView;
    private RelativeLayout rtReport;
    private RelativeLayout rtNoReport;
    private RelativeLayout rtNoneTopicReport;
    private LinearLayout llChart;
//    private LinearLayout stageLayout;
    private LinearLayout llDimension;
    private ImageView ivCountry;
    private ImageView ivClass;
    private ImageView ivGrade;
    private CustomReportViewPager vpDimension;

    List<Fragment> dimensionReportFragments = new ArrayList<>();
    private TopicInfoEntity topicInfoEntity;
    private ReportRootEntity reportData;
    List<ReportItemEntity> topicReports = new ArrayList<>();
    List<List<ReportItemEntity>> dimensionReports = new ArrayList<>();//每个量表可能不只一个图表
//    List<ReportResultEntity> reportResultEntities = new ArrayList<>();

    private int curDimensionIndex;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(contentView == null){
            contentView = inflater.inflate(R.layout.qs_fragment_topic_report, container,
                    false);
        }

        initView();
        initData();
        return contentView;
    }

    private void initView(){
        rtNoReport = (RelativeLayout)contentView.findViewById(R.id.rt_no_report);
        rtNoneTopicReport = (RelativeLayout)contentView.findViewById(R.id.rt_none_report);
        rtReport = (RelativeLayout)contentView.findViewById(R.id.rt_report);
        llChart = (LinearLayout) contentView.findViewById(R.id.ll_char);
        ivCountry = (ImageView)contentView.findViewById(R.id.iv_country);
        ivClass = (ImageView)contentView.findViewById(R.id.iv_class);
        ivGrade = (ImageView)contentView.findViewById(R.id.iv_grade);
        ivCountry.setOnClickListener(onMultiClickListener);
        ivClass.setOnClickListener(onMultiClickListener);
        ivGrade.setOnClickListener(onMultiClickListener);

        llDimension = (LinearLayout)contentView.findViewById(R.id.ll_dimension);
    }

    private void initData(){
        Bundle bundle = getArguments();
        if(bundle!=null){
            topicInfoEntity = (TopicInfoEntity) bundle.getSerializable(TOPIC_REPORT);
        }

        if(topicInfoEntity == null || topicInfoEntity.getChildTopic() == null){
            rtReport.setVisibility(View.GONE);
//            vpDimension.setVisibility(View.GONE);
            llDimension.setVisibility(View.GONE);
            rtNoReport.setVisibility(View.VISIBLE);
            return;
        }

        loadReportData(topicInfoEntity);
    }

    private void initReportVisiable(){
        if(topicReports!=null && topicReports.size()>0
                && topicReports.get(0).getItems()!=null && topicReports.get(0).getItems().size()>0){
            rtReport.setVisibility(View.VISIBLE);
            rtNoReport.setVisibility(View.GONE);
            rtNoneTopicReport.setVisibility(View.GONE);
        }else{
            rtReport.setVisibility(View.GONE);
            rtNoReport.setVisibility(View.VISIBLE);
            rtNoneTopicReport.setVisibility(View.VISIBLE);
        }

        if(dimensionReports!=null && dimensionReports.size()>0
                && dimensionReports.get(0).get(0).getItems()!=null
                && dimensionReports.get(0).get(0).getItems().size()>0){
            vpDimension.setVisibility(View.VISIBLE);
        }else{
            vpDimension.setVisibility(View.GONE);
        }

        if(topicReports!=null && topicReports.size()>0){
            ReportItemEntity reportItemEntity = topicReports.get(0);
            if(topicReports.size()>1){
                for(int i=0;i<topicReports.size();i++){
                    if(i>0){
                        reportItemEntity.getItems().addAll(topicReports.get(i).getItems());
                    }
                }
            }
            if(reportItemEntity.getItems()!=null && reportItemEntity.getItems().size()>0){
                llChart.removeAllViews();
                List<ReportItemEntity> reportItemEntities = new ArrayList<>();
                reportItemEntities.add(reportItemEntity);
                MPChartViewHelper.addMpChartView(getActivity(), llChart, reportItemEntities);
            }
        }
    }

    private void initDimensionVp(){
        llDimension.removeAllViews();
        View dvpView = View.inflate(getActivity(),R.layout.qs_fragment_dimension_viewpage,null);
        vpDimension = (CustomReportViewPager)dvpView.findViewById(R.id.vp_dimension);
        llDimension.addView(dvpView);
        vpDimension.removeAllViewsInLayout();
        vpDimension.setOffscreenPageLimit(dimensionReports.size()-1);
        vpDimension.setPageMargin(DensityUtil.dip2px(getActivity(),20));
        dimensionReportFragments.clear();
        for(int i=0;i<dimensionReports.size();i++){
            DimensionReportFragment dimensionReportFragment = new DimensionReportFragment();
            Bundle bundleDim = new Bundle();
            bundleDim.putSerializable(DimensionReportFragment.DIMENSION_REPORT_DATA_KEY, (Serializable) dimensionReports.get(i));
            bundleDim.putString(DimensionReportFragment.DIMENSION_REPORT_STAGE_KEY,String.valueOf(i+1));
            dimensionReportFragment.setArguments(bundleDim);
            dimensionReportFragments.add(dimensionReportFragment);
        }
        vpDimension.setAdapter(new ReportRootAdapter(getActivity(),getChildFragmentManager(), (ArrayList<Fragment>) dimensionReportFragments));
        vpDimension.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                curDimensionIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
//        if(dimensionReportFragments.size()==0){
//            vpDimension.removeAllViewsInLayout();
//            vpDimension.setOffscreenPageLimit(dimensionReports.size());
//            vpDimension.setPageMargin(DensityUtil.dip2px(getActivity(),20));
//            for(int i=0;i<dimensionReports.size();i++){
//                DimensionReportFragment dimensionReportFragment = new DimensionReportFragment();
//                Bundle bundleDim = new Bundle();
//                bundleDim.putSerializable(DimensionReportFragment.DIMENSION_REPORT_DATA_KEY, (Serializable) dimensionReports.get(i));
//                bundleDim.putString(DimensionReportFragment.DIMENSION_REPORT_STAGE_KEY,String.valueOf(i+1));
//                dimensionReportFragment.setArguments(bundleDim);
//                dimensionReportFragments.add(dimensionReportFragment);
//            }
//            vpDimension.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
//                @Override
//                public Fragment getItem(int position) {
//                    return dimensionReportFragments.get(position);
//                }
//
//                @Override
//                public int getCount() {
//                    return dimensionReportFragments.size();
//                }
//            });
//            vpDimension.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//                @Override
//                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//                }
//
//                @Override
//                public void onPageSelected(int position) {
//                    curDimensionIndex = position;
//                    curChart.updateChart(curDimensionIndex);
//                }
//
//                @Override
//                public void onPageScrollStateChanged(int state) {
//
//                }
//            });
//        }else{
//            for(int i=0;i<dimensionReportFragments.size();i++){
//                DimensionReportFragment dimensionReportFragment = (DimensionReportFragment)dimensionReportFragments.get(i);
//                if(dimensionReportFragment!=null){
//                    dimensionReportFragment.updateData(dimensionReports.get(i));
//                }
//            }
//        }

    }

    private void updateReportTypeBg(int selectType){
        if(selectType == REPORT_TYPE_COUNTYR){
            ivCountry.setBackgroundResource(R.mipmap.qs_report_country_select);
            ivClass.setBackgroundResource(R.mipmap.qs_report_class_nor);
            ivGrade.setBackgroundResource(R.mipmap.qs_report_grade_nor);
        }else if(selectType == REPORT_TYPE_CLASS){
            ivCountry.setBackgroundResource(R.mipmap.qs_report_country_nor);
            ivClass.setBackgroundResource(R.mipmap.qs_report_class_select);
            ivGrade.setBackgroundResource(R.mipmap.qs_report_grade_nor);
        }else if(selectType == REPORT_TYPE_GRADE){
            ivCountry.setBackgroundResource(R.mipmap.qs_report_country_nor);
            ivClass.setBackgroundResource(R.mipmap.qs_report_class_nor);
            ivGrade.setBackgroundResource(R.mipmap.qs_report_grade_select);
        }
    }

    private OnMultiClickListener onMultiClickListener = new OnMultiClickListener() {
        @Override
        public void onMultiClick(View view) {
            if(view == ivCountry){
                updateReportTypeBg(REPORT_TYPE_COUNTYR);
            }else if(view == ivClass){
                updateReportTypeBg(REPORT_TYPE_CLASS);
            }else if(view == ivGrade){
                updateReportTypeBg(REPORT_TYPE_GRADE);
            }
        }
    };

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

        initDimensionVp();
        initReportVisiable();
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
                entity.getChildTopic().getExamId(),
                entity.getTopicId(),
                MPChartViewHelper.REPORT_RELATION_TOPIC,
                "0", new BaseService.ServiceCallback() {
                    @Override
                    public void onFailure(QSCustomException e) {
//                        ToastUtil.showShort(getActivity(),"获取报告失败");
                    }

                    @Override
                    public void onResponse(Object obj) {
                        if(obj!=null){
                            LogUtils.w("topic_report:",obj.toString());
                            Map dataMap = JsonUtil.fromJson(obj.toString(),Map.class);
                            ReportRootEntity data = InjectionWrapperUtil.injectMap(dataMap,ReportRootEntity.class);
                            if(data!=null){
                                initReportData(data);
                            }
                        }
                    }
                });
    }

    public void updateData(){
        loadReportData(topicInfoEntity);
    }
}
