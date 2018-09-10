package com.cheersmind.cheersgenie.features.modules.exam.fragment;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.adapter.ReportRecyclerAdapter;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.entity.ReportItemEntity;
import com.cheersmind.cheersgenie.main.entity.ReportResultEntity;
import com.cheersmind.cheersgenie.main.entity.ReportRootEntity;
import com.cheersmind.cheersgenie.main.entity.TopicInfoEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;
import com.cheersmind.cheersgenie.main.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 最新报告
 */
public class LastReportFragment extends LazyLoadFragment {

    private static final String TOPIC_INFO = "topic_info";
    //话题
    private TopicInfoEntity topicInfo;

    //报告列表
    @BindView(R.id.recycleView)
    RecyclerView recycleView;

    @BindView(R.id.emptyLayout)
    XEmptyLayout emptyLayout;

    Unbinder unbinder;

    //报告列表
    List<List<ReportItemEntity>> recyclerItem = new ArrayList<>();//每个报告可能不只一个图表
    //适配器
    private ReportRecyclerAdapter recyclerAdapter;


    @Override
    protected int setContentView() {
        return R.layout.fragment_last_report;
    }

    @Override
    protected void onInitView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);

        try {
            Bundle bundle = getArguments();
            if (bundle != null) {
                topicInfo = (TopicInfoEntity) bundle.getSerializable(TOPIC_INFO);
                if (topicInfo == null) {
                    throw new Exception();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showShort(getContext(), "数据传递有误");
            getActivity().finish();
        }

        //重载监听
        emptyLayout.setOnLayoutClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View view) {
                //加载报告
                loadReport();
            }
        });

        //适配器
        recyclerAdapter = new ReportRecyclerAdapter(getContext(), R.layout.recycleritem_report, recyclerItem);
        recyclerAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        recycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        recycleView.setAdapter(recyclerAdapter);
        //添加自定义分割线
        DividerItemDecoration divider = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getContext(),R.drawable.recycler_divider_custom));
        recycleView.addItemDecoration(divider);

    }

    @Override
    protected void lazyLoad() {
        //加载报告
        loadReport();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    /**
     * 加载报告
     */
    private void loadReport() {
        //通信等待提示
        emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);

        DataRequestService.getInstance().getReportV2(
                topicInfo.getChildTopic().getChildExamId(),
                topicInfo.getTopicId(),
                Dictionary.REPORT_TYPE_TOPIC,
                Dictionary.REPORT_COMPARE_AREA_COUNTRY,
                new BaseService.ServiceCallback() {
                    @Override
                    public void onFailure(QSCustomException e) {
                        //空布局：网络连接有误，或者请求失败
                        emptyLayout.setErrorType(XEmptyLayout.NETWORK_ERROR);
                    }

                    @Override
                    public void onResponse(Object obj) {
                        //空布局：隐藏
                        emptyLayout.setErrorType(XEmptyLayout.HIDE_LAYOUT);

                        try {
                            Map dataMap = JsonUtil.fromJson(obj.toString(), Map.class);
                            ReportRootEntity data = InjectionWrapperUtil.injectMap(dataMap, ReportRootEntity.class);

                            if (data == null || (data.getChartDatas().size() == 0 && data.getReportResults().size() == 0)) {
                                //空布局：无数据
                                emptyLayout.setErrorType(XEmptyLayout.NODATA);
                                return;
                            }

                            //报告结果
                            List<ReportResultEntity> reportResultEntities = data.getReportResults();
                            //报告图表数据
                            List<ReportItemEntity> reportItems = data.getChartDatas();
                            //把报告结果置于报告图表对象中
                            reportItems = settingResultToReportItem(reportItems, reportResultEntities);
                            //把reportItems分组，每个量表可能不只一个图表
                            recyclerItem = groupReportItem(reportItems);

                            //目前每次都是重置列表数据
                            recyclerAdapter.setNewData(recyclerItem);

                        } catch (Exception e) {
                            e.printStackTrace();
                            //空布局：加载失败
                            emptyLayout.setErrorType(XEmptyLayout.NODATA_ENABLE_CLICK);
                        }
                    }
                });
    }

    /**
     * 把reportItems分组，每个报告可能不只一个图表
     *
     * @param reportItems
     * @return
     */
    private List<List<ReportItemEntity>> groupReportItem(List<ReportItemEntity> reportItems) {
        List<List<ReportItemEntity>> topicReportList = new ArrayList<>();
        List<List<ReportItemEntity>> dimensionReportList = new ArrayList<>();

        for(ReportItemEntity reportItem : reportItems){
            //话题
            if(reportItem.getTopic()){
                //话题报告
                addDimensionReport(topicReportList, reportItem);
            }else{
                //量表报告
                addDimensionReport(dimensionReportList, reportItem);
            }
        }

        //话题报告在前
        topicReportList.addAll(dimensionReportList);

        return topicReportList;
    }

    /**
     * 添加报告到报告集合中
     * @param reportList 报告集合
     * @param reportItem 报告对象
     */
    private void addDimensionReport(List<List<ReportItemEntity>> reportList, ReportItemEntity reportItem){
        //是否已存在
        boolean exist = false;

        for(int i=0;i<reportList.size();i++){
            List<ReportItemEntity> list = reportList.get(i);
            if(list!=null && list.size()>0){
                if(list.get(0).getChartItemId().equals(reportItem.getChartItemId())){
                    list.add(reportItem);
                    //标记已存在
                    exist = true;
                    break;
                }
            }
        }

        //未存在，则新建
        if (!exist) {
            List<ReportItemEntity> newList = new ArrayList<>();
            newList.add(reportItem);
            reportList.add(newList);
        }
    }

//    private List<List<ReportItemEntity>> groupReportItem(List<ReportItemEntity> reportItems) {
//        ArrayMap<String, List<ReportItemEntity>> reportArrayMap = new ArrayMap<>();
//
//        //目前暂时不区分是不是topic报告
//        for (ReportItemEntity reportItem : reportItems) {
//            //是否已经存在
//            if (reportArrayMap.containsKey(reportItem.getChartItemId())) {
//                reportArrayMap.get(reportItem.getChartItemId()).add(reportItem);
//
//            } else {
//                //不存在则新建
//                List<ReportItemEntity> newReport = new ArrayList<>();
//                newReport.add(reportItem);
//                reportArrayMap.put(reportItem.getChartItemId(), newReport);
//            }
//        }
//
//        //转成ArrayList
//        List<List<ReportItemEntity>> reportList = new ArrayList<>();
//        Iterator<Map.Entry<String, List<ReportItemEntity>>> iterator = reportArrayMap.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Map.Entry<String, List<ReportItemEntity>> next = iterator.next();
//            reportList.add(next.getValue());
//        }
//
//        return reportList;
//    }


    /**
     * 把ReportResult设置到对应的ReportItem中
     *
     * @param reportItemEntities   图表项集合
     * @param reportResultEntities 结果项集合
     * @return
     */
    private List<ReportItemEntity> settingResultToReportItem(List<ReportItemEntity> reportItemEntities, List<ReportResultEntity> reportResultEntities) {
        //图表项
        for (int i = 0; i < reportItemEntities.size(); i++) {
            ReportItemEntity reportItemEntity = reportItemEntities.get(i);
            //结果项
            for (int j = 0; j < reportResultEntities.size(); j++) {
                ReportResultEntity reportResultEntity = reportResultEntities.get(j);
                //判等：chart_item_id和relationId
                if (reportItemEntity.getChartItemId().equals(reportResultEntity.getRelationId())) {
                    //图表项中的reportResult为空才赋值
                    if (reportItemEntity.getReportResult() == null) {
                        reportItemEntity.setReportResult(reportResultEntity);
                    }
                }
            }
        }
        return reportItemEntities;
    }


}
