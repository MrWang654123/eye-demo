package com.cheersmind.cheersgenie.features.modules.exam.fragment;


import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cheersmind.cheersgenie.R;
import com.cheersmind.cheersgenie.features.adapter.HistoryReportRecyclerAdapter;
import com.cheersmind.cheersgenie.features.constant.Dictionary;
import com.cheersmind.cheersgenie.features.modules.base.fragment.LazyLoadFragment;
import com.cheersmind.cheersgenie.features.view.XEmptyLayout;
import com.cheersmind.cheersgenie.features.view.dialog.TopicReportDialog;
import com.cheersmind.cheersgenie.main.Exception.QSCustomException;
import com.cheersmind.cheersgenie.main.entity.HistoryReportItemEntity;
import com.cheersmind.cheersgenie.main.entity.HistoryReportRootEntity;
import com.cheersmind.cheersgenie.main.entity.ReportItemEntity;
import com.cheersmind.cheersgenie.main.entity.ReportResultEntity;
import com.cheersmind.cheersgenie.main.entity.ReportRootEntity;
import com.cheersmind.cheersgenie.main.entity.SimpleArticleEntity;
import com.cheersmind.cheersgenie.main.entity.TopicInfoEntity;
import com.cheersmind.cheersgenie.main.service.BaseService;
import com.cheersmind.cheersgenie.main.service.DataRequestService;
import com.cheersmind.cheersgenie.main.util.InjectionWrapperUtil;
import com.cheersmind.cheersgenie.main.util.JsonUtil;
import com.cheersmind.cheersgenie.main.util.OnMultiClickListener;
import com.cheersmind.cheersgenie.main.util.ToastUtil;
import com.cheersmind.cheersgenie.module.login.UCManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 历史报告
 */
public class HistoryReportFragment extends LazyLoadFragment {

    private static final String TOPIC_INFO = "topic_info";
    //话题
    private TopicInfoEntity topicInfo;

    @BindView(R.id.recycleView)
    RecyclerView recycleView;
    @BindView(R.id.emptyLayout)
    XEmptyLayout emptyLayout;

    Unbinder unbinder;

    //适配器的数据列表
    List<HistoryReportItemEntity> recyclerItem = new ArrayList<>();
    //适配器
    private HistoryReportRecyclerAdapter recyclerAdapter;

    //recycler子项的孩子的点击监听
    BaseQuickAdapter.OnItemChildClickListener recyclerItemChildClickListener = new BaseQuickAdapter.OnItemChildClickListener() {

        @Override
        public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
            switch (view.getId()) {
                //查看
                case R.id.tv_goto_detail: {
                    HistoryReportItemEntity historyReportItemEntity = recyclerItem.get(position);
                    String childExamId = historyReportItemEntity.getChildExamId();
                    topicInfo.getChildTopic().setChildExamId(childExamId);
//                    ToastUtil.showShort(getContext(), "点击查看 " + (position + 1));
                    try {
                        new TopicReportDialog().setTopicInfo(topicInfo).show(getChildFragmentManager(), "报告");
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtil.showShort(getContext(), e.getMessage());
                    }
                    break;
                }
            }
        }
    };


    @Override
    protected int setContentView() {
        return R.layout.fragment_history_report;
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
                loadHistoryReport();
            }
        });

        //适配器
        recyclerAdapter = new HistoryReportRecyclerAdapter(recyclerItem);
        recycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        recycleView.setAdapter(recyclerAdapter);
        //子项孩子的点击监听
        recyclerAdapter.setOnItemChildClickListener(recyclerItemChildClickListener);
        //添加自定义分割线
        DividerItemDecoration divider = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getContext(),R.drawable.recycler_divider_line));
        recycleView.addItemDecoration(divider);

    }

    @Override
    protected void lazyLoad() {
        loadHistoryReport();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * 加载历史报告
     */
    private void loadHistoryReport() {
        //通信等待提示
        emptyLayout.setErrorType(XEmptyLayout.NETWORK_LOADING);

        String defaultChildId = UCManager.getInstance().getDefaultChild().getChildId();
        DataRequestService.getInstance().getHistoryReport(
                topicInfo.getTopicId(),
                defaultChildId,
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
                            HistoryReportRootEntity data = InjectionWrapperUtil.injectMap(dataMap, HistoryReportRootEntity.class);

                            if (data == null || data.getItems().size() == 0) {
                                //空布局：无数据
                                emptyLayout.setErrorType(XEmptyLayout.NODATA);
                                return;
                            }

                            //历史报告项集合
                            recyclerItem = data.getItems();
                            recyclerItem.add(0, new HistoryReportItemEntity(HistoryReportItemEntity.HEAD));
                            recyclerAdapter.setNewData(recyclerItem);
//                            recyclerAdapter.notifyDataSetChanged();

                        } catch (Exception e) {
                            e.printStackTrace();
                            //空布局：加载失败
                            emptyLayout.setErrorType(XEmptyLayout.NODATA_ENABLE_CLICK);
                        }
                    }
                });
    }

}
